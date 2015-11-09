package ch.epfl.sweng.opengm;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.epfl.sweng.opengm.parse.PFConstants;

import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;
import static ch.epfl.sweng.opengm.utils.Utils.unzipRoles;
import static ch.epfl.sweng.opengm.utils.Utils.zipRole;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void StripAccentTest() {
        String input = "àâäéèêëîïìôöòûùÿç";
        String expecteed = "aaaeeeeiiiooouuyc";
        assertEquals(expecteed, stripAccents(input));
    }

    @Test
    public void zipRolesTest() {
        List<String> l = new ArrayList<>();
        String excepted = "";
        for (int i = 0; i < 10; ++i) {
            String s = "role" + Integer.toString(i);
            l.add(s);
            excepted += "role" + Integer.toString(i) + " -- ";
        }
        excepted = excepted.substring(0, excepted.length() - 4);
        String s = zipRole(l);
        assertEquals(excepted, s);
    }

    @Test
    public void unzipRolesTest() {
        List<String> l = new ArrayList<>();
        List<String[]> expected = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            String[] tab = new String[3];
            String s = "";
            for (int j = 0; j < 2; ++j) {
                String r = "role" + Integer.toString(i + j);
                tab[j] = r;
                s += r + " -- ";
            }
            String r = "role" + Integer.toString(i + 2);
            s += r;
            tab[2] = r;
            expected.add(tab);
            l.add(s);
        }

        List<String[]> result = unzipRoles(l);

        for(int i = 0; i < l.size(); ++i) {
            for(int j = 0; j < 3; ++j) {
                assertEquals(expected.get(i)[j], result.get(i)[j]);
            }
        }
    }


}
