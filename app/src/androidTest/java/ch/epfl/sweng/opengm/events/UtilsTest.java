package ch.epfl.sweng.opengm.events;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UtilsTest {
    @Test
    public void legitDateShouldBeParsedCorrectly() {
        int year = 1994; int month = 5; int day = 6; int hour = 6; int min = 10;
        Date date = new Date(year, month, day, hour, min);
        assertEquals("1994-06-06-06-10", Utils.dateToString(date));
    }
    @Test
    public void LegitStringShouldProduceCorrectDate() {
        String s = "2012-12-21-12-05";
        Date date = Utils.stringToDate(s);
        assertEquals(2012, date.getYear());
        assertEquals(12, date.getMonth()+1);
        assertEquals(21, date.getDate());
        assertEquals(12, date.getHours());
        assertEquals(5, date.getMinutes());
    }
    @Test
    public void IllegalStringShouldProduceException() {
        String s = "42";
        try {
            Utils.stringToDate(s);
            fail("No IllegalArgumentException for 42");
        } catch (IllegalArgumentException e) {
            String s2 = "----";
            try {
                Utils.stringToDate(s2);
                fail("No IllegalArgumentException for ----");
            } catch (IllegalArgumentException e2) {
            }
        }
    }


}
