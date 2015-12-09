package ch.epfl.sweng.opengm.events;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import org.junit.After;

import java.util.ArrayList;
import java.util.Date;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;
import ch.epfl.sweng.opengm.parse.PFUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static ch.epfl.sweng.opengm.parse.PFGroup.createNewGroup;
import static ch.epfl.sweng.opengm.parse.PFUser.createNewUser;

/**
 * Created by André on 06/12/2015.
 */
public class AddParticipantTest  extends ActivityInstrumentationTestCase2<AddRemoveParticipantsActivity>  {
    public AddParticipantTest() {
        super(AddRemoveParticipantsActivity.class);
    }

    private final String EMAIL = "ShowEvent@caramail.co.uk";
    private final String USERNAME = "UserName_ShowEvent";
    private final String FIRST_NAME = "FirstName_ShowEvent";
    private final String LAST_NAME = "LastName_ShowEvent";

    private PFEvent e;
    private PFGroup group;
    private PFUser user;
    private String id;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        id = getRandomId();
        String name = "groupName_ShowEvent";
        String description = "groupDescription_ShowEvent";
        try {
            user = createNewUser(id, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
            group = createNewGroup(user, name, description, null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        e = PFEvent.createEvent(group, "eventName_ShowEvent", "eventPlace_ShowEvent", new Date(1994, 5, 6, 2, 4), new ArrayList<PFMember>(), "eventDescription_ShowEvent", PFUtils.pathNotSpecified, PFUtils.nameNotSpecified, null);
    }

    public void testCanClickOnOkayButton(){
        getActivity();
        onView(withId(R.id.AddRemoveParticipantOkayButton)).perform(click());
    }

    public void testCanSearchOnSearchView(){
        getActivity();
        onView(withId(R.id.AddRemoveParticipantSearchMember)).perform(click());
        onView(withId(R.id.AddRemoveParticipantSearchMember)).perform(typeText("a simple text"));
    }

    @After
    public void tearDown() {
        if (e != null) {
            try {
                e.delete();
            } catch (PFException e1) {
                e1.printStackTrace();
            }
        }
        if (group != null) {
            group.deleteGroup();
        }
        if(user != null) {
            deleteUserWithId(id);
        }
    }
}
