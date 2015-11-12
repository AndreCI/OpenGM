package ch.epfl.sweng.opengm.identification;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.identification.phoneNumber.CountryCode;
import ch.epfl.sweng.opengm.identification.phoneNumber.PhoneAddingActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.opengm.identification.StyleIdentificationUtils.isTextStyleCorrect;

public class PhoneActivityTest extends ActivityInstrumentationTestCase2<PhoneAddingActivity> {

    private PhoneAddingActivity activity;


    public PhoneActivityTest() {
        super(PhoneAddingActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testCountryCodeHand() {
        activity = getActivity();
        HashMap<String, String> codes = new HashMap<>();
        codes.put("1", "United States");
        codes.put("33", "France");
        codes.put("34", "Spain");
        codes.put("35", activity.getString(R.string.invalid_phone_number));
        codes.put("41", "Switzerland");
        codes.put("167", activity.getString(R.string.invalid_phone_number));
        codes.put("1234", activity.getString(R.string.invalid_phone_number));
        codes.put("*+", activity.getString(R.string.invalid_phone_number));

        for (Map.Entry<String, String> entry : codes.entrySet()) {
            onView(withId(R.id.country_code)).perform(clearText()).perform(replaceText(entry.getKey()));
            onView(withId(R.id.country_name)).check(matches(withText(entry.getValue())));
        }
    }

    public void testCountryCodeList() {
        activity = getActivity();
        onView(withId(R.id.country_name)).perform(click());

        onView(withText(R.string.title_phone_number)).inRoot(isDialog()).check(matches(isDisplayed()));

        onView(withId(R.id.country_code_layout)).check(matches(isListViewWorking()));

        onView(withId(R.id.country_code)).check(matches(withText("+352")));
        onView(withId(R.id.country_name)).check(matches(withText("Luxembourg")));

    }

    public void testBadNumber() {
        activity = getActivity();
        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("0"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("00"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("000"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("00000"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("000000"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("0000000"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("a"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("@"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("aaaaaaaaaa"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("000000000000000"));
        onView(withId(R.id.action_phone_number_validate)).perform(click());
        onView(withId(R.id.input_phoneNumber)).check(matches(isTextStyleCorrect(activity.getString(R.string.invalid_phone_input), true)));

        onView(withId(R.id.input_phoneNumber)).perform(clearText()).perform(replaceText("0000000000"));
    }

    public void testInformationDialog() {
        activity = getActivity();
        onView(withId(R.id.action_phone_number_help)).perform(click());
        onView(withText(R.string.information_phone_number)).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    private TypeSafeMatcher<View> isListViewWorking() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(View item) {
                ListView list = (ListView) item.findViewById(R.id.listView_coutrycodes);
                int nOfItems = list.getAdapter().getCount();
                SearchView search = (SearchView) item.findViewById(R.id.filterCodeCountry);
                search.setQuery("Fr", false);
                boolean bFr = list.getAdapter().getCount() == 4 && list.getAdapter().getCount() < nOfItems;
                search.setQuery("France", false);
                CountryCode cc = new CountryCode("33;France;/");
                assertEquals(cc, list.getItemAtPosition(0));
                boolean bFrance = list.getAdapter().getCount() == 1 && list.getAdapter().getCount() < nOfItems;
                search.setQuery("@€", false);
                boolean bSymbols = list.getAdapter().getCount() == 0 && list.getAdapter().getCount() < nOfItems;
                search.setQuery("Luxembourg", false);
                boolean bFJapan = list.getAdapter().getCount() == 1 && list.getAdapter().getCount() < nOfItems;
                int click = 0;
                list.performItemClick(list.getAdapter().getView(click, null, null), click, list.getAdapter().getItemId(click));
                return bFr && bFrance && bSymbols && bFJapan;
            }
        };
    }

}
