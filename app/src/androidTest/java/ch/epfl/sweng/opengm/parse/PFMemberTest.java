package ch.epfl.sweng.opengm.parse;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.sweng.opengm.OpenGMApplication;

import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class PFMemberTest {


    private final String EMAIL = "bobby.lapointe@caramail.co.uk";
    private final String USERNAME = "BobTheBobby";
    private final String FIRST_NAME = "Bobby";
    private final String LAST_NAME = "LaPointe";


    @Test
    public void testFetchingWithIdNull() {
        try {
            PFMember.fetchExistingMember(null);
            Assert.fail("should have thrown an exception");
        } catch (PFException e) {
            // Success
        }
    }

    @Test
    public void testFetchingWithIdInvalid() {
        try {
            PFMember.fetchExistingMember("Mouh@h@");
            Assert.fail("should have thrown an exception");
        } catch (PFException e) {
            // Success
        }
    }

    @Test
    public void testGetters() throws PFException {
        OpenGMApplication.logOut();
        String id = getRandomId();

        PFUser user = null;
        try {
            user = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        // Initial empty fields
        PFMember member = PFMember.fetchExistingMember(id);
        assertEquals(id, member.getId());
        assertEquals(USERNAME, member.getUsername());
        assertEquals(FIRST_NAME, member.getFirstname());
        assertEquals(LAST_NAME, member.getLastname());
        assertEquals("", member.getAbout());
        assertEquals("", member.getPhoneNumber());
        assertNull(member.getPicture());
        assertEquals(new ArrayList<String>(), member.getRoles());

        deleteUserWithId(id);
    }

    @Test
    public void settersTest() throws InterruptedException {
        OpenGMApplication.logOut();
        // Assuming create user is working now
        String id1 = getRandomId();

        PFUser user1 = null, user2, user3 = null;
        try {
            user1 = PFUser.createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String id3 = getRandomId();

        try {
            user3 = PFUser.createNewUser(id3, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        PFGroup group1 = null;

        try {
            group1 = PFGroup.createNewGroup(user1, "Name1", "Description1", null);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.addUser(id3);

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(user3.getId());
            assertEquals(1, user2.getGroups().size());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.removeUser(id3);

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id3);
            assertEquals(0, user2.getGroups().size());
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        group1.deleteGroup();

        deleteUserWithId(id1);
        deleteUserWithId(id3);
    }

}