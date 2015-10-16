package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseUser;

import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFUser;

public class OpenGMApplication extends Application {

    private final static String PARSE_APP_ID = "LiaIqx4G3cgt0LSZ6aYcZB7mGI5V2zx3fek03HGc";
    private final static String PARSE_KEY = "tQSqozHYj1d9hVhMAwKnEslDVXuzyATAQcOstEor";

    private static Activity currentActivity = null;
    private static PFUser currentUser = null;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            PFUser builder = new PFUser.Builder(ParseUser.getCurrentUser().getObjectId()).build();

        } catch (PFException e) {
            e.printStackTrace();
        }
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_APP_ID, PARSE_KEY);
    }

    public static PFUser getCurrentUser() {
        return currentUser;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        OpenGMApplication.currentActivity = currentActivity;
    }
}
