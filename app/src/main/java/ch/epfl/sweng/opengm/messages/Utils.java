package ch.epfl.sweng.opengm.messages;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;

/**
 * Created by virgile on 18/11/2015.
 */
public class Utils {
    public static final String FILE_INFO_INTENT_MESSAGE = "ch.epfl.weng.opengm.file_info";
    public static final String CONVERSATION_INFO_INTENT_MESSAGE = "ch.epfl.weng.opengm.conv_info";

    public static void writeMessageLocal(String fileName, MessageAdapter messageAdapter, Context context) {
        try {
            Log.v("Utils", fileName+" : " + messageAdapter.toString());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            outputStreamWriter.write(createNewMessage(messageAdapter));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void writeConversationInformationLocal(String fileName, ConversationInformation conversationInformation, Context context) {
        try {
            Log.v("Utils writeConvInf", fileName+" : " + conversationInformation.toString());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            outputStreamWriter.write(conversationInformation.toString()+'\n');
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static String createNewMessage(MessageAdapter messageAdapter) {
        return String.format("<|%s|%s|%s|>\n", messageAdapter.getSenderName(), dateToString(new Date()),messageAdapter.getMessage());
    }

    public static void writeMessageServeur(ConversationInformation conversationInformation) {
        //TODO: check if remote file is older than local, if true upload, else merge the two.
    }

    public static List<String> readMessagesFile(String filePath) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        result.add(bufferedReader.readLine()); //get conv info string
        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            Log.v("Utils readTxtFile", line);
            if(line.startsWith("<|") && line.endsWith("|>")) {
                result.add(line);
            } else if(line.startsWith("<|")) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(line);
                stringBuilder.append('\n');
            } else if (line.endsWith("|>")) {
                stringBuilder.append(line);
                result.add(stringBuilder.toString());
            } else {
                stringBuilder.append(line);
            }
            line = bufferedReader.readLine();
        }
        return result;
    }

    public static List<ConversationInformation> readIndexFile(String filePath) throws IOException {
        List<ConversationInformation> result = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String line = bufferedReader.readLine();
        while (line != null) {
            Log.v("Utils readIndexFile", line);
            result.add(stringToConversationInformation(line));
            line = bufferedReader.readLine();
        }
        return result;
    }

    public static String[] extractMessage(String s) {
        String[] split = s.split("\\|");
        if(split.length != 5) {
            throw new IllegalArgumentException("Message should be formated <|sender|sendDate|body|>, was " + s);
        }
        String[] result = new String[3];
        result[0] = split[1];
        result[1] = split[2];
        result[2] = split[3];
        return result;
    }

    public static ConversationInformation stringToConversationInformation(String s) {
        String[] strings = s.split("\\|");
        if(strings.length != 5) {
            throw new IllegalArgumentException(strings.length + " Invalid string format, should be <|convName|groupId|path|>, was " + s);
        }
        return new ConversationInformation(strings[1], strings[2]);
    }


    public static String getNewStringDate() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(Calendar.getInstance().getTime());
    }
}
