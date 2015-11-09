package ch.epfl.sweng.opengm.parse;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import junit.framework.Assert;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.sweng.opengm.OpenGMApplication;

import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class PFUserTest {

    private final String EMAIL = "bobby.lapointe@caramail.co.uk";
    private final String USERNAME = "BobTheBobby";
    private final String FIRST_NAME = "Bobby";
    private final String LAST_NAME = "LaPointe";

    private String id1;

    @Before
    public void newIds() {
        id1 = null;
    }

    @Test
    public void testFetchingWithIdNull() {
        OpenGMApplication.logOut();
        try {
            PFUser.fetchExistingUser(null);
            Assert.fail("should have thrown an exception");
        } catch (PFException e) {
            // Success
        }
    }

    @Test
    public void testFetchingWithIdInvalid() {
        OpenGMApplication.logOut();
        try {
            PFUser.fetchExistingUser("Mouh@h@");
            Assert.fail("should have thrown an exception");
        } catch (PFException e) {
            // Success
        }
    }

    @Test
    public void testCreateNewUser() {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        try {
            PFUser.createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        // we wait for the background task

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
        query1.whereEqualTo(PFConstants.USER_ENTRY_USERID, id1);
        try {
            ParseObject o1 = query1.getFirst();
            if (o1 != null) {
                assertEquals(USERNAME, o1.getString(PFConstants._USER_TABLE_USERNAME));
                assertEquals(FIRST_NAME, o1.getString(PFConstants.USER_ENTRY_FIRSTNAME));
                assertEquals(LAST_NAME, o1.getString(PFConstants.USER_ENTRY_LASTNAME));
                assertEquals(new JSONArray(), o1.getJSONArray(PFConstants.USER_ENTRY_GROUPS));
                assertEquals("", o1.getString(PFConstants.USER_ENTRY_ABOUT));
                assertEquals("", o1.getString(PFConstants.USER_ENTRY_PHONENUMBER));
                assertNull(o1.getParseFile(PFConstants.USER_ENTRY_PICTURE));
            }
        } catch (ParseException e) {
            Assert.fail("Error while retrieving the user from the server");
        }
    }

    @Test
    public void testGetters() throws PFException {
        OpenGMApplication.logOut();
        id1 = getRandomId();

        PFUser user = null;
        try {
            user = PFUser.createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        assertEquals(id1, user.getId());
        assertEquals(USERNAME, user.getUsername());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals("", user.getAboutUser());
        assertEquals("", user.getPhoneNumber());
        assertNull(user.getPicture());
        assertEquals(new ArrayList<PFGroup>(), user.getGroups());
    }

    @Test
    public void testFetchExistingUser() throws PFException {
        OpenGMApplication.logOut();
        // Assuming create user is working now
        id1 = getRandomId();

        PFUser user1 = null;
        try {
            user1 = PFUser.createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        PFUser user2 = PFUser.fetchExistingUser(id1);

        assertNotNull(user1);
        assertNotNull(user2);

        // Note : we do not check emails since we work with the User table and not the _User one

        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getUsername(), user2.getUsername());
        assertEquals(user1.getFirstName(), user2.getFirstName());
        assertEquals(user1.getLastName(), user2.getLastName());
        assertEquals(user1.getAboutUser(), user2.getAboutUser());
        assertEquals(user1.getPhoneNumber(), user2.getPhoneNumber());
        assertEquals(user1.getPicture(), user2.getPicture());
        assertEquals(user1.getGroups().size(), user2.getGroups().size());
    }

    @Test
    public void settersTest() throws InterruptedException {
        OpenGMApplication.logOut();
        // Assuming create user is working now
        id1 = getRandomId();

        PFUser user1 = null, user2;
        try {
            user1 = PFUser.createNewUser(id1, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String username = "Patou";
        String firstname = "Patrick";
        String lastname = "Aebischer";
        String about = "Director of EPFL";
        String phoneNumber = "02100044433";

        try {
            user1.setUsername(username);
        } catch (PFException e) {
            e.printStackTrace();
        }

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id1);
            assertEquals(username, user2.getUsername());
            assertEquals(user1.getId(), user2.getId());
            assertEquals(user1.getFirstName(), user2.getFirstName());
            assertEquals(user1.getLastName(), user2.getLastName());
            assertEquals(user1.getAboutUser(), user2.getAboutUser());
            assertEquals(user1.getPhoneNumber(), user2.getPhoneNumber());
            assertEquals(user1.getPicture(), user2.getPicture());
            assertEquals(user1.getGroups().size(), user2.getGroups().size());
        } catch (PFException e) {
            Assert.fail("Should have thrown an exception");
        }

        try {
            user1.setFirstName(firstname);
        } catch (PFException e) {
            e.printStackTrace();
        }

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id1);
            assertEquals(firstname, user2.getFirstName());
            assertEquals(user1.getId(), user2.getId());
            assertEquals(user1.getUsername(), user2.getUsername());
            assertEquals(user1.getLastName(), user2.getLastName());
            assertEquals(user1.getAboutUser(), user2.getAboutUser());
            assertEquals(user1.getPhoneNumber(), user2.getPhoneNumber());
            assertEquals(user1.getPicture(), user2.getPicture());
            assertEquals(user1.getGroups().size(), user2.getGroups().size());
        } catch (PFException e) {
            Assert.fail("Should have thrown an exception");
        }

        try {
            user1.setLastName(lastname);
        } catch (PFException e) {
            e.printStackTrace();
        }

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id1);
            assertEquals(lastname, user2.getLastName());
            assertEquals(user1.getId(), user2.getId());
            assertEquals(user1.getUsername(), user2.getUsername());
            assertEquals(user1.getFirstName(), user2.getFirstName());
            assertEquals(user1.getAboutUser(), user2.getAboutUser());
            assertEquals(user1.getPhoneNumber(), user2.getPhoneNumber());
            assertEquals(user1.getPicture(), user2.getPicture());
            assertEquals(user1.getGroups().size(), user2.getGroups().size());
        } catch (PFException e) {
            Assert.fail("Should have thrown an exception");
        }

        try {
            user1.setAboutUser(about);
        } catch (PFException e) {
            e.printStackTrace();
        }

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id1);
            assertEquals(about, user2.getAboutUser());
            assertEquals(user1.getId(), user2.getId());
            assertEquals(user1.getUsername(), user2.getUsername());
            assertEquals(user1.getLastName(), user2.getLastName());
            assertEquals(user1.getFirstName(), user2.getFirstName());
            assertEquals(user1.getPhoneNumber(), user2.getPhoneNumber());
            assertEquals(user1.getPicture(), user2.getPicture());
            assertEquals(user1.getGroups().size(), user2.getGroups().size());
        } catch (PFException e) {
            Assert.fail("Should have thrown an exception");
        }

        try {
            user1.setPhoneNumber(phoneNumber);
        } catch (PFException e) {
            e.printStackTrace();
        }

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id1);
            assertEquals(phoneNumber, user2.getPhoneNumber());
            assertEquals(user1.getId(), user2.getId());
            assertEquals(user1.getUsername(), user2.getUsername());
            assertEquals(user1.getLastName(), user2.getLastName());
            assertEquals(user1.getAboutUser(), user2.getAboutUser());
            assertEquals(user1.getFirstName(), user2.getFirstName());
            assertEquals(user1.getPicture(), user2.getPicture());
            assertEquals(user1.getGroups().size(), user2.getGroups().size());
        } catch (PFException e) {
            Assert.fail("Should have thrown an exception");
        }

        try {
            user1.setUsername(null);
        } catch (PFException e) {
            e.printStackTrace();
        }

    }

    @After
    public void deleteAfterTesting() {
        deleteUserWithId(id1);
    }


}