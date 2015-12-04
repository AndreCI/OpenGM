package ch.epfl.sweng.opengm.userProfile;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import junit.framework.TestResult;


public class MyProfileActivityTest extends ActivityInstrumentationTestCase2<MyProfileActivity> {

    private MyProfileActivity activity;

    public MyProfileActivityTest() {
        super(MyProfileActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testDisplayCorrectInformations() {
        activity = getActivity();


    }
}
