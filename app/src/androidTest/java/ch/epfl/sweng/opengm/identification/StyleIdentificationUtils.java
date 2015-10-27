package ch.epfl.sweng.opengm.identification;

import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class StyleIdentificationUtils {

    public static TypeSafeMatcher<View> isTextStyleCorrect(final String expectedString, final boolean checkFocus) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View o) {

                if (o == null) {
                    return false;
                }
                EditText editText = (EditText) o;
                // if it does not have the focus, its error should be null
                if (!checkFocus) {
                    return editText.getError() == null;
                }
                return editText.getError().equals(expectedString) && editText.hasFocus();
            }

            public void describeTo(Description description) {
                description.appendText("The selected edit text has not the " +
                        "focus or does not contain the expected error");
            }

        };
    }

}
