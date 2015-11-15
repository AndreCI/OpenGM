package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;

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

    private PFGroup group;
    private PFUser user;
    private String id;

    @Before
    public void newIds() {
        group = null;
        user = null;
        id = null;
    }

    public void testCanClickAddButton(){
        Intent intent = new Intent();
        id = getRandomId();
        try {
            user = PFUser.createNewUser(id, "testmail", "testusername", "testfirstname", "testlastname");
        } catch (PFException e) {
            e.printStackTrace();
        }
        try {
            group = PFGroup.createNewGroup(user, "testGroup", "testDescription", null);
        } catch (PFException e) {
            e.printStackTrace();
        }
        intent.putExtra(Utils.GROUP_INTENT_MESSAGE, group);
        setActivityIntent(intent);
        getActivity();
        onView(withId(R.id.eventListAddButton)).perform(click());
    }

    @After
    public void clear() {
        if(group != null) {
            group.deleteGroup();
        }
        if(user != null) {
            deleteUserWithId(id);
        }
    }
}
