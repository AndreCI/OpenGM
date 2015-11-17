package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static ch.epfl.sweng.opengm.parse.PFGroup.createNewGroup;
import static ch.epfl.sweng.opengm.parse.PFUser.createNewUser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

public class CreateEditEventTest extends ActivityInstrumentationTestCase2<CreateEditEventActivity> {
    private static final String PHONE_NUMBER = "0123456789";

    public CreateEditEventTest() {
        super(CreateEditEventActivity.class);
    }

    private final String EMAIL = "bobby.lapointe@caramail.co.uk";
    private final String USERNAME = "CreateEditEventTest";
    private final String FIRST_NAME = "Bobby";
    private final String LAST_NAME = "LaPointe";

    private PFEvent e;
    private PFGroup group;
    private PFUser user;
    private String id;

    @Before
    public void newIds() {
        e = null;
        group = null;
        id = null;
        user = null;
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testNoName() {
        CreateEditEventActivity act = getActivity();
        ViewInteraction nameText = onView(withId(R.id.CreateEditEventNameText));
        nameText.perform(clearText());
        onView(withId(R.id.CreateEditOkButton)).perform(click());
        nameText.check(matches(hasErrorText(act.getString(R.string.CreateEditEmptyNameErrorMessage))));
    }

    public void testNameButNoTime() {
        CreateEditEventActivity act = getActivity();
        onView(withId(R.id.CreateEditEventNameText)).perform(typeText("testName"));
        closeSoftKeyboard();
        onView(withId(R.id.CreateEditOkButton)).perform(click());
        Button timeButton = (Button) act.findViewById(R.id.CreateEditEventTimeText);
        Button dateButton = (Button) act.findViewById(R.id.CreateEditEventDateText);
        assertEquals("", timeButton.getError());
        assertEquals("", dateButton.getError());
        onView(withText(R.string.CreateEditEmptyTimeDateErrorMessage)).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    public void testEventInIntent() throws PFException {
        id = getRandomId();
        String name = "testEventInIntent";
        String description = "A group, much nicer than the previous one";
        try {
            user = createNewUser(id, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
            group = createNewGroup(user, name, description, null);
        } catch (PFException e) {
            e.printStackTrace();
            Assert.fail("Network error");
        }
        Intent i = new Intent();
        e = PFEvent.createEvent(group, "testName", "testPlace", new Date(2000, 0, 1, 10, 10), new ArrayList<PFMember>(), "testDescription", null);
        i.putExtra(Utils.EVENT_INTENT_MESSAGE, e);
        setActivityIntent(i);
        CreateEditEventActivity act = getActivity();
        onView(withId(R.id.CreateEditEventNameText)).check(matches(withText("testName")));
        onView(withId(R.id.CreateEditEventPlaceText)).check(matches(withText("testPlace")));
        assertEquals("10 : 10", ((Button) act.findViewById(R.id.CreateEditEventTimeText)).getText());
        assertEquals("1/01/2000", ((Button) act.findViewById(R.id.CreateEditEventDateText)).getText());
        onView(withId(R.id.CreateEditEventDescriptionText)).check(matches(withText("testDescription")));
    }

    public void testNoParticipants() throws PFException {
        id = getRandomId();
        String name = "testNoParticipants";
        String description = "A group, much nicer than the previous one";
        try {
            user = createNewUser(id, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
            group = createNewGroup(user, name, description, null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        Intent i = new Intent();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        Date date = new Date(year, month + 1, day, hour, min);
        e = PFEvent.createEvent(group, "testName", "testPlace", date, new ArrayList<PFMember>(), "testDescription", null);
        i.putExtra(Utils.EVENT_INTENT_MESSAGE, e);
        setActivityIntent(i);
        getActivity();
        onView(withId(R.id.CreateEditOkButton)).perform(click());
        onView(withText(R.string.CreateEditNoParticipants)).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    public void testCanClickOnBrowseButton() throws PFException{
        id = getRandomId();
        String name = "Really Nice Group to test BrowseButton";
        String description = "A group, much nicer than the previous one";
        try {
            user = createNewUser(id, EMAIL, PHONE_NUMBER, USERNAME, FIRST_NAME, LAST_NAME);
            group = createNewGroup(user, name, description, null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        Intent i = new Intent();
        e = PFEvent.createEvent(group, "testName", "testPlace", new Date(2000, 0, 1, 10, 10), new ArrayList<PFMember>(), "testDescription", null);
        i.putExtra(Utils.EVENT_INTENT_MESSAGE, e);
        setActivityIntent(i);
        onView(withId(R.id.CreateEditEventBitmapBrowseButton)).perform(click());
    }

    public void NoTestImageNameChanged() throws PFException{
        id = getRandomId();
        String name = "Really Nice Group to test BrowseButton";
        String description = "A group, much nicer than the previous one";
        try {
            user = createNewUser(id, EMAIL, PHONE_NUMBER, USERNAME, FIRST_NAME, LAST_NAME);
            group = createNewGroup(user, name, description, null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        Intent i = new Intent();
        e = PFEvent.createEvent(group, "testName", "testPlace", new Date(2000, 0, 1, 10, 10), new ArrayList<PFMember>(), "testDescription", null);
        i.putExtra(Utils.EVENT_INTENT_MESSAGE, e);
        setActivityIntent(i);
        CreateEditEventActivity a = getActivity();
        Intent data = null;
        a.onActivityResult(CreateEditEventActivity.CREATE_EDIT_EVENT_RESULT_CODE_BROWSEFORBITMAP,
                Activity.RESULT_OK, data);
    }

    @After
    public void deleteAfterTesting() {
        if (e != null) {
            try {
                e.delete();
            } catch (PFException e1) {
                e1.printStackTrace();
            }
        }
        if (group != null) {
            group.deleteGroup();
        }
        if(user != null) {
            deleteUserWithId(id);
        }
    }
}