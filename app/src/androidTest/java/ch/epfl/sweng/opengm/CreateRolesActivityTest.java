package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.support.test.InstrumentationRegistry;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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

    }
}
