package ch.epfl.sweng.opengm.groups;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.junit.After;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ManageRolesActivityTest extends ActivityInstrumentationTestCase2<ManageRolesActivity>{
    private ListView listView;
    private ListView permissionView;
    private PFGroup testGroup;
    private List<PFUser> testUsers;
    private ManageRolesActivity createRolesActivity;

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
    private final static String ADMIN_ROLE = "Administrator";

    private final static String ROLE_FOR_PREFIX = "roleFor";
    private final static String ROLE_FOR_BOTH = "ForBoth";

    private final static String SAVE_BUTTON = "Save";
    private final static String ADD_BUTTON = "Add";


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
        OpenGMApplication.logOut();
        super.tearDown();
    }

    public void testIfFetchesUsersRoles() throws Exception {
        prepareIntentAndDatabase(1);
        Thread.sleep(1000);
        getActivityAndLayout();

        boolean viewMatches = databaseRolesMatchesView();

        assertTrue(viewMatches);
    }

    public void testIfAddsRoles() throws Exception {
        prepareIntentAndDatabase(1);
        getActivityAndLayout();
        addNewRoleWithView(NEW_TEST_ROLE_PREFIX, "1");
        assertTrue(getDisplayedRoles().contains(NEW_TEST_ROLE_PREFIX + "1"));
        onView(withId(R.id.button)).perform(click());
        updateReferencesFromDatabase();
        assertTrue(getRolesOrFail(testUsers.get(0).getId()).contains(NEW_TEST_ROLE_PREFIX + "1"));
    }

    public void testIfRemovesRoleFromDatabase() throws Exception {
        prepareIntentAndDatabase(1);
        testGroup.addRoleToUser(TEST_ROLE_PREFIX + "0", testUsers.get(0).getId());
        Thread.sleep(1000);
        OpenGMApplication.getCurrentUser().getGroups().get(0).reload();
        Thread.sleep(1000);
        getActivityAndLayout();

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForRole(TEST_ROLE_PREFIX + "0").performClick();
            }
        });
        onView(withId(R.id.action_remove_role)).perform(click());
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

        onView(withId(R.id.action_add_role)).perform(click());
        onView(withText(ADD_BUTTON)).perform(click());

        assertFalse(getDisplayedRoles().contains(""));
    }

    public void testOnlyShowCommonRolesWhenMultipleUsers() throws Exception{
        prepareIntentAndDatabase(2);
        testGroup.addRoleToUser(ROLE_FOR_PREFIX + "0", testUsers.get(0).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH + "1", testUsers.get(1).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(0).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(1).getId());
        Thread.sleep(1000);
        OpenGMApplication.getCurrentUser().getGroups().get(0).reload();
        Thread.sleep(1000);
        getActivityAndLayout();

        boolean result = databaseRolesMatchesView();
        assertTrue(result);

    }

    public void testAddsRoleForMultipleUsers() throws Exception{
        prepareIntentAndDatabase(2);
        Thread.sleep(1000);
        getActivityAndLayout();

        addNewRoleWithView(NEW_TEST_ROLE_PREFIX, "1");
        assertTrue(getDisplayedRoles().contains(NEW_TEST_ROLE_PREFIX + "1"));
        onView(withId(R.id.button)).perform(click());
        updateReferencesFromDatabase();

        Thread.sleep(1000);

        for(PFUser user : testUsers){
            assertTrue(getRolesOrFail(user.getId()).contains(NEW_TEST_ROLE_PREFIX + "1"));
        }

    }

    public void testRemovesFromMultipleUsers() throws Exception{
        prepareIntentAndDatabase(2);
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(0).getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, testUsers.get(1).getId());
        Thread.sleep(1000);
        OpenGMApplication.getCurrentUser().getGroups().get(0).reload();
        Thread.sleep(1000);
        getActivityAndLayout();

        assertTrue(getDisplayedRoles().contains(ROLE_FOR_BOTH));

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForRole(ROLE_FOR_BOTH).performClick();
            }
        });
        onView(withId(R.id.action_remove_role)).perform(click());
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
        OpenGMApplication.getCurrentUser().getGroups().get(0).reload();
        Thread.sleep(1000);
        PFUser additionalUser = PFUser.createNewUser("additionalUser", "addi@tional.com", "0", "testDoesntAffectOtherUsers", "addi", "tional");
        testGroup.addUserWithId(additionalUser.getId());
        testGroup.addRoleToUser(ROLE_FOR_BOTH, additionalUser.getId());
        Thread.sleep(1000);
        OpenGMApplication.getCurrentUser().getGroups().get(0).reload();
        Thread.sleep(1000);
        getActivityAndLayout();

        addNewRoleWithView(NEW_TEST_ROLE_PREFIX, "");
        assertTrue(getDisplayedRoles().contains(NEW_TEST_ROLE_PREFIX));

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForRole(ROLE_FOR_BOTH).performClick();
            }
        });
        onView(withId(R.id.action_remove_role)).perform(click());

        onView(withId(R.id.button)).perform(click());

        Thread.sleep(1000);
        updateReferencesFromDatabase();
        additionalUser = PFUser.fetchExistingUser(additionalUser.getId());
        Thread.sleep(1000);

        String userID0 = testUsers.get(0).getId();
        String userID1 = testUsers.get(1).getId();
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

    public void testCorrectPermissions() throws PFException, InterruptedException {
        prepareIntentAndDatabase(1);
        getActivityAndLayout();

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForRole(ADMIN_ROLE).performClick();
            }
        });

        onView(withId(R.id.action_modify_permissions)).perform(click());

        List<String> permissions = getAllPermissions();
        permissionView = (ListView) createRolesActivity.getModifyPermissionsDialog().findViewById(R.id.dialog_modify_permissions_list);

        assertTrue(matchPermissions(permissions));
    }

    public void testCorrectPermissionsForRoles() throws PFException, InterruptedException {
        prepareIntentAndDatabase(1);
        testGroup.addRoleToUser(NEW_TEST_ROLE_PREFIX, testUsers.get(0).getId());
        Thread.sleep(1000);
        OpenGMApplication.getCurrentUser().getGroups().get(0).reload();
        Thread.sleep(1000);
        getActivityAndLayout();

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForRole(ADMIN_ROLE).performClick();
                getCheckBoxForRole(NEW_TEST_ROLE_PREFIX).performClick();
            }
        });

        onView(withId(R.id.action_modify_permissions)).perform(click());

        List<String> permissions = new ArrayList<>();
        permissionView = (ListView) createRolesActivity.getModifyPermissionsDialog().findViewById(R.id.dialog_modify_permissions_list);

        assertTrue(matchPermissions(permissions));
    }

    public void testAddPermission() throws Exception {
        prepareIntentAndDatabase(1);
        testGroup.addRoleToUser(NEW_TEST_ROLE_PREFIX, testUsers.get(0).getId());
        Thread.sleep(1000);
        OpenGMApplication.getCurrentUser().getGroups().get(0).reload();
        Thread.sleep(1000);
        getActivityAndLayout();

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForRole(NEW_TEST_ROLE_PREFIX).performClick();
            }
        });

        onView(withId(R.id.action_modify_permissions)).perform(click());

        permissionView = (ListView) createRolesActivity.getModifyPermissionsDialog().findViewById(R.id.dialog_modify_permissions_list);

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForPermission(PFGroup.Permission.ADD_MEMBER).performClick();
            }
        });

        onView(withText(SAVE_BUTTON)).perform(click());
        onView(withId(R.id.button)).perform(click());

        updateReferencesFromDatabase();
        assertTrue(testGroup.getPermissionsForRole(NEW_TEST_ROLE_PREFIX).contains(PFGroup.Permission.ADD_MEMBER));
    }

    public void testRemovePermission() throws Exception {
        prepareIntentAndDatabase(1);
        getActivityAndLayout();

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForRole(ADMIN_ROLE).performClick();
            }
        });

        onView(withId(R.id.action_modify_permissions)).perform(click());

        permissionView = (ListView) createRolesActivity.getModifyPermissionsDialog().findViewById(R.id.dialog_modify_permissions_list);

        createRolesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCheckBoxForPermission(PFGroup.Permission.ADD_MEMBER).performClick();
            }
        });

        onView(withText(SAVE_BUTTON)).perform(click());
        onView(withId(R.id.button)).perform(click());

        updateReferencesFromDatabase();
        assertTrue(!testGroup.userHavePermission(testUsers.get(0).getId(), PFGroup.Permission.ADD_MEMBER));
    }

    private CheckBox getCheckBoxForPermission(PFGroup.Permission permission){
        CheckBox toReturn = null;
        for(int i = 0; i < permissionView.getCount(); i++){
            View v = permissionView.getChildAt(i);
            CheckBox permissionBox = (CheckBox) v.findViewById(R.id.role_checkbox);
            TextView permissionName = (TextView) v.findViewById(R.id.role_name);
            if(permission.equals(PFGroup.Permission.forName(permissionName.getText().toString()))){
                toReturn = permissionBox;
            }
        }
        return toReturn;
    }

    private boolean matchPermissions(List<String> expected){
        boolean correct = true;
        List<String> visited = new ArrayList<>();
        for(int i = 0; i < permissionView.getCount(); i++){
            View v = permissionView.getChildAt(i);
            CheckBox permissionBox = (CheckBox) v.findViewById(R.id.role_checkbox);
            TextView permissionName = (TextView) v.findViewById(R.id.role_name);
            if(permissionBox.isChecked()){
                if(!expected.contains(permissionName.getText().toString())){
                    correct = false;
                }
                visited.add(permissionName.getText().toString());
            }
        }
        return correct && (visited.size() == expected.size());
    }

    private List<String> getAllPermissions(){
        List<String> allPermissions = new ArrayList<>();

        for(PFGroup.Permission permission : PFGroup.Permission.values()){
            allPermissions.add(permission.getName());
        }

        return allPermissions;
    }

    private void addNewRoleWithView(String newRole, String index){
        onView(withId(R.id.action_add_role)).perform(click());
        onView(withId(R.id.dialog_add_role_role)).perform(typeText(newRole + index));
        onView(withText(ADD_BUTTON)).perform(click());
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
        intent.putExtra(ManageRolesActivity.GROUP_INDEX, 0);

        setActivityIntent(intent);
    }

    private void setUpTestingEnvironment(int numUser) throws PFException {
        testUsers = new ArrayList<>();

        PFUser firstUser = PFUser.createNewUser(TEST_USER_ID_PREFIX + 0, TEST_USER_MAIL_PREFIX + 0 + TEST_USER_MAIL_SUFFIX, "0", TEST_USERNAME_PREFIX + 0, TEST_USER_FIRST_PREFIX + 0, TEST_USER_LAST_PREFIX + 0);
        testUsers.add(firstUser);


        testGroup = PFGroup.createNewGroup(firstUser, TEST_GROUP_NAME_PREFIX, TEST_GROUP_DESC_PREFIX, null);
        for(int i = 1; i < numUser; i++){
            testUsers.add(PFUser.createNewUser(TEST_USER_ID_PREFIX + i, TEST_USER_MAIL_PREFIX + i + TEST_USER_MAIL_SUFFIX, "0", TEST_USERNAME_PREFIX + i, TEST_USER_FIRST_PREFIX + i, TEST_USER_LAST_PREFIX + i));
            testGroup.addUserWithId(TEST_USER_ID_PREFIX + i);
        }
        firstUser.reload();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OpenGMApplication.setCurrentUser(firstUser.getId());
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

        List<String> displayedRoles = getDisplayedRoles();

        for(String role : displayedRoles){
            if(roles.contains(role)){
                roles.remove(role);
            } else {
                allIn = false;
            }
        }

        return roles.isEmpty() && allIn;
    }

    private List<String> getDisplayedRoles(){
        List<String> displayedRoles = new ArrayList<>();
        for(int i = 0; i < listView.getCount(); i++){
            View v = listView.getChildAt(i);
            TextView roleText = (TextView) v.findViewById(R.id.role_name);
            displayedRoles.add(roleText.getText().toString());
        }
        return displayedRoles;
    }

    private CheckBox getCheckBoxForRole(String role){
        CheckBox toReturn = null;
        for(int i = 0; i < listView.getCount(); i++){
            View v = listView.getChildAt(i);
            TextView roleText = (TextView) v.findViewById(R.id.role_name);
            String current = roleText.getText().toString();
            if(current.equals(role)){
                toReturn = (CheckBox) v.findViewById(R.id.role_checkbox);
            }
        }

        return toReturn;
    }

    private void updateReferencesFromDatabase() throws Exception{
        OpenGMApplication.getCurrentUser().reload();
        Thread.sleep(2000);
        testGroup = OpenGMApplication.getCurrentUser().getGroups().get(0);
//        for(int i = 0; i < testUsers.size(); i++){
//            testUsers.set(i, PFUser.fetchExistingUser(testUsers.get(i).getId()));
//        }
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
        createRolesActivity = getActivity();
        listView = (ListView) createRolesActivity.findViewById(R.id.rolesListView);
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
    }
}
