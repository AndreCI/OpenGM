package ch.epfl.sweng.opengm;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import ch.epfl.sweng.opengm.groups.CreateGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static org.hamcrest.Matchers.is;

public class CreateGroupActivityTest extends ActivityInstrumentationTestCase2<CreateGroup>{
    Activity createGroupActivity;
    PFUser currentUser;

    public CreateGroupActivityTest() {
        super(CreateGroup.class);
    }

    public void setUp() throws Exception{
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        createGroupActivity = getActivity();
        currentUser = PFUser.fetchExistingUser("f9PMNCFLXN");
        OpenGMApplication.setCurrentUser(currentUser.getId());
    }

    public void testDeclinesTooShortName() throws InterruptedException {
        onView(withId(R.id.enterGroupName)).perform(typeText("sh"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.doneGroupCreate)).perform(click());
        onView(withId(R.id.enterGroupName)).check(matches(hasErrorText("Group name is too short")));
    }

    public void testDeclinesTooLongName() throws InterruptedException {
        onView(withId(R.id.enterGroupName)).perform(typeText("thisisasuperlonggroupnameitsimpossiblesomeonewouldwanttowritesuchalonginfactverylonggroupname"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.doneGroupCreate)).perform(click());
        onView(withId(R.id.enterGroupName)).check(matches(hasErrorText("Group name is too long")));
    }

    

    private BaseMatcher<View> hasErrorText(final String expectedError){
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(TextView textView) {
                return expectedError.equals(textView.getError().toString());
            }
        };
    }
}
