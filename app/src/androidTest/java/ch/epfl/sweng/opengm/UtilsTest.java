package ch.epfl.sweng.opengm;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;

import ch.epfl.sweng.opengm.parse.PFConstants;

public class UtilsTest {

    public static void deleteUserWithId(String id) {
        if (id == null)
            return;
        try {
            // Remove from User table
            ParseQuery<ParseObject> query1 = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
            query1.whereEqualTo(PFConstants.USER_ENTRY_USERID, id);
            ParseObject user1 = query1.getFirst();
            user1.delete();

            ParseUser.logOut();

            // Remove from _User table
            ParseQuery<ParseUser> query2 = ParseUser.getQuery();
            ParseUser user2 = query2.get(id);
            user2.deleteInBackground();
        } catch (ParseException e) {
            // Error while deleting the user but not so important
        }
    }

    public static String getRandomId() {
        return String.format("%1$10s", Calendar.getInstance().getTimeInMillis());
    }

}
