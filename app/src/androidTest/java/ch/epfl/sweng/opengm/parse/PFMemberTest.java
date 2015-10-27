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
public class PFMemberTest {

    private final String USER_ID = "tEsTuSr";
    private final String EMAIL = "bobby.lapointe@caramail.co.uk";
    private final String USERNAME = "BobTheBobby";
    private final String FIRST_NAME = "Bobby";
    private final String LAST_NAME = "LaPointe";
    private final String ABOUT_TEXT = "A simple about text";
    private final String PHONE_NUMBER = "0793332567";


    private PFUser createTestUserWithID(String id) {
        PFUser userTest = null;
        try {
            userTest = PFUser.createNewUser(id, EMAIL, USERNAME, FIRST_NAME, LAST_NAME);
            Thread.sleep(1000);
            userTest.setAboutUser(ABOUT_TEXT);
            userTest.setPhoneNumber(PHONE_NUMBER);
        } catch (PFException | InterruptedException e) {
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
    public void gettersTest() throws PFException, InterruptedException {
        createTestUserWithID(USER_ID);
        PFMember member = PFMember.fetchExistingMember(USER_ID);
        Thread.sleep(1000);

        assertEquals(USERNAME, member.getUsername());
        // TODO: getter for email ?
//        assertEquals(EMAIL, member.getEmail());
        assertEquals(FIRST_NAME, member.getFirstname());
        assertEquals(LAST_NAME, member.getLastname());
        assertEquals(PHONE_NUMBER, member.getPhoneNumber());
        assertEquals(ABOUT_TEXT, member.getAbout());
//        member.getPicture();
        // TODO: test roles
//        member.getRoles();

        deleteUserWithId(USER_ID);
    }

    @Test
    public void sameIdThanPFUserTest() {
        // TODO : cf. PFGroupTest
    }

}