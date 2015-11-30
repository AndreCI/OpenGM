package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;

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
        OpenGMApplication.logOut();
        group = null;
        user = null;
        id = null;
    }

    // TODO : this is not as test as there is any check ;)
    public void notestCanClickAddButton() throws InterruptedException {
        Intent intent = new Intent();
        id = getRandomId();
        try {
            user = PFUser.createNewUser(id, "testmail", "0", "testusername", "EventListTest", "testlastname");
        } catch (PFException e) {
            e.printStackTrace();
        }
        try {
            OpenGMApplication.setCurrentUserWithId(user.getId());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        Thread.sleep(2000);
        try {
            group = PFGroup.createNewGroup(user, "EventListTest", "testDescription", null);
        } catch (PFException e) {
            e.printStackTrace();
        }
        Thread.sleep(2000);
        OpenGMApplication.setCurrentGroup(0);
        setActivityIntent(intent);
        getActivity();
        onView(withId(R.id.eventListAddButton)).perform(click());
    }

    @After
    public void tearDown() {
        if(group != null) {
            group.deleteGroup();
        }
        if(user != null) {
            deleteUserWithId(id);
        }
        OpenGMApplication.logOut();
    }
}
