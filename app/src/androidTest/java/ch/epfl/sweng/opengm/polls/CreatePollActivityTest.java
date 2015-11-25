package ch.epfl.sweng.opengm.polls;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.UtilsTest;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFPoll;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;
import static org.hamcrest.Matchers.not;

public class CreatePollActivityTest extends ActivityInstrumentationTestCase2<CreatePollActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();

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
        currentUser = PFUser.createNewUser(UtilsTest.getRandomId(), "name", "0123456789", "email", "firstname", "lastname");
        currentGroup = PFGroup.createNewGroup(currentUser, "name", "description", null);
        currentPoll = null;
    }

    public void testEmptyName() {
        getActivity();
        onView(ViewMatchers.withId(R.id.action_validate)).perform(click());
        onView(withId(R.id.namePollEditText)).check(matches(isTextStyleCorrect(getActivity().getString(R.string.empty_name_poll), true)));
    }

    public void testEmptyDescription() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.action_validate)).perform(click());
        onView(withId(R.id.descriptionPollEditText)).check(matches(isTextStyleCorrect(act.getString(R.string.empty_description_poll), true)));
    }

    public void testNoAnswer() {
        Activity act = getActivity();
        onView(withId(R.id.namePollEditText)).perform(typeText("blabla"));
        closeSoftKeyboard();
        onView(withId(R.id.descriptionPollEditText)).perform(typeText("description"));
        closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.action_validate)).perform(click());
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
        onView(ViewMatchers.withId(R.id.add_answer_poll)).perform(click());
        typeTextIntoFocusedView("Answer 1");

        onView(ViewMatchers.withId(R.id.action_validate)).perform(click());

        onView(withText(R.string.no_answer_poll))
                .inRoot(withDecorView(not(act.getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
