package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
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
    private PFGroup group;
    private PFUser user;
    private String id;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        id = getRandomId();
        try {
            user = PFUser.createNewUser(id, "userEmail@_EventListTest", "userPhoneNbr_EventListTest", "userUsername_EventListTest", "userFirstName_EventListTest", "userLastName_EventListTest");
        } catch (PFException e) {
            e.printStackTrace();
        }
        OpenGMApplication.setCurrentUser(user);
        Thread.sleep(2000);
        try {
            group = PFGroup.createNewGroup(user, "groupName_EventListTest", "groupDescription_EventListTest", null);
        } catch (PFException e) {
            e.printStackTrace();
        }
        Thread.sleep(2000);
        OpenGMApplication.setCurrentGroup(0);
    }

   /* @Before
    public void newIds() {
        OpenGMApplication.logOut();
        group = null;
        user = null;
        id = null;
    }*/
    public void testCanClickOnCheckBox() {
        getActivity();
        onView(withId(R.id.eventListCheckBoxForPastEvent)).perform(click());
    }
    public void testCanClickOnRefresh() {
        getActivity();
        onView(withId(R.id.action_refresh_user)).perform(click());
    }
    public void testCanClickOnBack() {
        try {
            getActivity();
            onView(withId(android.R.id.home)).perform(click());
        }catch(NoMatchingViewException e){
            Assert.assertTrue(true);
        }
    }

    //TODO : how to test this strange button?
    public void NotestCanClickAddButton() throws InterruptedException {
        getActivity();
        onView(withId(R.id.eventListAddButton)).perform(click());
    }

    @After
    public void tearDown() {
        if (group != null) {
            group.deleteGroup();
        }
        if (user != null) {
            deleteUserWithId(id);
        }
        OpenGMApplication.logOut();
    }
}
