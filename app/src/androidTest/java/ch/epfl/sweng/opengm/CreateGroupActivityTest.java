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

import java.util.List;
import java.util.Random;

import ch.epfl.sweng.opengm.groups.CreateGroup;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
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

    public void testDeclinesNameWithBadChars() throws InterruptedException {
        onView(withId(R.id.enterGroupName)).perform(typeText("This//is//bad"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.doneGroupCreate)).perform(click());
        onView(withId(R.id.enterGroupName)).check(matches(hasErrorText("Group name contains illegal characters, only letters, numbers and spaces allowed.")));
    }

    public void testDeclinesNameStartingWithSpace() throws InterruptedException {
        onView(withId(R.id.enterGroupName)).perform(typeText(" Why would you start with  ?"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.doneGroupCreate)).perform(click());
        onView(withId(R.id.enterGroupName)).check(matches(hasErrorText("Group name cannot start with a space")));
    }

    public void testGoodGroupAddedToDatabase() throws InterruptedException, PFException {
        Random random = new Random();
        int groupNumber = random.nextInt(100);
        onView(withId(R.id.enterGroupName)).perform(typeText("Nice Group" + groupNumber));
        onView(withId(R.id.enterGroupDescription)).perform(typeText("Nice Description"));
        closeSoftKeyboard();
        Thread.sleep(1000);
        onView(withId(R.id.doneGroupCreate)).perform(click());

        currentUser = PFUser.fetchExistingUser(currentUser.getId());
        List<PFGroup> groups = currentUser.getGroups();
        boolean found = false;
        for(PFGroup group : groups){
            if(group.getName().equals("Nice Group" + groupNumber)){
                found = true;
            }
        }
        assertTrue(found);
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
