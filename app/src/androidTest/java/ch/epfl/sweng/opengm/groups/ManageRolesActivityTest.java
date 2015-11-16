package ch.epfl.sweng.opengm.groups;

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

import org.junit.After;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;
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

public class ManageRolesActivityTest extends ActivityInstrumentationTestCase2<ManageRolesActivity>{
    private LinearLayout rolesAndButtons;
    private PFGroup testGroup;
    private List<PFUser> testUsers;



    private final static String TEST_USER_ID_PREFIX = "testUser";
    private final static String TEST_USER_MAIL_PREFIX = "testEmail";
    private final static String TEST_USER_MAIL_SUFFIX = "@test.com";
    private final static String TEST_USERNAME_PREFIX = "testUsername";
    private final static String TEST_USER_FIRST_PREFIX = "testFirst";
    private final static String TEST_USER_LAST_PREFIX = "testLast";

    private final static String TEST_GROUP_NAME_PREFIX = "TestGroupManageRoles";
    private final static String TEST_GROUP_DESC_PREFIX = "Test Group";

    private final static String NO_USER_FOR_ID_ERROR = "No user found with id ";

    private final static String TEST_ROLE_PREFIX = "testRole";
    private final static String NEW_TEST_ROLE_PREFIX = "Super new Role";

    private final static String ROLE_BOX_PREFIX = "roleBox";
    private final static String REMOVE_ROLE_PREFIX = "removeRole";
    private final static String ROLE_NAME_PREFIX = "roleName";
    private final static String OK_BUTTON = "okButton";
    private final static String ADD_ROLE = "addRole";
    private final static String NEW_ROLE_EDIT = "newRoleEdit";

    private final static String ROLE_FOR_PREFIX = "roleFor";
    private final static String ROLE_FOR_BOTH = "ForBoth";


    public ManageRolesActivityTest() {
        super(ManageRolesActivity.class);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    @After
    public void tearDown() throws Exception {
        cleanUpDBAfterTests();
        super.tearDown();
    }

    public void testIfFetchesUsersRoles() throws Exception {
        prepareIntentAndDatabase(1);
        addTestRolesToUser(3, testUsers.get(0).getId());
        Thread.sleep(1000);
        getActivityAndLayout();

        boolean viewMatches = databaseRolesMatchesView();

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

        assertTrue(viewMatches && allChecked);
    }

    public void testIfAddsRoles() throws Exception {
        prepareIntentAndDatabase(1);
        getActivityAndLayout();
        addNewRoleWithView(NEW_TEST_ROLE_PREFIX, "1");
        onView(withTagValue(is((Object) (REMOVE_ROLE_PREFIX + "1")))).check(matches(isEnabled()));
        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "1")))).check(matches(isChecked()));
        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "1")))).check(matches(not(isEnabled())));
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateReferencesFromDatabase();
        Thread.sleep(1000);

        assertTrue(databaseRolesMatchesView());
    }


    public void testIfRemovingAddedRoleDoesntGoToDatabase() throws Exception {
        prepareIntentAndDatabase(1);
        getActivityAndLayout();
        addNewRoleWithView(NEW_TEST_ROLE_PREFIX, "1");
        onView(withTagValue(is((Object) (REMOVE_ROLE_PREFIX + "1")))).check(matches(isEnabled()));
        onView(withTagValue(is((Object) (REMOVE_ROLE_PREFIX + "1")))).perform(click());
        onView(withTagValue(is((Object) (ROLE_NAME_PREFIX + "1")))).check(doesNotExist());

        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateReferencesFromDatabase();
        Thread.sleep(1000);
        boolean result = databaseRolesMatchesView();
        assertTrue(result);
    }

    public void testIfRemovesRoleFromDatabase() throws Exception {
        prepareIntentAndDatabase(1);
        testGroup.addRoleToUser(TEST_ROLE_PREFIX + "0", testUsers.get(0).getId());
        Thread.sleep(1000);
        getActivityAndLayout();

        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "1")))).perform(click());
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateReferencesFromDatabase();
        Thread.sleep(1000);
        String userId = testUsers.get(0).getId();
        List<String> rolesForUser = getRolesOrFail(userId);
        boolean result = !rolesForUser.contains(TEST_ROLE_PREFIX + "0");
        Thread.sleep(1000);
        assertTrue(result);
    }


    public void testIfDeclinesBadRoleName() throws Exception{
        prepareIntentAndDatabase(1);
        getActivityAndLayout();
        onView(withTagValue(is((Object) ADD_ROLE))).perform(click());
        onView(withTagValue(is((Object) NEW_ROLE_EDIT))).perform(typeText(""));
        onView(withTagValue(is((Object) OK_BUTTON))).perform(click());
        onView(withTagValue(is((Object) (ROLE_NAME_PREFIX + "2")))).check(doesNotExist());
    }


    public void testCantPressAddButtonWhenEditingNewRole() throws Exception{
        prepareIntentAndDatabase(1);
        getActivityAndLayout();
        onView(withTagValue(is((Object) ADD_ROLE))).perform(click());
        onView(withTagValue(is((Object) ADD_ROLE))).check(doesNotExist());
    }

    public void testOnlyShowCommonRolesWhenMultipleUsers() throws Exception{
        prepareIntentAndDatabase(2);
        testGroup.addRoleToUser(ROLE_FOR_PREFIX + "0", testUsers.get(0).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH + "1", testUsers.get(1).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(0).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(1).getId());
        Thread.sleep(1000);
        getActivityAndLayout();

        boolean result = databaseRolesMatchesView();
        assertTrue(result);

    }

    public void testAddsRoleForMultipleUsers() throws Exception{
        prepareIntentAndDatabase(2);
        Thread.sleep(1000);
        getActivityAndLayout();
        addNewRoleWithView(NEW_TEST_ROLE_PREFIX, "0");
        onView(withTagValue(is((Object) (REMOVE_ROLE_PREFIX + "0")))).check(matches(isEnabled()));
        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "0")))).check(matches(isChecked()));
        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "0")))).check(matches(not(isEnabled())));
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateReferencesFromDatabase();
        Thread.sleep(1000);

        boolean result = databaseRolesMatchesView();
        assertTrue(result);

    }

    public void testRemovesFromMultipleUsers() throws Exception{
        prepareIntentAndDatabase(2);
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(0).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(1).getId());
        Thread.sleep(1000);
        getActivityAndLayout();

        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "0")))).perform(click());
        onView(withId(R.id.button)).perform(click());
        Thread.sleep(1000);
        updateReferencesFromDatabase();
        Thread.sleep(1000);
        String userID1 = testUsers.get(0).getId();
        String userID2 = testUsers.get(1).getId();
        List<String> rolesFor1 = getRolesOrFail(userID1);
        List<String> rolesFor2 = getRolesOrFail(userID2);
        boolean result0 = !rolesFor1.contains(ROLE_FOR_BOTH);
        boolean result1 = !rolesFor2.contains(ROLE_FOR_BOTH);
        assertTrue(result0 && result1);
    }

    public void testDoesntAffectOtherUsers () throws Exception {
        prepareIntentAndDatabase(2);
        testGroup.addRoleToUser(ROLE_FOR_PREFIX + "0", testUsers.get(0).getId());
        testGroup.addRoleToUser(ROLE_FOR_PREFIX + "1", testUsers.get(1).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(0).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(1).getId());
        Thread.sleep(1000);
        PFUser additionalUser = PFUser.createNewUser("additionalUser", "addi@tional.com", "additional", "addi", "tional");
        testGroup.addUserWithId(additionalUser.getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, additionalUser.getId());
        Thread.sleep(1000);
        getActivityAndLayout();

        onView(withTagValue(is((Object) ADD_ROLE))).perform(click());
        onView(withTagValue(is((Object) NEW_ROLE_EDIT))).perform(typeText(NEW_TEST_ROLE_PREFIX));
        onView(withTagValue(is((Object) OK_BUTTON))).perform(click());
        onView(withTagValue(is((Object) (ROLE_NAME_PREFIX + "1")))).check(matches(withText(NEW_TEST_ROLE_PREFIX)));
        onView(withTagValue(is((Object) (REMOVE_ROLE_PREFIX + "1")))).check(matches(isEnabled()));
        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "1")))).check(matches(isChecked()));
        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "1")))).check(matches(not(isEnabled())));

        onView(withTagValue(is((Object) (ROLE_BOX_PREFIX + "0")))).perform(click());
        onView(withId(R.id.button)).perform(click());

        Thread.sleep(1000);
        updateReferencesFromDatabase();
        additionalUser = PFUser.fetchExistingUser(additionalUser.getId());
        Thread.sleep(1000);

        String userID0 = testUsers.get(0).getId();
        String userID1 = testUsers.get(0).getId();
        String additionalID = additionalUser.getId();
        List<String> rolesFor0 = getRolesOrFail(userID0);
        List<String> rolesFor1 = getRolesOrFail(userID1);
        List<String> rolesForAdditional = getRolesOrFail(additionalID);

        boolean result = rolesFor0.contains(NEW_TEST_ROLE_PREFIX) &&
                rolesFor1.contains(NEW_TEST_ROLE_PREFIX);
        boolean result0 = !rolesFor0.contains(ROLE_FOR_BOTH);
        boolean result1 = !rolesFor1.contains(ROLE_FOR_BOTH);
        boolean result2 = rolesForAdditional.contains(ROLE_FOR_BOTH);
        boolean result3 = !rolesForAdditional.contains(NEW_TEST_ROLE_PREFIX);

        deleteUserFromDatabase(additionalUser.getId());
        assertTrue(result);
        assertTrue(result0);
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
    }

    private void addNewRoleWithView(String newRole, String index){
        onView(withTagValue(is((Object) ADD_ROLE))).perform(click());
        onView(withTagValue(is((Object) NEW_ROLE_EDIT))).perform(typeText(newRole));
        onView(withTagValue(is((Object) OK_BUTTON))).perform(click());
        onView(withTagValue(is((Object) (ROLE_NAME_PREFIX + index)))).check(matches(withText(newRole)));
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

    private void prepareIntentAndDatabase(int numUser) throws PFException {
        Intent intent = new Intent();
        setUpTestingEnvironment(numUser);

        ArrayList<String> testUsersIds = new ArrayList<>();
        for(PFUser testUser : testUsers){
            testUsersIds.add(testUser.getId());
        }

        intent.putStringArrayListExtra(ManageRolesActivity.USER_IDS, testUsersIds);
        intent.putExtra(ManageRolesActivity.GROUP_ID, testGroup.getId());

        setActivityIntent(intent);
    }

    private void setUpTestingEnvironment(int numUser) throws PFException {
        testUsers = new ArrayList<>();

        PFUser firstUser = PFUser.createNewUser(TEST_USER_ID_PREFIX + 0, TEST_USER_MAIL_PREFIX + 0 + TEST_USER_MAIL_SUFFIX, TEST_USERNAME_PREFIX + 0, TEST_USER_FIRST_PREFIX + 0, TEST_USER_LAST_PREFIX + 0);
        testUsers.add(firstUser);

        testGroup = PFGroup.createNewGroup(firstUser, TEST_GROUP_NAME_PREFIX, TEST_GROUP_DESC_PREFIX, null);
        for(int i = 1; i < numUser; i++){
            testUsers.add(PFUser.createNewUser(TEST_USER_ID_PREFIX + i, TEST_USER_MAIL_PREFIX + i + TEST_USER_MAIL_SUFFIX, TEST_USERNAME_PREFIX + i, TEST_USER_FIRST_PREFIX + i, TEST_USER_LAST_PREFIX + i));
            testGroup.addUserWithId(TEST_USER_ID_PREFIX + i);
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

    private void updateReferencesFromDatabase() throws Exception{
        testGroup = PFGroup.fetchExistingGroup(testGroup.getId());
        for(int i = 0; i < testUsers.size(); i++){
            testUsers.set(i, PFUser.fetchExistingUser(testUsers.get(i).getId()));
        }
    }

    private List<String>getRolesIntersection(){
        List<String> roles = new ArrayList<>(getRolesOrFail(testUsers.get(0).getId()));

        ArrayList<String> toRemove = new ArrayList<>();
        for(int i = 0; i < testUsers.size(); i++){
            for(String role : roles){
                List<String> otherRoles = getRolesOrFail(testUsers.get(i).getId());
                if(!otherRoles.contains(role)){
                    toRemove.add(role);
                }
            }
        }

        roles.removeAll(toRemove);
        return roles;
    }

    private List<String> getRolesOrFail(String userId){
        List<String> rolesForUser = testGroup.getRolesForUser(userId);
        if(rolesForUser == null){
            fail(NO_USER_FOR_ID_ERROR + userId);
        }
        return rolesForUser;
    }

    private void getActivityAndLayout() {
        Activity createRolesActivity = getActivity();
        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);
    }

    private void addTestRolesToUser(int numRoles, String userID) throws Exception{
        for(int i = 0; i < numRoles; i++){
            testGroup.addRoleToUser(TEST_ROLE_PREFIX + i, userID);
        }
        Thread.sleep(1000);
    }
}
