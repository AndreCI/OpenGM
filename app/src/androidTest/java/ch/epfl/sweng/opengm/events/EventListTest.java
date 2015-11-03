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

    public void testEventsAreInRightOrder(){
        getActivity();
          //For the purpose of test only.
        //TODO : Not finished yet
        ArrayList<PFEvent> eventList = new ArrayList<>();
        PFEvent tester1 = new PFEvent();
        PFEvent tester2 = new PFEvent();
        PFEvent tester3 = new PFEvent();
        PFEvent tester4 = new PFEvent();
        PFEvent tester5 = new PFEvent();
        PFEvent tester6 = new PFEvent();
        PFEvent tester7 = new PFEvent();
        PFEvent tester8 = new PFEvent();
        PFEvent tester9 = new PFEvent();
        PFEvent tester10 = new PFEvent();
        PFEvent tester11 = new PFEvent();
        tester1.setName("E1");
        tester2.setName("E2");
        tester3.setName("E3");
        tester4.setName("E4");
        tester5.setName("E5");
        tester6.setName("E6");
        tester7.setName("E7");
        tester8.setName("E8");
        tester9.setName("E9");
        tester10.setName("E10");
        tester11.setName("E11");
        tester1.setDate(new Date(1995, 1, 29));
        tester2.setDate(new Date(2000,1,29));
        tester3.setDate(new Date(1999,2,22));
        tester4.setDate(new Date(1994,6,29));
        tester5.setDate(new Date(1995,1,28));
        tester6.setDate(new Date(1995,1,27));
        tester7.setDate(new Date(1995,1,26));
        tester8.setDate(new Date(1995,1,25));
        tester9.setDate(new Date(1995,1,24));
        tester10.setDate(new Date(1995,1,23));
        tester11.setDate(new Date(1995, 1, 22));
        eventList.add(tester1);
        eventList.add(tester2);
        eventList.add(tester3);
        eventList.add(tester4);
        eventList.add(tester5);
        eventList.add(tester6);
        eventList.add(tester7);
        eventList.add(tester8);
        eventList.add(tester9);
        eventList.add(tester10);
        eventList.add(tester11);
        Mockito m = new Mockito();
    }
}
