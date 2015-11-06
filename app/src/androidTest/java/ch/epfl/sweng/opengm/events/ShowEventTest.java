package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFMember;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


public class ShowEventTest extends ActivityInstrumentationTestCase2<ShowEventActivity>{
    public ShowEventTest() {
        super(ShowEventActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    // TODO : debug it
    public void notestPreFillWithEventInIntent() throws PFException {
        Intent intent = new Intent();
        PFEvent event = PFEvent.createEvent("testName", "testPlace", new Date(1994, 5, 6, 2, 4), new ArrayList<PFMember>(), "testDescription", null);
        intent.putExtra(EventListActivity.EVENT_LIST_MESSAGE_EVENT, event);
        setActivityIntent(intent);
        ShowEventActivity activity = getActivity();
        onView(withId(R.id.ShowEventNameText)).check(matches(withText("testName")));
        onView(withId(R.id.ShowEventPlaceText)).check(matches(withText("testPlace")));
        onView(withId(R.id.ShowEventDescriptionText)).check(matches(withText("Description:\ntestDescription")));
        assertEquals("2 : 04", ((TextView) activity.findViewById(R.id.ShowEventHourText)).getText());
        assertEquals("6/06/1994", ((TextView) activity.findViewById(R.id.ShowEventDateText)).getText());
        onView(withId(R.id.ShowEventParticipants)).check(matches(withText("Participants:")));
    }
}
