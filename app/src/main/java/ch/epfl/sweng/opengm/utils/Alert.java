package ch.epfl.sweng.opengm.utils;

import android.app.Activity;
import android.widget.Toast;

import ch.epfl.sweng.opengm.OpenGMApplication;

class Alert {

    public static void displayAlert(String message) {
        Activity activity = OpenGMApplication.getCurrentActivity();
        if (activity != null) {
            Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

}
