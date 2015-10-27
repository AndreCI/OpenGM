package ch.epfl.sweng.opengm.parse;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import junit.framework.Assert;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

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


    @Test
    public void testFetchingWithIdNull() {
        try {
            PFUser.fetchExistingUser(null);
            Assert.fail("should have thrown an exception");
        } catch (PFException e) {
            // Success
        }
    }

    @Test
    public void testFetchingWithIdInvalid() {
        try {
            PFUser.fetchExistingUser("Mouh@h@");
            Assert.fail("should have thrown an exception");
        } catch (PFException e) {
            // Success
        }
    }

    @Test
    public void testCreateNewUser() {

        String id = getRandomId();

        PFUser user = null;
        try {
            user = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        // we wait for the background task
        while (user == null) ;

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
        query1.whereEqualTo(PFConstants.USER_ENTRY_USERID, id);
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
        deleteUserWithId(id);
    }

    @Test
    public void testGetters() throws PFException {
        String id = getRandomId();

        PFUser user = null;
        try {
            user = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        assertEquals(id, user.getId());
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
        // Assuming create user is working now
        String id = getRandomId();

        PFUser user1 = null;
        try {
            user1 = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        PFUser user2 = PFUser.fetchExistingUser(id);

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

        deleteUserWithId(id);
    }

    @Test
    public void settersTest() throws ParseException, InterruptedException {
        // Assuming create user is working now
        String id = getRandomId();

        PFUser user1 = null, user2;
        try {
            user1 = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            Assert.fail("Network error");
        }

        String username = "Patou";
        String firstname = "Patrick";
        String lastname = "Aebischer";
        String about = "Director of EPFL";
        String phoneNumber = "02100044433";

        user1.setUsername(username);

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id);
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

        user1.setFirstName(firstname);

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id);
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

        user1.setLastName(lastname);

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id);
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

        user1.setAboutUser(about);

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id);
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

        user1.setPhoneNumber(phoneNumber);

        Thread.sleep(2000);

        try {
            user2 = PFUser.fetchExistingUser(id);
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

        user1.setUsername(null);

        deleteUserWithId(id);
    }

}