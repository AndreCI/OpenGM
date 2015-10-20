package ch.epfl.sweng.opengm;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

import ch.epfl.sweng.opengm.identification.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.opengm.StyleIdentificationUtils.isTextStyleCorrect;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();

    private final static String USERNAME_INCORRECT = "TestRegister-" + CURRENT_DATE;
    private final static String USERNAME_CORRECT = "Test";
    private final static String PASSWORD_INCORRECT = "aaa";
    private final static String PASSWORD_CORRECT = "Abcdef12";

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testEmptyFields() {

        LoginActivity activity = getActivity();

        // empty username
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_username)).check(matches(isTextStyleCorrect(activity.getString(R.string.emtpy_username_activity_register), true)));

        //empty password
        onView(withId(R.id.login_username)).perform(clearText()).perform(typeText(USERNAME_INCORRECT));
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));

        //bad password
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.short_password_activity_register), true)));

        //bad credits
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.incorrect_activity_login), true)));

        onView(withId(R.id.login_username)).perform(clearText()).perform(typeText(USERNAME_CORRECT));
        onView(withId(R.id.login_buttonLogin)).perform(click());

        onView(withId(R.id.login_username)).check(matches(isTextStyleCorrect("", false)));
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect("", false)));
    }
}