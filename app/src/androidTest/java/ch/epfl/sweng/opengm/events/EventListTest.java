package ch.epfl.sweng.opengm.events;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFMember;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by André on 01/11/2015.
 */
public class EventListTest extends ActivityInstrumentationTestCase2<EventListActivity> {
public EventListTest() {
        super(EventListActivity.class);
        }
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testCanClickAddButton(){
        getActivity();
        onView(withId(R.id.eventListAddButton)).perform(click());
    }
}
