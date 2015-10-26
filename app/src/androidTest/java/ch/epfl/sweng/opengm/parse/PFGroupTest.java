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



}