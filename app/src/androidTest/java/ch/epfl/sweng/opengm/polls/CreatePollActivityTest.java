package ch.epfl.sweng.opengm.polls;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.PickerActions;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.DatePicker;

import junit.framework.Assert;

import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.Date;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.UtilsTest;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFPoll;
import ch.epfl.sweng.opengm.parse.PFUser;
import ch.epfl.sweng.opengm.polls.participants.Participant;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class CreatePollActivityTest extends ActivityInstrumentationTestCase2<CreatePollActivity> {

    public CreatePollActivityTest() {
        super(CreatePollActivity.class);
    }

    private PFUser currentUser;
    private PFGroup currentGroup;
    private PFPoll currentPoll;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        OpenGMApplication.logOut();
        currentUser = PFUser.createNewUser(UtilsTest.getRandomId(), "name", "0123456789", "email", "firstname", "lastname");
        OpenGMApplication.setCurrentUser(currentUser);
        currentGroup = PFGroup.createNewGroup(currentUser, "name", "description", null);
        OpenGMApplication.setCurrentGroup(currentGroup);
        currentPoll = null;
    }

    public void tearDown() throws PFException {
        OpenGMApplication.logOut();
        deleteUserWithId(currentUser.getId());
        if (currentGroup != null)
            currentGroup.deleteGroup();
        if (currentPoll != null)
            currentPoll.delete();
    }

    public void testEmptyName() {
        getActivity();
        onView(withId(R.id.action_validate)).perform(click());
        onView(withId(R.id.namePollEditText)).check(matches(isTextStyleCorrect(getActivity().getString(R.string.empty_name_poll), true)));
    }

    public void testEmptyDescription() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.action_validate)).perform(click());
        onView(withId(R.id.descriptionPollEditText)).check(matches(isTextStyleCorrect(act.getString(R.string.empty_description_poll), true)));
    }

    public void testNoAnswer() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.descriptionPollEditText)).perform(typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.action_validate)).perform(click());
        onView(withText(R.string.less_two_answers_poll))
                .inRoot(withDecorView(not(act.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }


    public void testNotEnoughAnswer() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.descriptionPollEditText)).perform(typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 1"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());

        onView(withId(R.id.action_validate)).perform(click());

        onView(withText(R.string.less_two_answers_poll))
                .inRoot(withDecorView(not(act.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }


    public void testNoDeadline() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.descriptionPollEditText)).perform(typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 1"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 2"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());
        onView(withId(R.id.action_validate)).perform(click());

        onView(withText(R.string.empty_date_poll))
                .inRoot(withDecorView(not(act.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

    }

    public void testIncorrectParticipant() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.descriptionPollEditText)).perform(typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 1"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 2"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());

        onView(withId(R.id.deadlineButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2016, 1, 1));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.action_validate)).perform(click());

        onView(withText(R.string.no_participants_poll))
                .inRoot(withDecorView(not(act.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    public void testNoParticipant() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.descriptionPollEditText)).perform(typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 1"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 2"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());

        onView(withId(R.id.deadlineButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2016, 1, 1));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.participantsButton)).perform(click());

        try {
            PFMember m = PFMember.fetchExistingMember(currentUser.getId());
            Participant p = new Participant(m);
            onView(withTagValue(is((Object) p))).perform(click());
            onView(withId(R.id.action_validate)).perform(click());
            onView(withId(R.id.participantsButton)).perform(click());
            onView(withTagValue(is((Object) p))).perform(click());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        onView(withId(R.id.action_validate)).perform(click());

        onView(withId(R.id.action_validate)).perform(click());

        onView(withText(R.string.no_participants_poll))
                .inRoot(withDecorView(not(act.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }


    public void testNoPossibleAnswer() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.descriptionPollEditText)).perform(typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 1"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 2"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());

        onView(withId(R.id.deadlineButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2016, 1, 1));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.participantsButton)).perform(click());

        try {
            PFMember m = PFMember.fetchExistingMember(currentUser.getId());
            Participant p = new Participant(m);
            onView(withTagValue(is((Object) p))).perform(click());
            onView(withId(R.id.action_validate)).perform(click());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        onView(withId(R.id.action_validate)).perform(click());

        onView(withText(R.string.no_answer_poll))
                .inRoot(withDecorView(not(act.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    public void testCreatePoll() throws InterruptedException {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.descriptionPollEditText)).perform(typeText("description"));
        closeSoftKeyboard();
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 1"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());
        onView(withId(R.id.add_answer_poll)).perform(click());
        onView(withId(R.id.answer_editText)).perform(typeText("Answer 2"));
        closeSoftKeyboard();
        onView(withText(act.getString(R.string.add))).perform(click());

        onView(withId(R.id.deadlineButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2016, 1, 1));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.participantsButton)).perform(click());

        try {
            PFMember m = PFMember.fetchExistingMember(currentUser.getId());
            Participant p = new Participant(m);
            onView(withTagValue(is((Object) p))).perform(click());
            onView(withId(R.id.action_validate)).perform(click());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        onView(withId(R.id.minus_poll_answer)).perform(click());
        onView(withId(R.id.action_validate)).perform(click());
        onView(withText(R.string.no_answer_poll))
                .inRoot(withDecorView(not(act.getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        onView(withId(R.id.plus_poll_answer)).perform(click());

        onView(withId(R.id.action_validate)).perform(click());
        Thread.sleep(3000);

        try {
            PFUser u2 = PFUser.fetchExistingUser(currentUser.getId());
            PFGroup g = PFGroup.fetchExistingGroup(u2.getGroupsIds().get(0));
            PFPoll p = new ArrayList<>(g.getPolls()).get(0);
            assertEquals("blabla", p.getName());
            assertEquals("description", p.getDescription());
            assertEquals(new Date(116, 0, 1), p.getDeadline());
            assertEquals(1, p.getParticipants().size());
            assertEquals(u2.getLastName(), p.getParticipants().get(u2.getId()).getLastName());
            assertEquals(2, p.getAnswers().size());
            assertEquals("Answer 1", p.getAnswers().get(0).getAnswer());
            assertEquals(0, p.getAnswers().get(0).getVotes());
            assertEquals("Answer 2", p.getAnswers().get(1).getAnswer());
            assertEquals(0, p.getAnswers().get(1).getVotes());
            assertFalse(p.getVoters().get(u2.getId()));
        } catch (PFException e) {
            Assert.fail("Network error");
        }

    }

}
