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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;

public class RegisterActivityTest extends ActivityInstrumentationTestCase2<RegisterActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();

    private final static String USERNAME = "DELETE" + CURRENT_DATE;
    private final static String FIRSTNAME = "Chuck";
    private final static String LASTRNAME = "Norris";
    private final static String PASSWORD_INCORRECT = "abc";
    private final static String PASSWORD_CORRECT = "Abcdef12";
    private final static String EMAIL_INCORRECT = "yolo";
    private final static String EMAIL_CORRECT = CURRENT_DATE + "@yolo.ch";

    private String id;

    public RegisterActivityTest() {
        super(RegisterActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    @Before
    public void newIds() {
        id = null;
    }

    public void testFields() {

        OpenGMApplication.logOut();

        RegisterActivity activity = getActivity();

        // empty username
        onView(ViewMatchers.withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_username)).check(matches(isTextStyleCorrect(activity.getString(R.string.emtpy_username_activity_register), true)));

        //empty password1
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));

        //empty password2
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password2)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));

        //empty firstname
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_firstname)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_firstname_activity_register), true)));

        //empty lastname
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_lastname)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_lastname_activity_register), true)));

        //empty email
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTRNAME));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_email_activity_register), true)));

        //bad email
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isTextStyleCorrect(activity.getString(R.string.incorrect_email_activity_register), true)));

        //short password1
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.short_password_activity_register), true)));

        //long password1
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.long_password_activity_register), true)));

        //all caps password1
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("1AAAAAAAAAAAAA"));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.case_password_activity_register), true)));

        //without caps password1
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("1aaaaaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.case_password_activity_register), true)));

        //without numbers password1
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("Aaaaaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.no_number_password_activity_register), true)));

        //without letters password1
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText("123456789"));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.no_letter_password_activity_register), true)));

        //password not correct
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.incorrect_password_activity_register), true)));

        onView(withId(R.id.register_password2)).perform(clearText());
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password2)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));
        onView(withId(R.id.register_password2)).perform(typeText(PASSWORD_CORRECT));
        closeSoftKeyboard();

        assertNull(ParseUser.getCurrentUser());

        onView(withId(R.id.button_signup)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Assert.fail("Waiting instruction failed");
        }

        onView(withId(R.id.groups_recycler_view)).check(matches(isDisplayed()));

        PFUser user = OpenGMApplication.getCurrentUser();

        assertNotNull(user);
        assertNotNull(ParseUser.getCurrentUser());

        id = user.getId();

        assertEquals(id, ParseUser.getCurrentUser().getObjectId());

        OpenGMApplication.logOut();
    }

    @AfterClass
    public void tearDown() {
        deleteUserWithId(id);
    }

}