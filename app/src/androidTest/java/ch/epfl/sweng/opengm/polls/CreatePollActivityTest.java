package ch.epfl.sweng.opengm.polls;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

import ch.epfl.sweng.opengm.UtilsTest;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFPoll;
import ch.epfl.sweng.opengm.parse.PFUser;

public class CreatePollActivityTest extends ActivityInstrumentationTestCase2<CreatePollActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();

    public CreatePollActivityTest() {
        super(CreatePollActivity.class);
    }

    private PFUser currentUser;
    private PFGroup currentGroup;
    private PFPoll currentPoll;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        currentUser = PFUser.createNewUser(UtilsTest.getRandomId(), "name", "0123456789", "email", "firstname", "lastname");
        currentGroup = PFGroup.createNewGroup(currentUser, "name", "description", null);
        currentPoll = null;
    }

    public void testEmptyName() {

    }


}
