package ch.epfl.sweng.opengm.userProfile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.UtilsTest;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


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

        OpenGMApplication.logOut();

        currentUser = PFUser.createNewUser(UtilsTest.getRandomId(), "SwaggyBoy66", "+3312131415", "alex.kuzbidon@me.com", "Alex", "Kuzbidon");
        currentGroup = PFGroup.createNewGroup(currentUser, "Hipster Group", "This is a group only for hipsters.", null);

        OpenGMApplication.setCurrentUser(currentUser);
        OpenGMApplication.setCurrentGroup(currentGroup);
    }

    public void testDisplayCorrectInformationsWithoutImage() {
        activity = getActivity();
        onView(withId(R.id.firstNameTV)).check(matches(withText(currentUser.getFirstName())));
        onView(withId(R.id.lastNameTV)).check(matches(withText(currentUser.getLastName())));
        onView(withId(R.id.emailTV)).check(matches(withText(currentUser.getEmail())));
        onView(withId(R.id.phoneTV)).check(matches(withText(currentUser.getPhoneNumber())));
        onView(withId(R.id.descriptionTV)).check(matches(withText(currentUser.getAboutUser())));
        Bitmap userPhoto = BitmapFactory.decodeResource(activity.getResources(), R.drawable.avatar_male1);
        onView(withId(R.id.userPhoto)).check(matches(isProperlyDisplayed(userPhoto)));
    }

    public void testDisplayCorrectInformationsWithImage() throws InterruptedException {
        activity = getActivity();
        onView(withId(R.id.action_edit_user_profile)).perform(click());
        Bitmap userPhoto = BitmapFactory.decodeResource(activity.getResources(), R.drawable.avatar_female1);
        currentUser.setPicture(userPhoto);
        Thread.sleep(2000);
        onView(withId(R.id.action_save_profile)).perform(click());
        onView(withId(R.id.firstNameTV)).check(matches(withText(currentUser.getFirstName())));
        onView(withId(R.id.lastNameTV)).check(matches(withText(currentUser.getLastName())));
        onView(withId(R.id.emailTV)).check(matches(withText(currentUser.getEmail())));
        onView(withId(R.id.phoneTV)).check(matches(withText(currentUser.getPhoneNumber())));
        onView(withId(R.id.descriptionTV)).check(matches(withText(currentUser.getAboutUser())));
        onView(withId(R.id.userPhoto)).check(matches(isProperlyDisplayed(userPhoto)));
    }

    private TypeSafeMatcher isProperlyDisplayed(final Bitmap userPhoto) {
        return new TypeSafeMatcher() {
            @Override
            protected boolean matchesSafely(Object o) {
                if (o == null) {
                    return userPhoto == null;
                }
                ImageView i = (ImageView) o;
                Bitmap photo = ((BitmapDrawable) i.getDrawable()).getBitmap();
                return equalBitmaps(userPhoto, photo);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    private boolean equalBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }
}
