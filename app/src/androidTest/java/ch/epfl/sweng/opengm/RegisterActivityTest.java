package ch.epfl.sweng.opengm;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseUser;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;

import ch.epfl.sweng.opengm.identification.RegisterActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

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
        onView(withId(R.id.register_username)).check(matches(isStyleCorrect(activity.getString(R.string.emtpy_username_activity_register))));

        //empty password1
        onView(withId(R.id.register_username)).perform(clearText()).perform(typeText(USERNAME));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isStyleCorrect(activity.getString(R.string.empty_password_activity_register))));

        //empty password2
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password2)).check(matches(isStyleCorrect(activity.getString(R.string.empty_password_activity_register))));

        //empty firstname
        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_INCORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_firstname)).check(matches(isStyleCorrect(activity.getString(R.string.empty_firstname_activity_register))));

        //empty lastname
        onView(withId(R.id.register_firstname)).perform(clearText()).perform(typeText(FIRSTNAME));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_lastname)).check(matches(isStyleCorrect(activity.getString(R.string.empty_lastname_activity_register))));

        //empty email
        onView(withId(R.id.register_lastname)).perform(clearText()).perform(typeText(LASTRNAME));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isStyleCorrect(activity.getString(R.string.empty_email_activity_register))));

        //bad email
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_INCORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_email)).check(matches(isStyleCorrect(activity.getString(R.string.incorrect_email_activity_register))));

        //bad password
        onView(withId(R.id.register_email)).perform(clearText()).perform(typeText(EMAIL_CORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isStyleCorrect(activity.getString(R.string.short_password_activity_register))));

        //password not correct
        onView(withId(R.id.register_password1)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        onView(withId(R.id.button_signup)).perform(click());
        onView(withId(R.id.register_password1)).check(matches(isStyleCorrect(activity.getString(R.string.incorrect_password_activity_register))));

        onView(withId(R.id.register_password2)).perform(clearText()).perform(typeText(PASSWORD_CORRECT));
        onView(withId(R.id.button_signup)).perform(click());

    }


    private TypeSafeMatcher<View> isStyleCorrect(final String expectedString) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View o) {
                if (o == null) {
                    return false;
                }
                EditText editText = (EditText) o;
                return editText.hasFocus() && editText.getError().equals(expectedString);
            }

            public void describeTo(Description description) {
                description.appendText("The selected edit text has not the" +
                        "focus or does not contain the expected error");
            }

        };
    }


}
