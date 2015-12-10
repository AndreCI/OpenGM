package ch.epfl.sweng.opengm.messages;

import java.text.DateFormat;
import java.util.Date;

public class Utils {

    public static final String NOTIF_INTENT_MESSAGE = "ch.epfl.sweng.opengm.messages.notification";
    public static final String FILE_INFO_INTENT_MESSAGE = "ch.epfl.sweng.opengm.file_info";
    public static final String CONVERSATION_INFO_INTENT_MESSAGE = "ch.epfl.sweng.opengm.conv_info";

    public static String getDateFromTimestamp(long timestamp) {
        return DateFormat.getDateTimeInstance().format(new Date(timestamp));
    }

}
