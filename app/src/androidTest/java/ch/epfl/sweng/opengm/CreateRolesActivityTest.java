package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.LinearLayout;

import ch.epfl.sweng.opengm.groups.ManageRoles;

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

public class CreateRolesActivityTest extends ActivityInstrumentationTestCase2<ManageRoles>{
    LinearLayout rolesAndButtons;
    Activity createRolesActivity;

    public CreateRolesActivityTest() {
        super(ManageRoles.class);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        createRolesActivity = getActivity();

        rolesAndButtons = (LinearLayout)createRolesActivity.findViewById(R.id.rolesAndButtons);
    }

    public void testInitialThreeRoles() {
        onView(withTagValue(is((Object) "roleName0"))).check(matches(withText("Administrator")));
        onView(withTagValue(is((Object) "roleName1"))).check(matches(withText("Moderator")));
        onView(withTagValue(is((Object) "roleName2"))).check(matches(withText("User")));
    }

    public void testInitialThreeRolesUnclickable(){
        onView(withTagValue(is((Object) "removeRole0"))).check(matches(not(isEnabled())));
        onView(withTagValue(is((Object) "removeRole1"))).check(matches(not(isEnabled())));
        onView(withTagValue(is((Object) "removeRole2"))).check(matches(not(isEnabled())));
    }

    public void testAddNewRoleAndPreserveOldRolesAndState(){
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "newRoleEdit"))).perform(typeText("Super new Role"));
        onView(withTagValue(is((Object) "okButton"))).perform(click());
        onView(withTagValue(is((Object) "roleName3"))).check(matches(withText("Super new Role")));
        onView(withTagValue(is((Object) "removeRole3"))).check(matches(isEnabled()));
        testInitialThreeRoles();
        testInitialThreeRolesUnclickable();
    }

    public void testRemoveAddedRoleAndPreserveOldRolesAndState(){
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "newRoleEdit"))).perform(typeText("Super new Role"));
        onView(withTagValue(is((Object) "okButton"))).perform(click());
        onView(withTagValue(is((Object) "removeRole3"))).perform(click());
        onView(withTagValue(is((Object) "roleRow3"))).check(doesNotExist());

        testInitialThreeRolesUnclickable();
        testInitialThreeRoles();
    }

    public void testAddTwoRolesAndRemoveOnlyOne(){
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "newRoleEdit"))).perform(typeText("Super new Role"));
        onView(withTagValue(is((Object) "okButton"))).perform(click());
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "newRoleEdit"))).perform(typeText("Another super new Role"));
        onView(withTagValue(is((Object) "okButton"))).perform(click());
        onView(withTagValue(is((Object) "removeRole4"))).perform(click());
        onView(withTagValue(is((Object) "roleRow4"))).check(doesNotExist());
        onView(withTagValue(is((Object) "roleName3"))).check(matches(withText("Super new Role")));
    }

    public void testCantPressAddButtonWhenEditingNewRole(){
        onView(withTagValue(is((Object) "addRole"))).perform(click());
        onView(withTagValue(is((Object) "addRole"))).check(doesNotExist());
    }
}
