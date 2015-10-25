package ch.epfl.sweng.opengm.events;

/**
 * Created by virgile on 16/10/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.runner.RunWith;

/** Tests whether the app correctly handles multiple question formats. */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ShowEventTest extends ActivityInstrumentationTestCase2<ShowEventActivity>{
    public ShowEventTest(Class<ShowEventActivity> activityClass) {
        super(activityClass);
    }

    /*public void testPreFillWithIntent() {
        Intent eventIntent = new Intent();

    }*/

}
