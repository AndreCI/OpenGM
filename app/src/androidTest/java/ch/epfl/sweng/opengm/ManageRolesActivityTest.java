package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.junit.Test;

import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.groups.ManageRoles;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ManageRolesActivityTest extends ActivityInstrumentationTestCase2<ManageRoles>{
    LinearLayout rolesAndButtons;
    Activity createRolesActivity;
    PFUser testUser;
    PFGroup testGroup;

    public ManageRolesActivityTest() {
        super(ManageRoles.class);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        createRolesActivity = getActivity();

        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);
        setUpDatabaseInfo();
    }

    private void setUpDatabaseInfo() throws PFException {
        testUser = PFUser.fetchExistingUser("f9PMNCFLXN");
        testGroup = PFGroup.fetchExistingGroup("9E0kzVZF4i");
        addTestRoles();
    }

    private void addTestRoles(){
        testGroup.addRoleToUser("TestRole1", testUser.getId());
        testGroup.addRoleToUser("TestRole2", testUser.getId());
    }

    private void cleanUpTestRoles(){
        List<String> roles = new ArrayList<>(testGroup.getRolesForUser(testUser.getId()));
        for(String role : roles){
            testGroup.removeRoleToUser(role, testUser.getId());
        }
    }

    private boolean databaseRolesMatchesView(){
        List<String> roles = new ArrayList<>(testGroup.getRolesForUser(testUser.getId()));;

        for(int i = 0; i < rolesAndButtons.getChildCount(); i++){
            TableRow currentRow = (TableRow) rolesAndButtons.getChildAt(i);
            if(currentRow.getChildCount() > 1){
                TextView currentRole = (TextView) currentRow.getChildAt(1);
                roles.remove(currentRole.getText().toString());
            }
        }
        return roles.isEmpty();
    }

    public void testIfFetchesUsersRoles(){
        assertTrue(databaseRolesMatchesView());
    }

    public void testIfFetchedRolesChecked(){
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
    }
}
