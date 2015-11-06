package ch.epfl.sweng.opengm;

import org.junit.Test;

import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;
import static org.junit.Assert.assertEquals;

/**
 * Created by virgile on 06/11/2015.
 */
public class UtilsTest {

    @Test
    public void StripAccentTest() {
        String input = "àâäéèêëîïìôöòûùÿç";
        String expecteed = "aaaeeeeiiiooouuyc";
        assertEquals(expecteed, stripAccents(input));
    }
}
