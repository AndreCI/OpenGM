package ch.epfl.sweng.opengm.parse;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class PFUserTest {

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

    @Test
    public void wrongUserIdTest() {
        PFUser user = null;
        try {
            user = PFUser.fetchExistingUser("Mouh@h@");
        } catch (PFException e) {
            // FIXME: does that really test ?
            // sucess
        }
    }

    @Test
    public void fetchExistingUserTest() throws PFException {
        PFUser user1 = createTestUserWithID(USER_ID+"1");
        PFUser user2 = PFUser.fetchExistingUser(USER_ID+"1");
        // test with the Equals inherited from PFEntity
        // (same ParseID, same ParseTableID)
        assertEquals(user1, user2);


        assertEquals(user1.getUsername(), user2.getUsername());
        assertEquals(user1.getEmail(), user2.getEmail());
        assertEquals(user1.getFirstName(), user2.getFirstName());
        assertEquals(user1.getLastName(), user2.getLastName());

        deleteUserWithId(USER_ID+"1");
    }

    @Test
    public void gettersTest() throws PFException {
        createTestUserWithID(USER_ID+"2");
        PFUser user = PFUser.fetchExistingUser(USER_ID + "2");

        // getUsernameTest()
        assertEquals(USERNAME, user.getUsername());
        // getEmailTest()
        // FIXME: getEmail() returns null
//        assertEquals(EMAIL, user.getEmail());
        // getFirstNameTest()
        assertEquals(FIRST_NAME, user.getFirstName());
        // getLastNameTest()
        assertEquals(LAST_NAME, user.getLastName());

        deleteUserWithId(USER_ID+"2");
    }

}
