package ch.epfl.sweng.opengm.messages;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ch.epfl.sweng.opengm.parse.PFMessage;

public class Utils {
    public static final String FILE_INFO_INTENT_MESSAGE = "ch.epfl.sweng.opengm.file_info";
    public static final String CONVERSATION_INFO_INTENT_MESSAGE = "ch.epfl.sweng.opengm.conv_info";


    public static List<PFMessage> getMessagesForConversationName(String conversation, long lastTimestamp) {
        List<PFMessage> messages = new ArrayList<>();
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(PFMessage.TABLE_NAME).
                    whereGreaterThan(PFMessage.TABLE_ENTRY_TIMESTAMP, lastTimestamp).
                    whereEqualTo(PFMessage.TABLE_ENTRY_NAME, conversation).
                    orderByAscending(PFMessage.TABLE_ENTRY_TIMESTAMP);
            query.setLimit(25);
            List<ParseObject> objects = query.find();
            for (ParseObject object : objects) {
                messages.add(PFMessage.getExistingMessage(object.getObjectId(), object.getUpdatedAt(),
                        object.getString(PFMessage.TABLE_ENTRY_SENDER),
                        object.getLong(PFMessage.TABLE_ENTRY_TIMESTAMP),
                        object.getString(PFMessage.TABLE_ENTRY_NAME),
                        object.getString(PFMessage.TABLE_ENTRY_BODY),
                        object.getString(PFMessage.TABLE_ENTRY_GROUPID)));
            }
        } catch (ParseException e) {
        }
        return messages;
    }

    public static String getDateFromTimestamp(long timestamp) {
        return DateFormat.getDateTimeInstance().format(new Date(timestamp));
    }

    public static long getTimestamp() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
    }

}
