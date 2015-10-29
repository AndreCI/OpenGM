package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.groups.ManageRoles;
import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
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
        String firstUserId = testUsers.get(0).getId();
        List<String> rolesForFirst = testGroup.getRolesForUser(firstUserId);
        if(rolesForFirst == null){
            fail("No user found with id " + firstUserId);
        }
        List<String> roles = new ArrayList<>(rolesForFirst);

        ArrayList<String> toRemove = new ArrayList<>();
        for(int i = 0; i < testUsers.size(); i++){
            for(String role : roles){
                String otherUserId = testUsers.get(i).getId();
                List<String> otherRoles = testGroup.getRolesForUser(otherUserId);
                if(otherRoles == null){
                    fail("No user found with id " + otherUserId);
                }
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
        String userId = testUsers.get(0).getId();
        List<String> rolesForUser = testGroup.getRolesForUser(userId);
        if(rolesForUser == null){
            fail("No user found with id " + userId);
        }
        boolean result = !rolesForUser.contains("testRole0");
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

    public void testOnlyShowCommonRolesWhenMultipleUsers() throws Exception{
        launchActivityWithIntent(2);
        testGroup.addRoleToUser("RoleFor0", testUsers.get(0).getId());
        testGroup.addRoleToUser("RoleFor1", testUsers.get(1).getId());
        testGroup.addRoleToUser("ForBoth", testUsers.get(0).getId());
        testGroup.addRoleToUser("ForBoth", testUsers.get(1).getId());
        Thread.sleep(1000);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);

        boolean result = databaseRolesMatchesView();
        cleanUpDBAfterTests();
        assertTrue(result);

    }

    public void testAddsRoleForMultipleUsers() throws Exception{
        launchActivityWithIntent(2);
        Thread.sleep(1000);
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

        boolean result = databaseRolesMatchesView();
        cleanUpDBAfterTests();
        assertTrue(result);

    }

    public void testRemovesFromMultipleUsers() throws Exception{
        launchActivityWithIntent(2);
        testGroup.addRoleToUser("ForBoth", testUsers.get(0).getId());
        testGroup.addRoleToUser("ForBoth", testUsers.get(1).getId());
        Thread.sleep(1000);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);

        onView(withTagValue(is((Object) "roleBox0"))).perform(click());
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateRefsToDB();
        Thread.sleep(1000);
        String userID1 = testUsers.get(0).getId();
        String userID2 = testUsers.get(1).getId();
        List<String> rolesFor1 = testGroup.getRolesForUser(userID1);
        List<String> rolesFor2 = testGroup.getRolesForUser(userID2);
        if(rolesFor1 == null){
            fail("No user found with id " + userID1);
        }
        if(rolesFor2 == null){
            fail("No user found with id " + userID2);
        }
        boolean result0 = !rolesFor1.contains("ForBoth");
        boolean result1 = !rolesFor2.contains("ForBoth");
        cleanUpDBAfterTests();
        Thread.sleep(1000);
        assertTrue(result0 && result1);
    }

    public void testDoesntAffectOtherUsers () throws Exception {
        launchActivityWithIntent(2);
        testGroup.addRoleToUser("RoleFor0", testUsers.get(0).getId());
        testGroup.addRoleToUser("RoleFor1", testUsers.get(1).getId());
        testGroup.addRoleToUser("ForBoth", testUsers.get(0).getId());
        testGroup.addRoleToUser("ForBoth", testUsers.get(1).getId());
        Thread.sleep(1000);
        PFUser additionalUser = PFUser.createNewUser("additionalUser", "addi@tional.com", "additional", "addi", "tional");
        testGroup.addUser(additionalUser.getId());
        testGroup.addRoleToUser("ForBoth", additionalUser.getId());
        Thread.sleep(1000);
        createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);

        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "newRoleEdit"))).perform(typeText("Super new Role"));
        onView(withTagValue(is((Object) "okButton"))).perform(click());
        onView(withTagValue(is((Object) "roleName1"))).check(matches(withText("Super new Role")));
        onView(withTagValue(is((Object) "removeRole1"))).check(matches(isEnabled()));
        onView(withTagValue(is((Object) "roleBox1"))).check(matches(isChecked()));
        onView(withTagValue(is((Object) "roleBox1"))).check(matches(not(isEnabled())));

        onView(withTagValue(is((Object) "roleBox0"))).perform(click());
        onView(withId(R.id.button)).perform(click());

        Thread.sleep(1000);
        updateRefsToDB();
        additionalUser = PFUser.fetchExistingUser(additionalUser.getId());
        Thread.sleep(1000);

        String userID0 = testUsers.get(0).getId();
        String userID1 = testUsers.get(0).getId();
        String additionalID = additionalUser.getId();
        List<String> rolesFor0 = testGroup.getRolesForUser(userID0);
        List<String> rolesFor1 = testGroup.getRolesForUser(userID1);
        List<String> rolesForAdditional = testGroup.getRolesForUser(additionalID);

        if(rolesFor0 == null){
            fail("No user found with id " + userID0);
        }
        if(rolesFor1 == null){
            fail("No user found with id " + userID1);
        }
        if(rolesForAdditional == null){
            fail("No user found with id " + additionalID);
        }

        boolean result = rolesFor0.contains("Super new Role") &&
                rolesFor1.contains("Super new Role");
        boolean result0 = !rolesFor0.contains("ForBoth");
        boolean result1 = !rolesFor1.contains("ForBoth");
        boolean result2 = rolesForAdditional.contains("ForBoth");
        boolean result3 = !rolesForAdditional.contains("Super new Role");

        cleanUpDBAfterTests();
        deleteUserFromDatabase(additionalUser.getId());
        assertTrue(result);
        assertTrue(result0);
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
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
