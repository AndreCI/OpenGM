package ch.epfl.sweng.opengm.groups;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;

import java.util.Calendar;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;

public class GroupsHomeActivityTest extends ActivityInstrumentationTestCase2<GroupsHomeActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();

    private final static String USERNAME = CURRENT_DATE;
    private final static String FIRSTNAME = "Marc";
    private final static String LASTRNAME = "Assin";
    private final static String EMAIL = CURRENT_DATE + "@yolo.ch";

    private GroupsHomeActivity activity;
    private PFUser user;
    private PFGroup group;

    public GroupsHomeActivityTest() {
        super(GroupsHomeActivity.class);
    }

    @Before
    public void setIds() {
        user = null;
        group = null;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        OpenGMApplication.logOut();

        try {
            user = PFUser.createNewUser(CURRENT_DATE, EMAIL, "0", USERNAME, FIRSTNAME, LASTRNAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        Thread.sleep(1000);
        OpenGMApplication.setCurrentUser(user.getId());

        try {
            group = PFGroup.createNewGroup(user, USERNAME, EMAIL, null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        Thread.sleep(2000);

        OpenGMApplication.setCurrentGroup(user.getGroups().size()-1);

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        activity = getActivity();

    }

    public void testIntentAndTextViews() {
        assertEquals(USERNAME, activity.getTitle());
        onView(withId(R.id.textView_description)).check(matches(withText(EMAIL)));
    }

    @AfterClass
    public void tearDown() {
        group.deleteGroup();
        deleteUserWithId(user.getId());
    }

}
