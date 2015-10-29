package ch.epfl.sweng.opengm.identification;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;

import java.util.Calendar;

import ch.epfl.sweng.opengm.R;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private final static String CURRENT_DATE = "" + Calendar.getInstance().getTimeInMillis();

    private final static String USERNAME_INCORRECT = "user" + CURRENT_DATE;
    private final static String USERNAME_CORRECT = "TestAdmin";
    private final static String PASSWORD_CORRECT = "Abcdef12";

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testFields() {

        LoginActivity activity = getActivity();

        // empty username
        onView(ViewMatchers.withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_username)).check(matches(isTextStyleCorrect(activity.getString(R.string.emtpy_username_activity_register), true)));

        //empty password
        onView(withId(R.id.login_username)).perform(clearText()).perform(typeText(USERNAME_INCORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.empty_password_activity_register), true)));

        //short password
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText("a"));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_password_activity_login), true)));

        //long password
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_password_activity_login), true)));

        //all caps password
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText("AAAAAAAAAA"));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_password_activity_login), true)));

        //without caps password
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText("aaaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_password_activity_login), true)));

        //without number password
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText("Aaaaaaaaaaa"));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_password_activity_login), true)));

        //without letter password
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText("123456789"));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_password_activity_login), true)));

        //bad credits
        onView(withId(R.id.login_password)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());
        onView(withId(R.id.login_password)).check(matches(isTextStyleCorrect(activity.getString(R.string.incorrect_activity_login), true)));

        onView(withId(R.id.login_username)).perform(clearText()).perform(typeText(USERNAME_CORRECT));
        closeSoftKeyboard();
        onView(withId(R.id.login_buttonLogin)).perform(click());

        onView(withId(R.id.linearLayout_groupsOverview)).check(matches(isDisplayed()));

    }

    public void testPasswordPopup() {

        LoginActivity activity = getActivity();

        onView(ViewMatchers.withId(R.id.textView_password_forgotten)).perform(click());

        onView(ViewMatchers.withId(R.id.editTextMail_dialog_forgot)).perform(clearText()).perform(typeText("thisIsNotAnEmail")).perform(click());

        // TODO how to click on the positive button of our alert ?
    }
}