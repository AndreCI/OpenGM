package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;

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
    private Activity createGroupActivity;
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

        OpenGMApplication.setCurrentUser(currentUser.getId());
    }


    public void testDeclinesTooShortName() throws InterruptedException {
        createGroupActivity = getActivity();
        onView(ViewMatchers.withId(sNameEdit)).perform(typeText("sh"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name is too short", true)));
    }

    public void testDeclinesTooLongName() throws InterruptedException {
        createGroupActivity = getActivity();
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText("thisisasuperlonggroupnameitsimpossiblesomeonewouldwanttowritesuchalonginfactverylonggroupname"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name is too long", true)));
    }

    public void testDeclinesNameWithBadChars() throws InterruptedException {
        createGroupActivity = getActivity();
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText("This//is//bad"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name contains illegal characters, only letters, numbers and spaces allowed.", true)));
    }

    public void testDeclinesNameStartingWithSpace() throws InterruptedException {
        createGroupActivity = getActivity();
        onView(withId(sNameEdit)).perform(clearText()).perform(typeText(" Why would you start with  ?"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());
        onView(withId(sNameEdit)).check(matches(isTextStyleCorrect("Group name cannot start with a space", true)));
    }

    public void testGoodGroupAddedToDatabase() throws InterruptedException, PFException {
        createGroupActivity = getActivity();
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

    public void testShowsEditInfo() throws PFException {
        getActivityWithIntent();

        onView(withId(sNameEdit)).check(matches(withText("Testing group for edit group")));
        onView(withId(sDescriptionEdit)).check(matches(withText("Nice description bro")));
        onView(withId(R.id.createGroupsMembersButton)).check(matches(isDisplayed()));
    }

    public void testModifiesFiels() throws PFException, InterruptedException {
        getActivityWithIntent();

        onView(withId(sNameEdit)).perform(clearText());
        onView(withId(sDescriptionEdit)).perform(clearText());

        onView(withId(sNameEdit)).perform(typeText("Better group name"));
        onView(withId(sDescriptionEdit)).perform(typeText("Better group description"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(sDoneButton)).perform(click());

        currentUser = OpenGMApplication.getCurrentUser();

        assertEquals("Better group name", currentUser.getGroups().get(0).getName());
        assertEquals("Better group description", currentUser.getGroups().get(0).getDescription());
    }

    private void getActivityWithIntent() throws PFException {
        Intent intent = new Intent();
        intent.putExtra(CreateEditGroupActivity.GROUP_INDEX, 0);
        group = PFGroup.createNewGroup(OpenGMApplication.getCurrentUser(), "Testing group for edit group", "Nice description bro", null);
        currentUser.reload();
        //OpenGMApplication.getCurrentUser().reload();
        setActivityIntent(intent);
        createGroupActivity = getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        if (group != null)
            group.deleteGroup();
        deleteUserWithId(currentUser.getId());
        currentUser = null;
        OpenGMApplication.logOut();
        super.tearDown();
    }

}
