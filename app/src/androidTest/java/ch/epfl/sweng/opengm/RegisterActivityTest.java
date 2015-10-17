package ch.epfl.sweng.opengm;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

import ch.epfl.sweng.opengm.identification.RegisterActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.opengm.StyleIdentificationUtils.isTextStyleCorrect;

public class RegisterActivityTest extends ActivityInstrumentationTestCase2<RegisterActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();

    private final static String USERNAME = "TestRegister-" + CURRENT_DATE;
    private final static String FIRSTNAME = "Chuck";
    private final static String LASTRNAME = "Norris";
    private final static String PASSWORD_INCORRECT = "abcdef12";
    private final static String PASSWORD_CORRECT = "Abcdef12";
    private final static String EMAIL_INCORRECT = "yolo";
    private final static String EMAIL_CORRECT = CURRENT_DATE + "@yolo.ch";

    public RegisterActivityTest() {
        super(RegisterActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testEmptyFields() {

        RegisterActivity activity = getActivity();

        // empty username
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_username)).check(matches(isTextStyleCorrect(activity.getString(R.string.emtpy_username_activity_register), true)));

        //empty password1
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));

        //empty password2
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password2)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));

        //empty firstname
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_firstname)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_firstname_activity_register), true)));

        //empty lastname
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_lastname)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_lastname_activity_register), true)));

        //empty email
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTRNAME));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_email_activity_register), true)));

        //bad email
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_INCORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isTextStyleCorrect(activity.getString(R.string.incorrect_email_activity_register), true)));

        //bad password
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.short_password_activity_register), true)));

        //password not correct
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect(activity.getString(R.string.incorrect_password_activity_register), true)));


        onView(withId(R.id.register_password2)).perform(clearText());
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password2)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));
        onView(withId(R.id.register_password2)).perform(typeText(PASSWORD_CORRECT));

        onView(withId(R.id.register_username)).check(matches(isTextStyleCorrect("", false)));
        onView(withId(R.id.register_password1)).check(matches(isTextStyleCorrect("", false)));
        onView(withId(R.id.register_password2)).check(matches(isTextStyleCorrect("", false)));
        onView(withId(R.id.register_firstname)).check(matches(isTextStyleCorrect("", false)));
        onView(withId(R.id.register_lastname)).check(matches(isTextStyleCorrect("", false)));
        onView(withId(R.id.register_email)).check(matches(isTextStyleCorrect("", false)));
    }
}