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

import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFMessage;

import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFMessage.*;

public class Utils {

    public static final String NOTIF_INTENT_MESSAGE = "ch.epfl.sweng.opengm.messages.notification";
    public static final String FILE_INFO_INTENT_MESSAGE = "ch.epfl.sweng.opengm.file_info";
    public static final String CONVERSATION_INFO_INTENT_MESSAGE = "ch.epfl.sweng.opengm.conv_info";


    public static List<PFMessage> getMessagesForConversationName(String conversation, List<ChatMessage> oldMessages) {
        int queryLimit = 25;
        List<PFMessage> messages = new ArrayList<>(queryLimit);
        try {
            List<String> messagesIds = new ArrayList<>();
            for (ChatMessage message : oldMessages) {
                messagesIds.add(message.getId());
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_NAME).
                    whereEqualTo(TABLE_ENTRY_NAME, conversation).
                    whereNotContainedIn(OBJECT_ID, messagesIds).
                    orderByDescending("createdAt");
            query.setLimit(queryLimit);
            List<ParseObject> objects = query.find();
            for (ParseObject object : objects) {
                messages.add(0, getExistingMessage(object.getObjectId(), object.getUpdatedAt(),
                        object.getString(TABLE_ENTRY_SENDER),
                        object.getString(TABLE_ENTRY_NAME),
                        object.getString(TABLE_ENTRY_BODY),
                        object.getString(TABLE_ENTRY_GROUPID)));
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
