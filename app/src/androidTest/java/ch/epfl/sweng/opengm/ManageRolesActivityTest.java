package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.junit.Test;

import java.nio.charset.CharacterCodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.groups.ManageRoles;
import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ManageRolesActivityTest extends ActivityInstrumentationTestCase2<ManageRoles>{
    LinearLayout rolesAndButtons;
    Activity createRolesActivity;
    PFGroup testGroup;
    List<PFUser> testUsers;

    public ManageRolesActivityTest() {
        super(ManageRoles.class);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    private void launchActivityWithIntent(int numUser) throws PFException {
        Intent intent = new Intent();
        setUpTestingEnvironment(numUser);

        ArrayList<String> testUsersIds = new ArrayList<>();
        for(PFUser testUser : testUsers){
            testUsersIds.add(testUser.getId());
        }

        intent.putStringArrayListExtra(ManageRoles.USER_IDS, testUsersIds);
        intent.putExtra(ManageRoles.GROUP_ID, testGroup.getId());

        setActivityIntent(intent);
    }

    private void setUpTestingEnvironment(int numUser) throws PFException {
        testUsers = new ArrayList<>();
        PFUser firstUser = PFUser.createNewUser("testUser" + 0, "testEmail" + 0 + "@test.com", "testUsername" + 0, "testFirst" + 0, "testLast" + 0);
        testUsers.add(firstUser);
        testGroup = PFGroup.createNewGroup(firstUser, "TestGroupManageRoles", "Test Group", null);
        for(int i = 1; i < numUser; i++){
            testUsers.add(PFUser.createNewUser("testUser" + i, "testEmail" + i + "@test.com", "testUsername" + i, "testFirst" + i, "testLast" + i));
            testGroup.addUser("testUser" + i);
        }
    }

    private void cleanUpDBAfterTests() throws com.parse.ParseException, InterruptedException {
        for(PFUser testUser : testUsers){
            deleteUserFromDatabase(testUser.getId());
        }
        deleteGroupFromDatabase(testGroup.getId());
    }

    private boolean databaseRolesMatchesView() throws PFException {
        List<String> roles = getRolesIntersection();
        boolean allIn = true;

        for(int i = 0; i < rolesAndButtons.getChildCount(); i++) {
            TableRow currentRow = (TableRow) rolesAndButtons.getChildAt(i);
            if (currentRow.getChildCount() > 1) {
                TextView currentRole = (TextView) currentRow.getChildAt(1);
                if (!roles.contains(currentRole.getText().toString())) {
                    allIn = false;
                } else {
                    roles.remove(currentRole.getText().toString());
                }
            }
        }
        return roles.isEmpty() && allIn;
    }

    private void updateRefsToDB() throws Exception{
        testGroup = PFGroup.fetchExistingGroup(testGroup.getId());
        for(int i = 0; i < testUsers.size(); i++){
            testUsers.set(i, PFUser.fetchExistingUser(testUsers.get(i).getId()));
        }
    }

    private List<String>getRolesIntersection(){
        List<String> roles = new ArrayList<>(testGroup.getRolesForUser(testUsers.get(0).getId()));
        ArrayList<String> toRemove = new ArrayList<>();
        for(int i = 0; i < testUsers.size(); i++){
            for(String role : roles){
                List<String> otherRoles = testGroup.getRolesForUser(testUsers.get(i).getId());
                if(!otherRoles.contains(role)){
                    toRemove.add(role);
                }
            }
        }

        roles.removeAll(toRemove);
        return roles;
    }

    public void testIfFetchesUsersRoles() throws PFException, InterruptedException, com.parse.ParseException {
        launchActivityWithIntent(1);
        for(int i = 0; i < 3; i++){
            testGroup.addRoleToUser("testRole" + i, testUsers.get(0).getId());
        }
        Thread.sleep(1000);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);

        assertTrue(databaseRolesMatchesView());
        cleanUpDBAfterTests();
        Thread.sleep(1000);
    }

    public void testIfFetchedRolesChecked() throws Exception{
        launchActivityWithIntent(1);
        for(int i = 0; i < 3; i++){
            testGroup.addRoleToUser("testRole" + i, testUsers.get(0).getId());
        }
        Thread.sleep(1000);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);

        boolean allChecked = true;
        for(int i = 0; i < rolesAndButtons.getChildCount(); i++){
            TableRow currentRow = (TableRow) rolesAndButtons.getChildAt(i);
            if(currentRow.getChildCount() > 1){
                CheckBox box = (CheckBox) currentRow.getChildAt(0);
                if(!box.isChecked()){
                    allChecked = false;
                }
            }
        }
        assertTrue(allChecked);
        cleanUpDBAfterTests();
        Thread.sleep(1000);
    }

    public void testIfAddsRoles() throws Exception {
        launchActivityWithIntent(1);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "newRoleEdit"))).perform(typeText("Super new Role"));
        onView(withTagValue(is((Object) "okButton"))).perform(click());
        onView(withTagValue(is((Object) "roleName0"))).check(matches(withText("Super new Role")));
        onView(withTagValue(is((Object) "removeRole0"))).check(matches(isEnabled()));
        onView(withTagValue(is((Object) "roleBox0"))).check(matches(isChecked()));
        onView(withTagValue(is((Object) "roleBox0"))).check(matches(not(isEnabled())));
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateRefsToDB();
        Thread.sleep(1000);

        assertTrue(databaseRolesMatchesView());
        cleanUpDBAfterTests();
        Thread.sleep(1000);
    }


    public void testIfRemovingAddedRoleDoesntGoToDatabase() throws Exception {
        launchActivityWithIntent(1);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "newRoleEdit"))).perform(typeText("Super new Role"));
        onView(withTagValue(is((Object) "okButton"))).perform(click());
        onView(withTagValue(is((Object) "roleName0"))).check(matches(withText("Super new Role")));
        onView(withTagValue(is((Object) "removeRole0"))).check(matches(isEnabled()));
        onView(withTagValue(is((Object) "removeRole0"))).perform(click());
        onView(withTagValue(is((Object) "roleName0"))).check(doesNotExist());

        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateRefsToDB();
        Thread.sleep(1000);
        boolean result = databaseRolesMatchesView();
        cleanUpDBAfterTests();
        assertTrue(result);
    }


    public void testIfRemovesRoleFromDatabase() throws Exception {
        launchActivityWithIntent(1);
        testGroup.addRoleToUser("testRole0", testUsers.get(0).getId());
        Thread.sleep(1000);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);

        onView(withTagValue(is((Object) "roleBox0"))).perform(click());
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateRefsToDB();
        Thread.sleep(1000);
        boolean result = !testGroup.getRolesForUser(testUsers.get(0).getId()).contains("testRole0");
        cleanUpDBAfterTests();
        Thread.sleep(1000);
        assertTrue(result);
    }


    public void testIfDeclinesBadRoleName() throws Exception{
        launchActivityWithIntent(1);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "newRoleEdit"))).perform(typeText(""));
        onView(withTagValue(is((Object) "okButton"))).perform(click());
        onView(withTagValue(is((Object) "roleName2"))).check(doesNotExist());
        cleanUpDBAfterTests();
    }


    public void testCantPressAddButtonWhenEditingNewRole() throws Exception{
        launchActivityWithIntent(1);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "addRole"))).check(doesNotExist());
        cleanUpDBAfterTests();
    }

    private void deleteUserFromDatabase(String id) throws com.parse.ParseException, InterruptedException {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
        query1.whereEqualTo(PFConstants.USER_ENTRY_USERID, id);
        ParseObject user1 = query1.getFirst();
        user1.delete();
        Thread.sleep(1000);
    }

    private void deleteGroupFromDatabase(String id) throws com.parse.ParseException, InterruptedException {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(PFConstants.GROUP_TABLE_NAME);
        query1.whereEqualTo(PFConstants.OBJECT_ID, id);
        ParseObject user1 = query1.getFirst();
        user1.delete();
        Thread.sleep(1000);
    }
}
