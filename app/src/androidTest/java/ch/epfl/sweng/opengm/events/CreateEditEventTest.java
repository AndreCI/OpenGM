package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFMember;

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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

public class CreateEditEventTest extends ActivityInstrumentationTestCase2<CreateEditEventActivity> {
    public CreateEditEventTest() {
        super(CreateEditEventActivity.class);
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

    public  void testNameButNoTime() {
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

    public void testEventInIntent() {
        Intent i = new Intent();
        PFEvent e = new PFEvent("testid","testName", "testPlace", new Date(2000,00,01,10,10), "testDescription", new ArrayList<PFMember>());
        i.putExtra(ShowEventActivity.SHOW_EVENT_MESSAGE_EVENT, e);
        setActivityIntent(i);
        CreateEditEventActivity act = getActivity();
        onView(withId(R.id.CreateEditEventNameText)).check(matches(withText("testName")));
        onView(withId(R.id.CreateEditEventPlaceText)).check(matches(withText("testPlace")));
        assertEquals("10 : 10", ((Button) act.findViewById(R.id.CreateEditEventTimeText)).getText());
        assertEquals("1/01/2000", ((Button) act.findViewById(R.id.CreateEditEventDateText)).getText());
        onView(withId(R.id.CreateEditEventDescriptionText)).check(matches(withText("testDescription")));
    }

    // TODO : debug it
    public void notestNoParticipants() {
        Intent i = new Intent();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        Date date = new Date(year, month+1, day, hour, min);
        PFEvent e = new PFEvent("testid","testName", "testPlace", date, "testDescription", new ArrayList<PFMember>());
        i.putExtra(ShowEventActivity.SHOW_EVENT_MESSAGE_EVENT, e);
        setActivityIntent(i);
        getActivity();
        onView(withId(R.id.CreateEditOkButton)).perform(click());
        onView(withText(R.string.CreateEditNoParticipants)).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}