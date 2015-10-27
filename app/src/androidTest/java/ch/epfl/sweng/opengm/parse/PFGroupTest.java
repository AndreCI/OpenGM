package ch.epfl.sweng.opengm.parse;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PFGroupTest {

    private final String EMAIL = "bobby.lapointe@caramail.co.uk";
    private final String USERNAME = "BobTheBobby";
    private final String FIRST_NAME = "Bobby";
    private final String LAST_NAME = "LaPointe";


    @Test
    public void testCreateAndDeleteGroup() {
        String id = getRandomId();

        PFUser user = null;
        try {
            user = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Really Nice Group";
        String description = "A group, much nicer than the previous one";

        try {
            PFGroup group = PFGroup.createNewGroup(user, name, description, null);

            ParseQuery<ParseObject> query1 = ParseQuery.getQuery(GROUP_TABLE_NAME);
            try {
                assertNotNull(query1.get(group.getId()));
                group.deleteGroup();
                ParseQuery<ParseObject> query2 = ParseQuery.getQuery(GROUP_TABLE_NAME);
                try {
                    query2.get(group.getId());
                    Assert.fail("Should have thrown a no results for query exception");
                } catch (ParseException e) {
                    // Success
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Assert.fail("Network error");
            }

        } catch (PFException e) {
            Assert.fail("Network error");
        }

        deleteUserWithId(id);
    }

    @Test
    public void testGroupGetters() {
        String id = getRandomId();

        PFUser user = null;
        try {
            user = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Really Nice Group";
        String description = "A group, much nicer than the previous one";

        try {
            PFGroup group = PFGroup.createNewGroup(user, name, description, null);

            assertEquals(name, group.getName());
            assertEquals(description, group.getDescription());
            assertTrue(group.getEvents().isEmpty());
            assertEquals(1, group.getMembers().size());
            assertTrue(group.getMembersWithoutUser(id).isEmpty());

            PFMember member = group.getMembers().get(0);

            assertTrue(member.getRoles().isEmpty());
            assertEquals(USERNAME, member.getNickname());
            assertEquals(user.getId(), member.getId());
            assertEquals(user.getUsername(), member.getUsername());
            assertEquals(user.getFirstName(), member.getFirstname());
            assertEquals(user.getLastName(), member.getLastname());
            assertEquals(user.getPicture(), member.getPicture());
            assertEquals(user.getAboutUser(), member.getAbout());

            group.deleteGroup();
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        deleteUserWithId(id);
    }

    @Test
    public void getMembersTest() throws PFException {
        String id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = PFUser.createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        String id2 = getRandomId();

        PFUser user2 = null;
        try {
            user2 = PFUser.createNewUser(id2, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Another group";
        String description = "Can we add members to this group?";

        PFGroup group = PFGroup.createNewGroup(user1, name, description, null);
        group.addUser(id2);

        List<PFMember> members = new ArrayList<>(Arrays.asList(PFMember.fetchExistingMember(id2),
                PFMember.fetchExistingMember(id1)));

        assertEquals(members, group.getMembers());

        List<PFMember> membersAlone = new ArrayList<>();
        membersAlone.add(PFMember.fetchExistingMember(id2));
        assertEquals(membersAlone, group.getMembersWithoutUser(id1));

        group.removeUser(id1);

        assertEquals(1, group.getMembers().size());

        deleteUserWithId(id2);

        deleteUserWithId(id1);
        deleteUserWithId(id2);
    }

    /*
    @Test
    public void createFetchDeleteGroupTest() throws PFException, InterruptedException {
        PFUser user = createTestUserWithID(USER_ID + "2");
        PFGroup gr = PFGroup.createNewGroup(user, "Le joli groupe", "Un très joli groupe", null);

        PFGroup group = PFGroup.fetchExistingGroup(gr.getId());

        assertEquals(group, gr);
        assertEquals(gr.getName(), group.getName());
        assertEquals(gr.getDescription(), group.getDescription());

        deleteUserWithId(USER_ID + "2");
        gr.deleteGroup();

        Thread.sleep(2000);

        // Will throw an error at deletion (group already deleted)
        group.deleteGroup();
    }

    @Test
    public void settersTest() throws PFException, ParseException, InterruptedException {
        PFUser user = createTestUserWithID(USER_ID + "3");
        PFGroup group = PFGroup.createNewGroup(user, "OneDirection", "Death Metal Band", null);

        String name = null;
        group.setName("Gojira");
        String description = null;
        group.setDescription("A jazz band");
        String nicknameForUser = null;
//        group.setNicknameForUser("The man in the corner", PFMember.fetchExistingMember(USER_ID + "3").getId());
        group.setNicknameForUser("The man in the corner", USER_ID + "3");
//        Bitmap picture = null;
//        group.setPicture();
        boolean isPrivate = false;
        group.setPrivacy(true);

        Thread.sleep(2000);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(PFConstants.GROUP_TABLE_NAME);
        query.whereEqualTo(PFConstants.OBJECT_ID, group.getId());
        ParseObject o = query.getFirst();
        if (o != null) {
            name = o.getString(PFConstants.GROUP_ENTRY_NAME);
            description = o.getString(PFConstants.GROUP_ENTRY_DESCRIPTION);
            nicknameForUser  = o.getString(PFConstants.GROUP_ENTRY_NICKNAMES);
            isPrivate = o.getBoolean(PFConstants.GROUP_ENTRY_ISPRIVATE);
        }

        Thread.sleep(2000);

        assertEquals("Gojira", name);   // .getName()
        assertEquals("A jazz band", description);   // .getDescription()
        assertEquals("The man in the corner", nicknameForUser); // .getNicknameForUser(String userId)
        // TODO: publicly check whether group is private ?
//        assertTrue(group.isPrivate());

        deleteUserWithId(USER_ID + "3");
        try {
            deleteGroupWithName("Gojira");
        } catch (Exception e) {
            deleteGroupWithName("OneDirection");
        }
    }
*/
}