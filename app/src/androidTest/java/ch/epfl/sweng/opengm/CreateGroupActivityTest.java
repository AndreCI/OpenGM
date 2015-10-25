package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.LinearLayout;

import ch.epfl.sweng.opengm.groups.CreateGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

public class CreateGroupActivityTest extends ActivityInstrumentationTestCase2<CreateGroup>{
    Activity createGroupActivity;
    PFUser currentUser;

    public CreateGroupActivityTest(Class activityClass) {
        super(CreateGroup.class);
    }

    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        createGroupActivity = getActivity();
        currentUser = PFUser.fetchExistingUser("f9PMNCFLXN");
        OpenGMApplication.setCurrentUser(currentUser.getId());
    }
}
