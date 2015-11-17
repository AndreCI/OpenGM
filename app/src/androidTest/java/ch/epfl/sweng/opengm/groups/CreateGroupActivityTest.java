package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;

import java.util.Calendar;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;

public class CreateGroupActivityTest extends ActivityInstrumentationTestCase2<CreateGroupActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();
    private final static String USERNAME = CURRENT_DATE;
    private final static String FIRSTNAME = "Chuck";
    private final static String LASTRNAME = "Norris";
    private final static String EMAIL = CURRENT_DATE + "@yolo.ch";
    private final static String GROUPNAME = "Group " + CURRENT_DATE;
    private final static int sNameEdit = R.id.enterGroupName;
    private final static int sDescriptionEdit = R.id.enterGroupDescription;
    private final static int sDoneButton = R.id.doneGroupCreate;
    private Activity createGroupActivity;
    private PFUser currentUser;
    private PFGroup group;

    public CreateGroupActivityTest() {
        super(CreateGroupActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        createGroupActivity = getActivity();
        currentUser = PFUser.createNewUser(CURRENT_DATE, EMAIL,"0", USERNAME, FIRSTNAME, LASTRNAME);

        OpenGMApplication.setCurrentUser(currentUser.getId());
    }


    public void testDeclinesTooShortName() throws InterruptedException {
        onView(ViewMatchers.withId(sNameEdit)).perform(typeText("sh"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name is too short", true)));
    }

    public void testDeclinesTooLongName() throws InterruptedException {
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText("thisisasuperlonggroupnameitsimpossiblesomeonewouldwanttowritesuchalonginfactverylonggroupname"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name is too long", true)));
    }

    public void testDeclinesNameWithBadChars() throws InterruptedException {
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText("This//is//bad"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name contains illegal characters, only letters, numbers and spaces allowed.", true)));
    }

    public void testDeclinesNameStartingWithSpace() throws InterruptedException {
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText(" Why would you start with  ?"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name cannot start with a space", true)));
    }

    public void testGoodGroupAddedToDatabase() throws InterruptedException, PFException {
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText(GROUPNAME));
        onView(withId(sDescriptionEdit)).perform(typeText("Nice Description HELLO"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());

        PFUser user2 = null;

        try {
            user2 = PFUser.fetchExistingUser(currentUser.getId());
        } catch (PFException e) {
            Assert.fail("Network fail");
        }

        Thread.sleep(1000);

        List<PFGroup> groups = user2.getGroups();

        boolean found = false;
        for (PFGroup group : groups) {
            if (group.getName().equals(GROUPNAME)) {
                found = true;
                this.group = group;
            }
        }
        assertTrue(found);
    }

    public void tearDown() {
        if (group != null)
            group.deleteGroup();
        deleteUserWithId(currentUser.getId());
    }

}
