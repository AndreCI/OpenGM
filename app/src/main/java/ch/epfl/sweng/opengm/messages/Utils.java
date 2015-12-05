package ch.epfl.sweng.opengm.messages;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ch.epfl.sweng.opengm.parse.PFMessage;

/**
 * Created by virgile on 18/11/2015.
 */
public class Utils {
    public static final String FILE_INFO_INTENT_MESSAGE = "ch.epfl.weng.opengm.file_info";
    public static final String CONVERSATION_INFO_INTENT_MESSAGE = "ch.epfl.weng.opengm.conv_info";

    public static List<PFMessage> getMessagesForConversationName(String conversation) {
        List<PFMessage> messages = new ArrayList<>();
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(PFMessage.TABLE_NAME).whereEqualTo(PFMessage.TABLE_ENTRY_NAME, conversation).orderByAscending(PFMessage.TABLE_ENTRY_TIMESTAMP);
            Log.v("Utils getMessages", "query size: " + query.count());
            List<ParseObject> objects = query.find();
            for(ParseObject object : objects) {
                messages.add(PFMessage.getExistingMessage(object.getObjectId(), object.getUpdatedAt(),
                        (String) object.get(PFMessage.TABLE_ENTRY_SENDER),
                        (long) object.get(PFMessage.TABLE_ENTRY_TIMESTAMP),
                        (String) object.get(PFMessage.TABLE_ENTRY_NAME),
                        (String) object.get(PFMessage.TABLE_ENTRY_BODY),
                        (String) object.get(PFMessage.TABLE_ENTRY_GROUPID)));
            }
        } catch (ParseException e) {
        }
        return messages;
    }



    public static String getNewStringDate() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(Calendar.getInstance().getTime());
    }
}
