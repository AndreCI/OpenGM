package ch.epfl.sweng.opengm;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import ch.epfl.sweng.opengm.parse.PFConstants;

public class UtilsTest {

    public static void deleteUserWithId(String id) {
        try {
            // Remove from User table
            ParseQuery<ParseObject> query1 = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
            query1.whereEqualTo(PFConstants.USER_ENTRY_USERID, id);
            ParseObject user1 = query1.getFirst();
            user1.delete();

            // Remove from _User table
            ParseQuery<ParseUser> query2 = ParseUser.getQuery();
            ParseUser user2 = query2.get(id);
            user2.delete();
        } catch (ParseException e) {
            // Error while deleting the user but not so important
        }
    }
}
