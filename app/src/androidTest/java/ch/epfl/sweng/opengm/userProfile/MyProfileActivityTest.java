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


public class MyProfileActivityTest extends ActivityInstrumentationTestCase2<MyProfileActivity> {

    private MyProfileActivity activity;
    private PFUser currentUser;
    private PFGroup currentGroup;

    public MyProfileActivityTest() {
        super(MyProfileActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();

        OpenGMApplication.logOut();

        currentUser = PFUser.createNewUser(UtilsTest.getRandomId(), "SwaggyBoy66", "+3312131415", "alex.kuzbidon@me.com", "Alex", "Kuzbidon");
        Bitmap userPhoto = BitmapFactory.decodeResource(activity.getResources(), R.drawable.avatar_female1);
        currentUser.setPicture(userPhoto);
        currentGroup = PFGroup.createNewGroup(currentUser, "Hipster Group", "This is a group only for hipsters.", null);

        OpenGMApplication.setCurrentUser(currentUser);
        OpenGMApplication.setCurrentGroup(currentGroup);
    }

    public void testDisplayCorrectInformations() {
        onView(withId(R.id.firstNameTV)).check(matches(withText(currentUser.getFirstName())));
        onView(withId(R.id.lastNameTV)).check(matches(withText(currentUser.getLastName())));
        onView(withId(R.id.emailTV)).check(matches(withText(currentUser.getEmail())));
        onView(withId(R.id.phoneTV)).check(matches(withText(currentUser.getPhoneNumber())));
        onView(withId(R.id.descriptionTV)).check(matches(withText(currentUser.getAboutUser())));
//        if (currentUser.getPicture() == null) {
//            onView(withId(R.drawable.avatar_male1)).check(matches(isDisplayed()));
//        } else {
//            onView(withId(R.id.userPhoto)).check(matches(isProperlyDisplayed(currentUser.getPicture())));
//        }
    }

    private TypeSafeMatcher isProperlyDisplayed(final Bitmap userPhoto) {
        return new TypeSafeMatcher() {
            @Override
            protected boolean matchesSafely(Object o) {
                ImageView i = (ImageView) o;
                Bitmap photo = ((BitmapDrawable) i.getDrawable()).getBitmap();
                return photo.equals(userPhoto);

//                Drawable photo = i.getDrawable();
//                Bitmap mutableBitmap = Bitmap.createBitmap(userPhoto.getWidth(), userPhoto.getHeight(), Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(mutableBitmap);
//                photo.setBounds(0, 0, userPhoto.getWidth(), userPhoto.getHeight());
//                photo.draw(canvas);
            }
            @Override
            public void describeTo(Description description) {}
        };
    }
}
