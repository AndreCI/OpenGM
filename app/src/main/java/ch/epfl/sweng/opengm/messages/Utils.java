package ch.epfl.sweng.opengm.messages;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
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
import static ch.epfl.sweng.opengm.events.Utils.stringToDate;

/**
 * Created by virgile on 18/11/2015.
 */
public class Utils {
    public static final String FILE_INFO_INTENT_MESSAGE = "ch.epfl.weng.opengm.file_info";
    public static final String CONVERSATION_INFO_INTENT_MESSAGE = "ch.epfl.weng.opengm.conv_info";

    public static void writeMessageLocal(String sender, String body, String conversationName, String groupid, Context context) {
        try {
            Log.v("Utils write local mes", context.getFilesDir().getAbsolutePath() + '/' + conversationName + '_' + groupid + ".txt" + " : " + sender + " - " + body);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(conversationName + '_' + groupid + ".txt", Context.MODE_APPEND));
            outputStreamWriter.write(createNewMessage(sender, body));
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static String createNewMessage(String sender, String body) {
        return String.format("<|%s|%s|%s|>\n", sender, getNewStringDate(), body);
    }

    public static List<String> readMessagesFile(String filePath) throws IOException {
        File file = new File(filePath);
        return readMessageFile(file);
        /*List<String> result = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        result.add(bufferedReader.readLine()); //get conv info string
        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            Log.v("Utils readTxtFile", line);
            if (line.startsWith("<|") && line.endsWith("|>")) {
                result.add(line);
            } else if (line.startsWith("<|")) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(line);
                stringBuilder.append('\n');
            } else if (line.endsWith("|>")) {
                stringBuilder.append(line);
                result.add(stringBuilder.toString());
            } else {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            line = bufferedReader.readLine();
        }
        return result;*/
    }

    public static List<String> readMessageFile(File file) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        result.add(bufferedReader.readLine()); //get conv info string
        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            Log.v("Utils readTxtFile", line);
            if (line.startsWith("<|") && line.endsWith("|>")) {
                result.add(line);
            } else if (line.startsWith("<|")) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(line);
                stringBuilder.append('\n');
            } else if (line.endsWith("|>")) {
                stringBuilder.append(line);
                result.add(stringBuilder.toString());
            } else {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            line = bufferedReader.readLine();
        }
        return result;
    }

    public static String[] extractMessage(String s) {
        String[] split = s.split("\\|");
        if (split.length != 5) {
            throw new IllegalArgumentException("Message should be formated <|sender|sendDate|body|>, was " + s);
        }
        String[] result = new String[3];
        result[0] = split[1];
        result[1] = split[2];
        result[2] = split[3];
        return result;
    }

    public static String calendarToString(Calendar calendar) {
        return Integer.toString(calendar.YEAR) + '|' + Integer.toString(calendar.MONTH + 1) + '|' + Integer.toString(calendar.DATE) + '|' + Integer.toString(calendar.HOUR_OF_DAY) + '|' + Integer.toString(calendar.MINUTE) + '|' + Integer.toString(calendar.SECOND);
    }

    public static String getNewStringDate() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public static String extractConversationName(String s) {
        String[] strings = s.split("|");
        if (strings.length != 6) {
            throw new IllegalArgumentException("Invalid string format: " + s);
        }
        return strings[3];
    }

    public static Date extractConversationDate(String s) {
        String[] strings = s.split("|");
        if (strings.length != 6) {
            throw new IllegalArgumentException("Invalid string format: " + s);
        }
        return stringToDate(strings[2]);
    }
}
