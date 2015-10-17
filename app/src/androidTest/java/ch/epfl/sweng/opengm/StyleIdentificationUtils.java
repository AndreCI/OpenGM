package ch.epfl.sweng.opengm;

import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class StyleIdentificationUtils {

    public static TypeSafeMatcher<View> isTextStyleCorrect(final String expectedString, boolean checkFocus) {
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
