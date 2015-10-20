package ch.epfl.sweng.opengm.events;

/**
 * Created by virgile on 16/10/2015.
 */

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.test.ActivityInstrumentationTestCase2;

import ch.epfl.sweng.opengm.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class CreateEditEventTest extends ActivityInstrumentationTestCase2<ShowEventActivity> {
    public CreateEditEventTest() {
        super(ShowEventActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testNoName() {
        getActivity();
        ViewInteraction nameText = onView(withId(R.id.CreateEditEventNameText));
        nameText.perform(clearText());
        onView(withId(R.id.CreateEditOkButton)).perform(click());
        //nameText.check(matches(hasErrorText("Event name should not be empty")));
    }

}
