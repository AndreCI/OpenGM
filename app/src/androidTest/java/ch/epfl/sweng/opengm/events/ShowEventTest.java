package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Date;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;
import ch.epfl.sweng.opengm.parse.PFUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static ch.epfl.sweng.opengm.parse.PFGroup.createNewGroup;
import static ch.epfl.sweng.opengm.parse.PFUser.createNewUser;


public class ShowEventTest extends ActivityInstrumentationTestCase2<ShowEventActivity>{
    public ShowEventTest() {
        super(ShowEventActivity.class);
    }

    private final String EMAIL = "ShowEvent@caramail.co.uk";
    private final String USERNAME = "UserName_ShowEvent";
    private final String FIRST_NAME = "FirstName_ShowEvent";
    private final String LAST_NAME = "LastName_ShowEvent";

    private PFEvent e;
    private PFGroup group;
    private PFUser user;
    private String id;


 /*   @Before
    public void newIds() {
        e = null;
        group = null;
        id = null;
        user = null;
    }*/

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        id = getRandomId();
        String name = "groupName_ShowEvent";
        String description = "groupDescription_ShowEvent";
        try {
            user = createNewUser(id, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
            OpenGMApplication.setCurrentUser(user);
            Thread.sleep(2000);
            group = createNewGroup(user, name, description, null);
            Thread.sleep(2000);
            OpenGMApplication.setCurrentGroup(0);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        e = PFEvent.createEvent(group, "eventName_ShowEvent", "eventPlace_ShowEvent", new Date(1994, 5, 6, 2, 4), new ArrayList<PFMember>(), "eventDescription_ShowEvent", PFUtils.pathNotSpecified, PFUtils.nameNotSpecified, null);
    }

   public void testPreFillWithEvent() throws PFException {
        Intent intent = new Intent();
        EventListActivity.currentEvent = e;//intent.putExtra(Utils.EVENT_INTENT_MESSAGE, e);
        setActivityIntent(intent);
        ShowEventActivity activity = getActivity();
        onView(withId(R.id.ShowEventNameText)).check(matches(withText(" " + "eventName_ShowEvent"+" ")));
        onView(withId(R.id.ShowEventPlaceText)).check(matches(withText("      A lieu à : "+"eventPlace_ShowEvent"+"  ")));
        onView(withId(R.id.ShowEventDescriptionText)).check(matches(withText("Description de l'evenement :\n"+"eventDescription_ShowEvent")));
        assertEquals("      A "+"2 : 04"+ "  ", ((TextView) activity.findViewById(R.id.ShowEventHourText)).getText());
        assertEquals("      Le : "+"6/06/1994"+"  ", ((TextView) activity.findViewById(R.id.ShowEventDateText)).getText());
        onView(withId(R.id.ShowEventParticipants)).check(matches(withText("Liste des participants:")));
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
