package ch.epfl.sweng.opengm.events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.Date;

public class Utils {

    public static final String GROUP_INTENT_MESSAGE = "ch.epfl.opengm.group_intent_message";
    public static final String EVENT_INTENT_MESSAGE = "ch.epfl.opengm.event_intent_message";
    public static final int DELETE_EVENT = 690;
    public static final int SHOWING_EVENT = 490;
    public static final int SILENCE = 830;
    public static final String EDIT_INTENT_MESSAGE = "ch.epfl.opengm.edit_intent_message";

    public static Date stringToDate(String s) {
        String[] stringArray = s.split("-");
        if(stringArray.length != 5) {
            throw new IllegalArgumentException("StringToDate " + s + " string format must be year-month-day-hour-minute");
        }
        int[] array = new int[stringArray.length];
        for(int i = 0; i < stringArray.length; ++i) {
            array[i] = Integer.parseInt(stringArray[i]);
        }
        return new Date(array[0],array[1]-1,array[2],array[3],array[4]);

    }

    public static String dateToString(Date d) {
        return String.format("%04d-%02d-%02d-%02d-%02d", d.getYear(), d.getMonth()+1, d.getDate(), d.getHours(), d.getMinutes());
    }

    public void displayImage(ParseFile image, final ImageView imageView){
        if (image != null) {
            image.getDataInBackground(new GetDataCallback() {

                @Override
                public void done(byte[] data, ParseException e) {

                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
                                data.length);

                        if (bmp != null) {

                            Log.e("parse file ok", " null");
                            // img.setImageBitmap(Bitmap.createScaledBitmap(bmp,
                            // (display.getWidth() / 5),
                            // (display.getWidth() /50), false));
                            imageView.setImageBitmap(bmp);
                            // img.setPadding(10, 10, 0, 0);


                        }
                    } else {
                        Log.e("paser after downloade", " null");
                    }

                }
            });
        } else {

            Log.e("parse file", " null");
        }
    }
}
