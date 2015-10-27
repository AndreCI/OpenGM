package ch.epfl.sweng.opengm.parse;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class PFGroupTest {

    private final String USER_ID = "tEsTuSr";
    private final String EMAIL = "bobby.lapointe@caramail.co.uk";
    private final String USERNAME = "BobTheBobby";
    private final String FIRST_NAME = "Bobby";
    private final String LAST_NAME = "LaPointe";


    private PFUser createTestUserWithID(String id) {
        PFUser userTest = null;
        try {
            userTest = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            e.printStackTrace();
        }
        return userTest;
    }

    private void deleteUserWithId(String id) {
        try {
            // Remove from User table
            ParseQuery<ParseObject> query1 = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
            query1.whereEqualTo(PFConstants.USER_ENTRY_USERID, id);
            ParseObject user1 = query1.getFirst();
            user1.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void deleteGroupWithName(String name) {
        try {
            // Remove from Group table
            ParseQuery<ParseObject> query = ParseQuery.getQuery(PFConstants.GROUP_TABLE_NAME);
            query.whereEqualTo(PFConstants.GROUP_ENTRY_NAME, name);
            ParseObject group = query.getFirst();
            group.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNameAndDescriptionTest() throws PFException {
        PFUser user = createTestUserWithID(USER_ID + "0");
        PFGroup group = PFGroup.createNewGroup(user, "Really Nice Group", "A group, much nicer than the previous one", null);

        List<PFGroup> groups = user.getGroups();
        assertEquals(group, groups.get(0));

        assertEquals("Really Nice Group", group.getName());
        assertEquals("A group, much nicer than the previous one", group.getDescription());

        deleteUserWithId(USER_ID + "0");
        deleteGroupWithName("Really Nice Group");
    }

    @Test
    public void getMembersTest() throws PFException {
        PFUser user = createTestUserWithID(USER_ID + "a");
        PFUser user2 = createTestUserWithID(USER_ID + "b");

        PFGroup group = PFGroup.createNewGroup(user, "Another Group", "Can we add members to this group ?", null);
        group.addUser(USER_ID + "b");

        List<PFMember> members = new ArrayList<>(Arrays.asList(PFMember.fetchExistingMember(USER_ID + "a"),
                                                               PFMember.fetchExistingMember(USER_ID + "b")));
        Collections.reverse(members);
        // Contains same members, in reverse order
        assertEquals(members, group.getMembers());

        List<PFMember> membersAlone = new ArrayList<>();
        membersAlone.add(PFMember.fetchExistingMember(USER_ID + "b"));
        assertEquals(membersAlone, group.getMembersWithoutUser(USER_ID + "a"));

        deleteUserWithId(USER_ID + "a");
        deleteUserWithId(USER_ID + "b");
        deleteGroupWithName("Another Group");
    }

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

}