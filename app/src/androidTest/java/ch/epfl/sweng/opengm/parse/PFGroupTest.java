package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.opengm.OpenGMApplication;

import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFGroup.createNewGroup;
import static ch.epfl.sweng.opengm.parse.PFGroup.fetchExistingGroup;
import static ch.epfl.sweng.opengm.parse.PFMember.fetchExistingMember;
import static ch.epfl.sweng.opengm.parse.PFUser.createNewUser;
import static ch.epfl.sweng.opengm.parse.PFUser.fetchExistingUser;
import static ch.epfl.sweng.opengm.utils.Utils.unzipRoles;
import static ch.epfl.sweng.opengm.utils.Utils.zipRole;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PFGroupTest {

    private final String EMAIL = "bobby.lapointe@caramail.co.uk";
    private final String USERNAME = "BobTheBobbyGroupTest";
    private final String FIRST_NAME = "Bobby";
    private final String LAST_NAME = "LaPointe";

    private String id1, id2;
    private PFGroup group;

    @Before
    public void newIds() {
        id1 = null;
        id2 = null;
        group = null;
    }

    @Test
    public void testWriteToParcel() {
        Parcel parcel = Parcel.obtain();
        String name = "testGroup";
        String description = "testDescription";
        id1 = getRandomId();
        PFUser user = null;
        try {
            user = createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
            group = createNewGroup(user, name, description, null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        group.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        parcel.readString(); //group id
        parcel.readString(); //last modified
        assertEquals(name, parcel.readString());
        ArrayList<String> ids = new ArrayList<>();
        parcel.readStringList(ids);
        assertEquals(1, ids.size());
        assertEquals(id1, ids.get(0));
        ArrayList<String> nicknames = new ArrayList<>();
        parcel.readStringList(nicknames);
        assertEquals(1, nicknames.size());
        PFMember member = null;
        try {
            member = PFMember.fetchExistingMember(id1);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        assertEquals(member.getNickname(), nicknames.get(0));
        ArrayList<String> rolesZip = new ArrayList<>();
        parcel.readStringList(rolesZip);
        assertNotNull(rolesZip);
        List<String[]> roles = unzipRoles(rolesZip);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        List<PFEvent> events = new ArrayList<>();
        List<String> eventsKeys = parcel.createStringArrayList();
        parcel.readTypedList(events, PFEvent.CREATOR);
        assertEquals(0, events.size());
        assertEquals(events.size(), eventsKeys.size());
        assertEquals(42, parcel.readInt());
        assertEquals(description, parcel.readString());
        assertNull(parcel.readParcelable(Bitmap.class.getClassLoader()));
    }


    @Test
    public void testConstructFromParcel() {
        id1 = getRandomId(); //group
        id2 = getRandomId(); //user
        String name = "testGroup";
        Date date = new Date();
        List<String> ids = new ArrayList<>();
        ids.add(id2);
        List<String> nicknames = new ArrayList<>();
        nicknames.add(USERNAME);
        String[] role = new String[2];
        role[0] = "admin";
        role[1] = "bobbbbbbyyyyy!!!";
        List<String> roles = new ArrayList<>();
        roles.add(zipRole(role));
        List<String> eventKeys = new ArrayList<>();
        List<PFEvent> events = new ArrayList<>();
        int isPrivate = 0;
        String description = "testDescription";
        Bitmap picture = null;

        Parcel in = Parcel.obtain();

        in.writeString(id1);
        in.writeString(dateToString(date));
        in.writeString(name);
        in.writeStringList(ids);
        in.writeStringList(nicknames);
        in.writeStringList(roles);
        in.writeStringList(eventKeys);
        in.writeTypedList(events);
        in.writeInt(isPrivate);
        in.writeString(description);
        in.writeParcelable(picture, 0);

        in.setDataPosition(0);

        try {
            PFUser.createNewUser(id2, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            e.printStackTrace();
        }

        PFGroup group = new PFGroup(in);

        Date lastModified = group.lastModified;

        assertEquals(id1, group.getId());
        assertEquals(date.getYear(), lastModified.getYear());
        assertEquals(date.getMonth(), lastModified.getMonth());
        assertEquals(date.getDate(), lastModified.getDate());
        assertEquals(date.getHours(), lastModified.getHours());
        assertEquals(date.getMinutes(), lastModified.getMinutes());
        assertEquals(name, group.getName());
        assertEquals(description, group.getDescription());
        assertEquals(eventKeys.size(), group.getEvents().size());
        assertEquals(events.size(), group.getEvents().size());
        assertEquals(true, group.isPrivate());
        assertNull(group.getPicture());
        assertEquals(ids.size(), group.getMembers().size());
        assertEquals(role.length, group.getRoles().size());
    }



    @Test
    public void testCreateAndDeleteGroup() {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user = null;
        try {
            user = createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Really Nice Group";
        String description = "A group, much nicer than the previous one";

        try {
            group = createNewGroup(user, name, description, null);

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
                Assert.fail("Network error");
            }

        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testGroupGetters() {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user = null;
        try {
            user = createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Really Nice Group";
        String description = "A group, much nicer than the previous one";

        try {
            group = createNewGroup(user, name, description, null);

            assertEquals(name, group.getName());
            assertEquals(description, group.getDescription());
            assertTrue(group.getEvents().isEmpty());
            assertEquals(1, group.getMembers().size());
            assertTrue(group.getMembersWithoutUser(id1).isEmpty());

            PFMember member = (PFMember) group.getMembers().values().toArray()[0];

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

    }

    @Test
    public void getMembersTest() throws PFException {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        id2 = getRandomId();

        try {
            createNewUser(id2, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Another group";
        String description = "Can we add members to this group?";

        group = createNewGroup(user1, name, description, null);
        group.addUser(id2);

        Set<PFMember> members = new HashSet<>(asList(fetchExistingMember(id1),
                fetchExistingMember(id2)));

        assertEquals(members, new HashSet<>(group.getMembers().values()));

        List<PFMember> membersAlone = new ArrayList<>();
        membersAlone.add(fetchExistingMember(id2));
        assertEquals(membersAlone, group.getMembersWithoutUser(id1));

        group.removeUser(id1);

        assertEquals(1, group.getMembers().size());

        group.removeUser(id2);

    }

    @Test
    public void testSetters() throws InterruptedException {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        id2 = getRandomId();

        PFUser user2 = null;
        try {
            user2 = createNewUser(id2, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group = null;
        try {
            group = createNewGroup(user1, "OneDirection", "Death Metal Band", null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        PFGroup group2;

        String name = "Gojira";
        String description = "A jazz band";
        String nicknameForUser1 = "The man in the corner";
        String role = "singer";

        group.setName(name);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(name, group.getName());
            assertEquals(name, group2.getName());
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group.setDescription(description);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(description, group.getDescription());
            assertEquals(description, group2.getDescription());
            assertEquals(group.getName(), group2.getName());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group.setNicknameForUser(nicknameForUser1, user1.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(nicknameForUser1, group.getNicknameForUser(user1.getId()));
            assertEquals(nicknameForUser1, group2.getNicknameForUser(user1.getId()));
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group.addRoleToUser(role, user1.getId());

        List<String> roles = new ArrayList<>();
        roles.add(role);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(roles, group.getRolesForUser(user1.getId()));
            assertEquals(roles, group2.getRolesForUser(user1.getId()));
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group.removeRoleToUser(role, user1.getId());

        roles.clear();

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(roles, group.getRolesForUser(user1.getId()));
            assertEquals(roles, group2.getRolesForUser(user1.getId()));
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group.addUser(user2.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        try {
            PFUser user3 = fetchExistingUser(user2.getId());
            assertEquals(user3.getId(), user2.getId());
            assertEquals(user3.getUsername(), user2.getUsername());
            assertEquals(user3.getFirstName(), user2.getFirstName());
            assertEquals(user3.getLastName(), user2.getLastName());
            assertEquals(user3.getAboutUser(), user2.getAboutUser());
            assertEquals(user3.getPhoneNumber(), user2.getPhoneNumber());
            assertEquals(user3.getPicture(), user2.getPicture());
            assertEquals(1, user3.getGroups().size());
            assertEquals(group, user3.getGroups().get(0));
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group.removeUser(user1.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group.removeUser(user2.getId());

        Thread.sleep(2000);

        try {
            fetchExistingGroup(group.getId());
            Assert.fail("Should have thrown a no result for this query exception");
        } catch (PFException e) {
            // Success
        }

    }

    @After
    public void deleteAfterTesting() {
        deleteUserWithId(id1);
        deleteUserWithId(id2);
        if (group != null)
            group.deleteGroup();
    }
}