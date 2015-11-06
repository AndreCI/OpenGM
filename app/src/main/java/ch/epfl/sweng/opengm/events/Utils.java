package ch.epfl.sweng.opengm.events;

import java.util.Date;

public class Utils {

    public static Date stringToDate(String s) {
        String[] stringArray = s.split("-");
        if(stringArray.length != 5) {
            throw new IllegalArgumentException("StringToDate string format must be year-month-day-hour-minute");
        }
        int[] array = new int[stringArray.length];
        for(int i = 0; i < stringArray.length; ++i) {
            array[i] = Integer.parseInt(stringArray[i]);
        }
        return new Date(array[0],array[1]-1,array[2],array[3],array[4]);

    }

    public static String dateToString(Date d) {
        return String.format("%d-%d-%d-%d-%d", d.getYear(), d.getMonth()+1, d.getDate(), d.getHours(), d.getMinutes());
    }
}
