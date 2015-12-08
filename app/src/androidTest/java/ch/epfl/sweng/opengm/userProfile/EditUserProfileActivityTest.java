package ch.epfl.sweng.opengm.userProfile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.UtilsTest;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.v4.content.res.ResourcesCompat.getDrawable;


public class EditUserProfileActivityTest extends ActivityInstrumentationTestCase2<EditUserProfileActivity> {

    private EditUserProfileActivity activity;
    private PFUser currentUser;
    private PFGroup currentGroup;

    private static final String PHONENUMBER_CHANGED = "+441842712";
    private static final String EMAILADDRESS_CHANGED = "e.f@g.hom";
    private static final String FIRSTNAME_CHANGED = "Martin";
    private static final String LASTNAME_CHANGED = "Malin";

    public EditUserProfileActivityTest() {
        super(EditUserProfileActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
        OpenGMApplication.logOut();
        currentUser = PFUser.createNewUser(UtilsTest.getRandomId(), "aBanalUsername", "+3312131415", "a.b@c.dom", "Napoleon", "Bonaparte");
        Bitmap userPhoto = BitmapFactory.decodeResource(activity.getResources(), R.drawable.avatar_female1);
        currentUser.setPicture(userPhoto);
        currentGroup = PFGroup.createNewGroup(currentUser, "Hipster Group", "This is a group only for hipsters.", null);
        OpenGMApplication.setCurrentUser(currentUser);
        OpenGMApplication.setCurrentGroup(currentGroup);
    }

    // 1st to launch
    public void editMyInformation() {
        activity = getActivity();
//        onView(withId(R.id.firstNameEditText)).perform(click);
        // Change information


        // Difficulties to test phoneNumber --> Launch another activity (PhoneAddingActivity), etc...


        // Click on "back" or "back button"
    }

    // 2nd test to launch
    public void checkCorrectnessOfInformation() {
        // on MyProfile :

        // Check if the information in the TextView matches with the information changes
    }


}
