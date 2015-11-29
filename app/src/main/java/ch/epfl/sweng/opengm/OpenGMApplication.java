package ch.epfl.sweng.opengm;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

public class OpenGMApplication extends Application {

    private final static String PARSE_APP_ID = "LiaIqx4G3cgt0LSZ6aYcZB7mGI5V2zx3fek03HGc";
    private final static String PARSE_KEY = "tQSqozHYj1d9hVhMAwKnEslDVXuzyATAQcOstEor";

    private static PFUser currentUser = null;
    private static PFGroup currentGroup = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_APP_ID, PARSE_KEY);
        ParseUser.enableRevocableSessionInBackground();
    }

    public static PFUser getCurrentUser() {
        return currentUser;
    }

    public static PFUser setCurrentUser(PFUser user) {
        if (currentUser == null) {
            currentUser = user;
        }
        return currentUser;
    }

    public static PFUser setCurrentUserWithId(String id) throws PFException {
        if (currentUser == null && id != null) {
            try {
                currentUser = PFUser.fetchExistingUser(id);
            } catch (PFException e) {
                throw new PFException(e);
            }
        }
        return currentUser;
    }

    public static PFGroup getCurrentGroup() {
        return currentGroup;
    }

    public static void setCurrentGroup(PFGroup group) {
        boolean found = false;
        for (PFGroup pfGroup : currentUser.getGroups()) {
            if (pfGroup.getId().equals(group.getId()))
                found = true;
        }
        currentGroup = found ? group : null;
    }

    /**
     * Set the current group to the value you want
     *
     * @param pos the position of the group in the user's groups list or -1 to put a null group
     */
    public static void setCurrentGroup(int pos) {
        currentGroup = (pos == -1 ? null : currentUser.getGroups().get(pos));
    }

    public static void logOut() {
        currentUser = null;
        ParseUser.logOut();
    }
}
