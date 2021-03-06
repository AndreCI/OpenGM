package ch.epfl.sweng.opengm.groups;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;

import java.util.Calendar;
import java.util.List;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentGroup;
import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;
import static ch.epfl.sweng.opengm.OpenGMApplication.logOut;
import static ch.epfl.sweng.opengm.OpenGMApplication.setCurrentGroup;
import static ch.epfl.sweng.opengm.OpenGMApplication.setCurrentUser;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;
import static ch.epfl.sweng.opengm.parse.PFGroup.createNewGroup;

public class CreateEditGroupActivityTest extends ActivityInstrumentationTestCase2<CreateEditGroupActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();
    private final static String USERNAME = CURRENT_DATE;
    private final static String FIRSTNAME = "Chuck";
    private final static String LASTRNAME = "Norris";
    private final static String EMAIL = CURRENT_DATE + "@yolo.ch";
    private final static String GROUPNAME = "Group " + CURRENT_DATE;
    private final static int sNameEdit = R.id.enterGroupName;
    private final static int sDescriptionEdit = R.id.enterGroupDescription;
    private final static int sDoneButton = R.id.doneGroupCreate;
    private PFUser currentUser;
    private PFGroup group;

    public CreateEditGroupActivityTest() {
        super(CreateEditGroupActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        currentUser = PFUser.createNewUser(CURRENT_DATE, EMAIL,"0", USERNAME, FIRSTNAME, LASTRNAME);
        setCurrentUser(currentUser);
        setCurrentGroup(-1);
    }


    public void testDeclinesTooShortName() throws InterruptedException {
        getActivity();
        onView(ViewMatchers.withId(sNameEdit)).perform(typeText("sh"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name is too short", true)));
    }

    public void testDeclinesTooLongName() throws InterruptedException {
        getActivity();
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText("thisisasuperlonggroupnameitsimpossiblesomeonewouldwanttowritesuchalonginfactverylonggroupname"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name is too long", true)));
    }

    public void testDeclinesNameWithBadChars() throws InterruptedException {
        getActivity();
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText("This//is//bad"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name contains illegal characters, only letters, numbers and spaces allowed.", true)));
    }

    public void testDeclinesNameStartingWithSpace() throws InterruptedException {
        getActivity();
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText(" Why would you start with  ?"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name cannot start with a space", true)));
    }

    public void testGoodGroupAddedToDatabase() throws InterruptedException, PFException {
        getActivity();
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText(GROUPNAME));
        onView(withId(sDescriptionEdit)).perform(typeText("Nice Description HELLO"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        Thread.sleep(2000);
        PFUser user2 = null;

        try {
            user2 = PFUser.fetchExistingUser(currentUser.getId());
        } catch (PFException e) {
            Assert.fail("Network fail");
        }

        for (String groupId : user2.getGroupsIds()) {
            user2.fetchGroupWithId(groupId);
        }

        Thread.sleep(2000);

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

    public void testShowsEditInfo() throws PFException {
        getActivityWithIntent();

        onView(withId(sNameEdit)).check(matches(withText("Testing group for edit group")));
        onView(withId(sDescriptionEdit)).check(matches(withText("Nice description bro")));
        onView(withId(R.id.createGroupsMembersButton)).check(matches(isDisplayed()));
    }

    public void testModifiesFields() throws PFException, InterruptedException {
        getActivityWithIntent();

        onView(withId(sNameEdit)).perform(clearText());
        onView(withId(sDescriptionEdit)).perform(clearText());

        onView(withId(sNameEdit)).perform(typeText("Better group name"));
        onView(withId(sDescriptionEdit)).perform(typeText("Better group description"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());

        group = getCurrentGroup();

        assertEquals("Better group name", group.getName());
        assertEquals("Better group description", group.getDescription());
    }

    private void getActivityWithIntent() throws PFException {
        group = createNewGroup(getCurrentUser(), "Testing group for edit group", "Nice description bro", null);
        setCurrentGroup(group);
        currentUser.reload();
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        if (group != null)
            group.deleteGroup();
        deleteUserWithId(currentUser.getId());
        logOut();
        super.tearDown();
    }

}
