package ch.epfl.sweng.opengm.identification;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.ParseUser;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Before;

import java.util.Calendar;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;

public class RegisterActivityTest extends ActivityInstrumentationTestCase2<RegisterActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();

    private final static String USERNAME = "DELETE" + CURRENT_DATE;
    private final static String FIRSTNAME = "Chuck";
    private final static String LASTNAME = "Norris";
    private final static String PASSWORD_INCORRECT = "abc";
    private final static String PASSWORD_CORRECT = "Abcdef12";
    private final static String EMAIL_INCORRECT = "yolo";
    private final static String EMAIL_CORRECT = CURRENT_DATE + "@yolo.ch";

    private String id;

    private RegisterActivity activity;

    public RegisterActivityTest() {
        super(RegisterActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        id = null;
        OpenGMApplication.logOut();
        activity = getActivity();
    }

    public void testEmptyUsername() {
        onView(ViewMatchers.withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_username)).check(matches(isTextStyleCorrect(activity.getString(R.string.emtpy_username_activity_register), true)));
    }

    public void testEmptyPassword1() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));
    }

    public void testEmptyPassword2() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password2)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));
    }

    public void testEmptyFirstName() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_firstname)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_firstname_activity_register), true)));
    }

    public void testEmptyLastName() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_lastname)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_lastname_activity_register), true)));
    }

    public void testEmptyMail() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_email_activity_register), true)));
    }

    public void testBadMail() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isTextStyleCorrect(activity.getString(R.string.incorrect_email_activity_register), true)));
    }

    public void testEmptyPhone() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_number)).check(matches(isTextStyleCorrect(activity.getString(R.string.incorrect_phone_number_activity_register), false)));
    }

    public void testShortPassword() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_number)).perform(click());
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("123456789"));
        closeSoftKeyboard();
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.short_password_activity_register), true)));
    }

    public void testLongPassword() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_number)).perform(click());
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("123456789"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.long_password_activity_register), true)));
    }

    public void testAllCapsPassword() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("1AAAAAAAAAA"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_number)).perform(click());
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("123456789"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.case_password_activity_register), true)));
    }

    public void testNoCapsPassword() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("1aaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_number)).perform(click());
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("123456789"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.case_password_activity_register), true)));
    }

    public void testNoNumberPassword() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("Aaaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_number)).perform(click());
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("123456789"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.no_number_password_activity_register), true)));
    }

    public void testNoLetterPassword() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("123456789"));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_number)).perform(click());
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("123456789"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.no_letter_password_activity_register), true)));
    }

    public void testNoMatchingPassword() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_number)).perform(click());
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("123456789"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.button_signup)).perform(click());
        activity.dismissPopUp();
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.non_matching_passwords_activity_register), true)));
    }

    public void testCreateUser() {
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.register_number)).perform(click());
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("123456789"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());

        assertNull(ParseUser.getCurrentUser());

        onView(withId(R.id.button_signup)).perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Assert.fail("Waiting instruction failed");
        }

        onView(withText(R.string.noGroupsYet)).inRoot(isDialog()).check(matches(isDisplayed()));

        PFUser user = OpenGMApplication.getCurrentUser();

        assertNotNull(user);
        assertNotNull(ParseUser.getCurrentUser());

        id = user.getId();

        assertEquals(id, ParseUser.getCurrentUser().getObjectId());

        assertEquals(EMAIL_CORRECT, user.getEmail());
    }

    @AfterClass
    public void tearDown() {
        deleteUserWithId(id);
        OpenGMApplication.logOut();
    }

}