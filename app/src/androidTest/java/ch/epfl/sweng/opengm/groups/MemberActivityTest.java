package ch.epfl.sweng.opengm.groups;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.UtilsTest;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;

public class MemberActivityTest extends ActivityInstrumentationTestCase2<MembersActivity> {

    private MembersActivity activity;
    private ListView list;
    private PFGroup testGroup;
    private List<PFUser> testUsers;
    private List<ParseUser> parseUsers;
    private MembersAdapter adapter;
    private PFUser user;

    public MemberActivityTest() {
        super(MembersActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        OpenGMApplication.logOut();

        testUsers = new ArrayList<>();
        parseUsers = new ArrayList<>();

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(UtilsTest.getRandomId());
        parseUser.setPassword("a");
        parseUser.setEmail(getRandomId() + "@testUser.com");
        parseUsers.add(parseUser);
        parseUser.signUp();

        user = PFUser.createNewUser(parseUser.getObjectId(), parseUser.getEmail(), "+41781234567", parseUser.getUsername(), "testFirst", "testLast");
        OpenGMApplication.setCurrentUser(user);
        testUsers.add(user);

        testGroup = PFGroup.createNewGroup(user, "testGroup", "bla", null);
        OpenGMApplication.setCurrentGroup(testGroup);

        for (int i = 1; i <= 5; i++) {
            parseUser = new ParseUser();
            parseUser.setUsername("testUsername" + getRandomId() + i);
            parseUser.setPassword("a");
            parseUser.setEmail(getRandomId() + "i" + i + "@testUser.com");
            parseUsers.add(parseUser);
            parseUser.signUp();

            PFUser testUser = PFUser.createNewUser(parseUser.getObjectId(), parseUser.getEmail(), "+41781234567", parseUser.getUsername(), "testFirst" + i, "testLast" + i);
            testUsers.add(testUser);
            testGroup.addUserWithId(testUser.getId());
        }

        for (int i = 6; i <= 10; i++) {
            parseUser = new ParseUser();
            parseUser.setUsername("testUsername" + i);
            parseUser.setPassword("a");
            parseUser.setEmail("testUser" + i + "@testUser.com");
            parseUsers.add(parseUser);
            parseUser.signUp();

            PFUser testUser = PFUser.createNewUser(parseUser.getObjectId(), parseUser.getEmail(), "+41781234567", parseUser.getUsername(), "testFirst" + i, "testLast" + i);
            testUsers.add(testUser);
        }

        activity = getActivity();
        list = (ListView) activity.findViewById(R.id.member_list);
        adapter = (MembersAdapter) list.getAdapter();
    }

    @Override
    protected void tearDown() throws Exception {
        OpenGMApplication.logOut();
        testGroup.deleteGroup();
        for (PFUser users : testUsers) {
            deleteUserWithId(users.getId());
        }
        for (ParseUser user : parseUsers) {
            ParseUser.logIn(user.getUsername(), "a");
            user.delete();
            ParseUser.logOut();
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
        onView(withId(R.id.dialog_add_member_username)).perform(typeText("testUsername" + 10));
        onView(withText(R.string.add)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(getDisplayedMembersNames().contains("testUsername10"));
        assertFalse(getDisplayedMembersNames().contains("testUsername8"));
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
