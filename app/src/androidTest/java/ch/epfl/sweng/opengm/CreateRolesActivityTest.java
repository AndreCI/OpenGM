package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;
import android.support.test.InstrumentationRegistry;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class CreateRolesActivityTest extends ActivityInstrumentationTestCase2<CreateRoles>{
    LinearLayout rolesAndButtons;
    Activity createRolesActivity;

    public CreateRolesActivityTest() {
        super(CreateRoles.class);
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
}
