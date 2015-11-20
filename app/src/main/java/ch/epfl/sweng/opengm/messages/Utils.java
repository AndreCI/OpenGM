package ch.epfl.sweng.opengm.messages;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;

/**
 * Created by virgile on 18/11/2015.
 */
public class Utils {
    public static final String FILE_INFO_INTENT_MESSAGE = "ch.epfl.weng.opengm.file_info";

    public static void writeMessageLocal(String pathToFile, MessageAdapter messageAdapter, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(pathToFile, Context.MODE_APPEND));
            outputStreamWriter.write(createNewMessage(messageAdapter));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static String createNewMessage(MessageAdapter messageAdapter) {
        return String.format("<<<|%s|%s|%s>>>", messageAdapter.getSenderName(), dateToString(new Date()),messageAdapter.getMessage());
    }

    public static void writeMessageServeur(ConversationInformation conversationInformation) {
        //TODO: check if remote file is older than local, if true upload, else merge the two.
    }

}
