package ch.epfl.sweng.opengm.groups;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;

public class MemberActivityTest extends ActivityInstrumentationTestCase2<MembersActivity> {

    private String random;


    private MembersActivity activity;
    private ListView list;
    private PFGroup testGroup;
    private List<PFUser> testUsers;
    private List<ParseUser> parseUsers;
    private PFUser user;

    public MemberActivityTest() {
        super(MembersActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        OpenGMApplication.logOut();

        random = getRandomId() + getRandomId().hashCode();

        testUsers = new ArrayList<>();
        parseUsers = new ArrayList<>();

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(random);
        parseUser.setPassword("a");
        parseUser.setEmail(random + "@testUser.com");
        parseUsers.add(parseUser);
        parseUser.signUp();

        user = PFUser.createNewUser(parseUser.getObjectId(), parseUser.getEmail(), "+41781234567", parseUser.getUsername(), "testFirst", "testLast");
        OpenGMApplication.setCurrentUser(user);
        testUsers.add(user);

        testGroup = PFGroup.createNewGroup(user, "testGroup", "bla", null);
        OpenGMApplication.setCurrentGroup(testGroup);

        for (int i = 1; i <= 4; i++) {
            parseUser = new ParseUser();
            parseUser.setUsername(random + i);
            parseUser.setPassword("a");
            parseUser.setEmail(random + i + "@testUser.com");
            parseUsers.add(parseUser);
            parseUser.signUp();

            PFUser testUser = PFUser.createNewUser(parseUser.getObjectId(), parseUser.getEmail(), "+41781234567", parseUser.getUsername(), "testFirst" + i, "testLast" + i);
            testUsers.add(testUser);
            testGroup.addUserWithId(testUser.getId());
        }

        for (int i = 5; i <= 10; i++) {
            parseUser = new ParseUser();
            parseUser.setUsername(random + i);
            parseUser.setPassword("a");
            parseUser.setEmail(random + i + "@testUser.com");
            parseUsers.add(parseUser);
            parseUser.signUp();

            PFUser testUser = PFUser.createNewUser(parseUser.getObjectId(), parseUser.getEmail(), "+41781234567", parseUser.getUsername(), "testFirst" + i, "testLast" + i);
            testUsers.add(testUser);
        }

        activity = getActivity();
        list = (ListView) activity.findViewById(R.id.member_list);
    }

    @Override
    protected void tearDown() throws Exception {
        OpenGMApplication.logOut();
        testGroup.deleteGroup();
        for (ParseUser user : parseUsers) {
            ParseUser.logIn(user.getUsername(), "a");
            deleteUserWithId(user.getObjectId());
        }
        super.tearDown();
    }

    public void testAllMembersAreDisplayed() {
        List<String> usernames = new ArrayList<>();
        usernames.add(user.getUsername());
        for (PFMember member : testGroup.getMembersWithoutUser(user.getId())) {
            usernames.add(member.getUsername());
        }
        assertTrue(usernames.equals(getDisplayedMembersNames()));
    }

    public void testAddMemberWithUsernameToGroup() {
        onView(withId(R.id.action_add_person)).perform(click());
        onView(withId(R.id.dialog_add_member_username)).perform(typeText(random + 10));
        onView(withText(R.string.add)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> l = getDisplayedMembersNames();
        assertTrue(l.contains(random + 10));
        assertFalse(l.contains(random + 8));
    }

    public void ignoretestAddMemberWithEmailToGroup() {
        onView(withId(R.id.action_add_person)).perform(click());
        onView(withId(R.id.dialog_add_member_username)).perform(typeText(random + "10@testUser.com"));
        onView(withText(R.string.add)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> l = getDisplayedMembersNames();
        assertTrue(l.contains(random + 10));
        assertFalse(l.contains(random + 8));
    }

    public void testCheckBoxAppearsOnLongClick() {
        View v = list.getChildAt(0);
        CheckBox c = (CheckBox) v.findViewById(R.id.member_checkbox);
        assertTrue(c.getVisibility() == View.GONE);
        onView(withText(random)).perform(longClick());
        assertTrue(c.getVisibility() == View.VISIBLE);
    }

    public void testCheckBoxAppearsAndCheckedOnLongClick() {
        View v = list.getChildAt(0);
        CheckBox c = (CheckBox) v.findViewById(R.id.member_checkbox);
        View v2 = list.getChildAt(1);
        CheckBox c2 = (CheckBox) v2.findViewById(R.id.member_checkbox);
        assertTrue(c.getVisibility() == View.GONE);
        onView(withText(random)).perform(longClick());
        assertTrue(c.getVisibility() == View.VISIBLE);
        assertTrue(c.isChecked());
        assertFalse(c2.isChecked());
    }

    public void testGoInSelectModeAndBack() {
        View v = list.getChildAt(0);
        CheckBox c = (CheckBox) v.findViewById(R.id.member_checkbox);
        assertTrue(c.getVisibility() == View.GONE);
        onView(withText(random)).perform(longClick());
        assertTrue(c.getVisibility() == View.VISIBLE);
        pressBack();
        assertTrue(c.getVisibility() == View.GONE);
    }

    public void testRemoveOneMember() {
        List<String> l = getDisplayedMembersNames();
        assertTrue(l.contains(random + 4));
        onView(withText(random + 4)).perform(longClick());
        onView(withId(R.id.action_remove_person)).perform(click());
        l = getDisplayedMembersNames();
        assertFalse(l.contains(random + 4));
        assertTrue(l.contains(random + 3));
    }

    public void testRemoveMultipleMembers() {
        List<String> l = getDisplayedMembersNames();
        assertTrue(l.contains(random + 4));
        assertTrue(l.contains(random + 1));
        assertTrue(l.contains(random + 2));

        onView(withText(random + 4)).perform(longClick());
        onView(withText(random + 1)).perform(click());
        onView(withText(random + 2)).perform(click());
        onView(withId(R.id.action_remove_person)).perform(click());

        l = getDisplayedMembersNames();
        assertFalse(l.contains(random + 4));
        assertFalse(l.contains(random + 1));
        assertFalse(l.contains(random + 2));
        assertTrue(l.contains(random + 3));
    }

    public void testAddThenRemoveMember() {
        onView(withId(R.id.action_add_person)).perform(click());
        onView(withId(R.id.dialog_add_member_username)).perform(typeText(random + 9 + "@testUser.com"));
        onView(withText(R.string.add)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> l = getDisplayedMembersNames();
        assertTrue(l.contains(random + 9));
        onView(withText(random + 9)).perform(longClick());
        onView(withId(R.id.action_remove_person)).perform(click());
        l = getDisplayedMembersNames();
        assertFalse(l.contains(random + 9));
    }

    public void testTryToAddWrongUsername() {
        onView(withId(R.id.action_add_person)).perform(click());
        onView(withId(R.id.dialog_add_member_username)).perform(typeText("bullshit"));
        onView(withText(R.string.add)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> l = getDisplayedMembersNames();
        assertFalse(l.contains("bullshit"));
    }

    private List<String> getDisplayedMembersNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < list.getCount(); i++) {
            TextView t = (TextView) list.getChildAt(i).findViewById(R.id.member_name);
            names.add(t.getText().toString());
        }
        return names;
    }
}
