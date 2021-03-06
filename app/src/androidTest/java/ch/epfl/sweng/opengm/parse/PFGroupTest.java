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
    private final String PHONE_NUMBER = "0123456789";

    private final String parseExceptionFailTest = "Network error";
    private final String adminRole = "Administrator";
    private final String userRole = "User";

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
        List<List<Integer>> permissions = new ArrayList<>();
        List<Integer> actualPermissions = new ArrayList<>();
        for (PFGroup.Permission permission : PFGroup.Permission.values()) {
            actualPermissions.add(permission.getValue());
        }
        permissions.add(actualPermissions);
        id1 = getRandomId();
        PFUser user = null;
        try {
            user = createNewUser(id1, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
            group = createNewGroup(user, name, description, null);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }
        List<String> conversations = new ArrayList<>();
        conversations.add("conv1");
        group.setConversationInformations(conversations);
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
            Assert.fail(parseExceptionFailTest);
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
        parcel.createStringArrayList();
        //assertEquals(conversations.size(), parcel.createStringArrayList().size());
        assertEquals(permissions, parcel.readArrayList(List.class.getClassLoader()));
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
        List<String> rolesList = new ArrayList<>();
        List<List<Integer>> permissions = new ArrayList<>();
        rolesList.add(adminRole);
        List<Integer> permissionsForAdministrator = new ArrayList<>();
        List<PFGroup.Permission> actualPermissions = new ArrayList<>();
        for (PFGroup.Permission permission : PFGroup.Permission.values()) {
            permissionsForAdministrator.add(permission.getValue());
            actualPermissions.add(permission);
        }
        permissions.add(permissionsForAdministrator);

        List<String> conversations = new ArrayList<>();
        conversations.add("conv1");

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
        in.writeStringList(rolesList);
        in.writeStringList(conversations);
        in.writeList(permissions);

        in.setDataPosition(0);

        try {
            PFUser.createNewUser(id2, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
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
        assertEquals(conversations.get(0), group.getConversationInformations().get(0));
        assertEquals(actualPermissions, group.getPermissionsForRole(adminRole));
    }


    @Test
    public void testCreateAndDeleteGroup() {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user = null;
        try {
            user = createNewUser(id1, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
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
                Assert.fail(parseExceptionFailTest);
            }

        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }
    }

    @Test
    public void testGroupGetters() {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user = null;
        try {
            user = createNewUser(id1, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
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

            assertTrue(member.getRoles().contains(adminRole));
            assertEquals(USERNAME, member.getNickname());
            assertEquals(user.getId(), member.getId());
            assertEquals(user.getUsername(), member.getUsername());
            assertEquals(user.getFirstName(), member.getFirstName());
            assertEquals(user.getLastName(), member.getLastName());
            assertEquals(user.getPicture(), member.getPicture());
            assertEquals(user.getAboutUser(), member.getAbout());

            group.deleteGroup();
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

    }

    @Test
    public void getMembersTest() throws PFException {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = createNewUser(id1, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }
        id2 = getRandomId();

        try {
            createNewUser(id2, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        String name = "Another group";
        String description = "Can we add members to this group?";

        group = createNewGroup(user1, name, description, null);
        group.addUserWithId(id2);

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
            user1 = createNewUser(id1, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }
        id2 = getRandomId();

        PFUser user2 = null;
        try {
            user2 = createNewUser(id2, EMAIL, "0", USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group = null;
        try {
            group = createNewGroup(user1, "OneDirection", "Death Metal Band", null);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        PFGroup group2;

        // Test if first user has "Administrator Role"
        List<String> roles = new ArrayList<>();
        roles.add(adminRole);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(roles, group2.getRolesForUser(id1));
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

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
            assertEquals(group.getEvents().size(), group2.getEvents().size());
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
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
            assertEquals(group.getEvents().size(), group2.getEvents().size());
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
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
            assertEquals(group.getEvents().size(), group2.getEvents().size());
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group.addRoleToUser(role, user1.getId());

        roles.add(role);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(roles, group.getRolesForUser(user1.getId()));
            assertEquals(roles, group2.getRolesForUser(user1.getId()));
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents().size(), group2.getEvents().size());
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group.removeRoleToUser(role, user1.getId());

        roles.remove(role);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(roles, group.getRolesForUser(user1.getId()));
            assertEquals(roles, group2.getRolesForUser(user1.getId()));
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents().size(), group2.getEvents().size());
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group.addUserWithId(user2.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents().size(), group2.getEvents().size());
            assertTrue(group2.getRolesForUser(id2).contains(userRole));
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        try {
            PFUser user3 = fetchExistingUser(user2.getId());
            for (String groupId : user3.getGroupsIds()) {
                user3.fetchGroupWithId(groupId);
            }
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
            Assert.fail(parseExceptionFailTest);
        }

        group.removeUser(user1.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(group.getId(), group2.getId());
            assertEquals(group.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group.getMembers().values()), new HashSet<>(group2.getMembers().values()));
            assertEquals(group.getEvents().size(), group2.getEvents().size());
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
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

    @Test
    public void testNewGroupHasRolesAndPermissions() throws InterruptedException {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = createNewUser(id1, EMAIL, PHONE_NUMBER, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        PFUser user2 = null;
        id2 = getRandomId();
        try {
            user2 = createNewUser(id2, EMAIL, PHONE_NUMBER, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        List<PFGroup.Permission> permissions = Arrays.asList(PFGroup.Permission.values());

        group = null;
        try {
            group = createNewGroup(user1, "OneDirection", "Death Metal Band", null);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        PFGroup group2;

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(permissions, group2.getPermissionsForUser(user1.getId()));
            assertEquals(permissions, group2.getPermissionsForRole(adminRole));
            assertEquals(permissions, group.getPermissionsForUser(user1.getId()));
            assertEquals(permissions, group.getPermissionsForRole(adminRole));
            for (PFGroup.Permission permission : permissions) {
                assertTrue(group2.hasUserPermission(user1.getId(), permission));
                assertTrue(group.hasUserPermission(user1.getId(), permission));
            }
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group.addUserWithId(user2.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(permissions, group2.getPermissionsForUser(user2.getId()));
            assertEquals(permissions, group2.getPermissionsForRole(userRole));
            assertEquals(permissions, group.getPermissionsForUser(user2.getId()));
            assertEquals(permissions, group.getPermissionsForRole(userRole));
            for (PFGroup.Permission permission : permissions) {
                assertTrue(group2.hasUserPermission(user2.getId(), permission));
                assertTrue(group.hasUserPermission(user2.getId(), permission));
            }
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group.removeUser(user1.getId());
        group.removeUser(user2.getId());
    }

    @Test
    public void testPermissionsRemoveAndAdd() throws InterruptedException {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = createNewUser(id1, EMAIL, PHONE_NUMBER, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        List<PFGroup.Permission> permissions = new ArrayList<>(Arrays.asList(PFGroup.Permission.values()));

        group = null;
        try {
            group = createNewGroup(user1, "OneDirection", "Death Metal Band", null);
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        PFGroup group2;

        group.removePermissionFromRole(adminRole, PFGroup.Permission.ADD_EVENT);
        permissions.remove(PFGroup.Permission.ADD_EVENT);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(permissions, group2.getPermissionsForRole(adminRole));
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group.removePermissionFromUser(user1.getId(), PFGroup.Permission.ADD_MEMBER);
        permissions.remove(PFGroup.Permission.ADD_MEMBER);
        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(permissions, group2.getPermissionsForUser(user1.getId()));
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group.addPermissionToRole(adminRole, PFGroup.Permission.ADD_EVENT);
        permissions.add(PFGroup.Permission.ADD_EVENT);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(permissions, group2.getPermissionsForRole(adminRole));
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
        }

        group.addPermissionToUser(user1.getId(), PFGroup.Permission.ADD_MEMBER);
        permissions.add(PFGroup.Permission.ADD_MEMBER);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group.getId());
            assertEquals(permissions, group2.getPermissionsForUser(user1.getId()));
        } catch (PFException e) {
            Assert.fail(parseExceptionFailTest);
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