package ch.epfl.sweng.opengm.parse;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.opengm.OpenGMApplication;

import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFGroup.createNewGroup;
import static ch.epfl.sweng.opengm.parse.PFGroup.fetchExistingGroup;
import static ch.epfl.sweng.opengm.parse.PFMember.fetchExistingMember;
import static ch.epfl.sweng.opengm.parse.PFUser.createNewUser;
import static ch.epfl.sweng.opengm.parse.PFUser.fetchExistingUser;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PFGroupTest {

    private final String EMAIL = "bobby.lapointe@caramail.co.uk";
    private final String USERNAME = "BobTheBobbyGroupTest";
    private final String FIRST_NAME = "Bobby";
    private final String LAST_NAME = "LaPointe";


    @Test
    public void testCreateAndDeleteGroup() {
        OpenGMApplication.logOut();
        String id = getRandomId();

        PFUser user = null;
        try {
            user = createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Really Nice Group";
        String description = "A group, much nicer than the previous one";

        try {
            PFGroup group = createNewGroup(user, name, description, null);

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
        OpenGMApplication.logOut();
        String id = getRandomId();

        PFUser user = null;
        try {
            user = createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Really Nice Group";
        String description = "A group, much nicer than the previous one";

        try {
            PFGroup group = createNewGroup(user, name, description, null);

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
        OpenGMApplication.logOut();
        String id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        String id2 = getRandomId();

        try {
            createNewUser(id2, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String name = "Another group";
        String description = "Can we add members to this group?";

        PFGroup group = createNewGroup(user1, name, description, null);
        group.addUser(id2);

        Set<PFMember> members = new HashSet<>(asList(fetchExistingMember(id1),
                fetchExistingMember(id2)));

        assertEquals(members, new HashSet<>(group.getMembers()));

        List<PFMember> membersAlone = new ArrayList<>();
        membersAlone.add(fetchExistingMember(id2));
        assertEquals(membersAlone, group.getMembersWithoutUser(id1));

        group.removeUser(id1);

        assertEquals(1, group.getMembers().size());

        group.removeUser(id2);

        deleteUserWithId(id1);
        deleteUserWithId(id2);
    }

    @Test
    public void testSetters() throws InterruptedException {
        OpenGMApplication.logOut();
        String id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }
        String id2 = getRandomId();

        PFUser user2 = null;
        try {
            user2 = createNewUser(id2, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        PFGroup group1 = null;
        try {
            group1 = createNewGroup(user1, "OneDirection", "Death Metal Band", null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        PFGroup group2;

        String name = "Gojira";
        String description = "A jazz band";
        String nicknameForUser1 = "The man in the corner";
        String role = "singer";

        group1.setName(name);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group1.getId());
            assertEquals(group1.getId(), group2.getId());
            assertEquals(name, group1.getName());
            assertEquals(name, group2.getName());
            assertEquals(group1.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group1.getMembers()), new HashSet<>(group2.getMembers()));
            assertEquals(group1.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.setDescription(description);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group1.getId());
            assertEquals(group1.getId(), group2.getId());
            assertEquals(description, group1.getDescription());
            assertEquals(description, group2.getDescription());
            assertEquals(group1.getName(), group2.getName());
            assertEquals(new HashSet<>(group1.getMembers()), new HashSet<>(group2.getMembers()));
            assertEquals(group1.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.setNicknameForUser(nicknameForUser1, user1.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group1.getId());
            assertEquals(group1.getId(), group2.getId());
            assertEquals(nicknameForUser1, group1.getNicknameForUser(user1.getId()));
            assertEquals(nicknameForUser1, group2.getNicknameForUser(user1.getId()));
            assertEquals(group1.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group1.getMembers()), new HashSet<>(group2.getMembers()));
            assertEquals(group1.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.addRoleToUser(role, user1.getId());

        List<String> roles = new ArrayList<>();
        roles.add(role);

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group1.getId());
            assertEquals(group1.getId(), group2.getId());
            assertEquals(roles, group1.getRolesForUser(user1.getId()));
            assertEquals(roles, group2.getRolesForUser(user1.getId()));
            assertEquals(group1.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group1.getMembers()), new HashSet<>(group2.getMembers()));
            assertEquals(group1.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.removeRoleToUser(role, user1.getId());

        roles.clear();

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group1.getId());
            assertEquals(group1.getId(), group2.getId());
            assertEquals(roles, group1.getRolesForUser(user1.getId()));
            assertEquals(roles, group2.getRolesForUser(user1.getId()));
            assertEquals(group1.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group1.getMembers()), new HashSet<>(group2.getMembers()));
            assertEquals(group1.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.addUser(user2.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group1.getId());
            assertEquals(group1.getId(), group2.getId());
            assertEquals(group1.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group1.getMembers()), new HashSet<>(group2.getMembers()));
            assertEquals(group1.getEvents(), group2.getEvents());
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
            assertEquals(group1, user3.getGroups().get(0));
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.removeUser(user1.getId());

        Thread.sleep(2000);

        try {
            group2 = fetchExistingGroup(group1.getId());
            assertEquals(group1.getId(), group2.getId());
            assertEquals(group1.getDescription(), group2.getDescription());
            assertEquals(new HashSet<>(group1.getMembers()), new HashSet<>(group2.getMembers()));
            assertEquals(group1.getEvents(), group2.getEvents());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.removeUser(user2.getId());

        Thread.sleep(2000);

        try {
            fetchExistingGroup(group1.getId());
            Assert.fail("Should have thrown a no result for this query exception");
        } catch (PFException e) {
            // Success
        }

        deleteUserWithId(id1);
        deleteUserWithId(id2);
    }

}