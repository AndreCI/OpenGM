package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PFEventTest {

    private PFUser user;
    private PFGroup group;
    private PFEvent event;

    @Before
    public void newIds() throws PFException {
        user = PFUser.createNewUser(getRandomId(), "aa@aa.aa", "username", "username", "firstname", "lastname");
        group = PFGroup.createNewGroup(user, "group", "description", null);
    }

    @Test
    public void fetchEventWithNullId() {
        try {
            PFEvent.fetchExistingEvent(null);
            Assert.fail("Should have throw an exception");
        } catch (PFException e) {
            // Success
        }
    }


    @Test
    public void testGetters() {
        String name = "eventName";
        String place = "EPFL";
        Date date = new Date(2000, 10, 10);
        String description = "This is the event's description";
        Bitmap image = null;
        List<PFMember> members = new ArrayList<>();
        try {
            members.add(PFMember.fetchExistingMember(user.getId()));
            event = PFEvent.createEvent(group, name, place, date, members, description, image);
            assertEquals(name, event.getName());
            assertEquals(place, event.getPlace());
            assertEquals(date, event.getDate());
            assertEquals(description, event.getDescription());
            assertEquals(image, event.getPicture());
            assertEquals(members, event.getParticipants().values());
            assertTrue(group.getEvents().contains(event));
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testCreateFetch() {
        String name = "eventName";
        String place = "EPFL";
        Date date = new Date(2000, 10, 10);
        String description = "This is the event's description";
        Bitmap image = null;
        List<PFMember> members = new ArrayList<>();
        try {
            members.add(PFMember.fetchExistingMember(user.getId()));
            event = PFEvent.createEvent(group, name, place, date, members, description, image);
            PFEvent event2 = PFEvent.fetchExistingEvent(event.getId());
            assertEquals(event.getName(), event2.getName());
            assertEquals(event.getPlace(), event2.getPlace());
            assertEquals(event.getDate(), event2.getDate());
            assertEquals(event.getDescription(), event2.getDescription());
            assertEquals(event.getPicture(), event2.getPicture());
            assertEquals(event.getParticipants(), event2.getParticipants());
            assertTrue(group.getEvents().contains(event2));
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @After
    public void tearDown() throws PFException {
        group.deleteGroup();
        deleteUserWithId(user.getId());
        if (event != null)
            event.delete();
    }

}
