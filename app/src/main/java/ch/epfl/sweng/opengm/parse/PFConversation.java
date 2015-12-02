package ch.epfl.sweng.opengm.parse;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.messages.Utils;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static ch.epfl.sweng.opengm.messages.Utils.getNewStringDate;

/**
 * Created by virgile on 27/11/2015.
 */
public class PFConversation extends PFEntity {
    private static final String TABLE_NAME = "Conversations";
    private static final String TABLE_ENTRY_FILE = "Messages";
    private static final String TABLE_ENTRY_NAME = "ConversationName";
    private static final String TABLE_ENTRY_GROUPID = "GroupId";
    String conversationName;
    String groupId;
    ParseFile file;

    public PFConversation(Parcel in) {
        super(in, TABLE_NAME);
        conversationName = in.readString();
        groupId = in.readString();
        File file = new File(in.readString(), in.readString());
        this.file = new ParseFile(file);
    }

    private PFConversation(String id, Date modifiedDate, String conversationName, String groupId, ParseFile file) {
        super(id, TABLE_NAME, modifiedDate);
        this.conversationName = conversationName;
        this.groupId = groupId;
        this.file = file;
    }

    public void writeMessage(String sender, String body) throws IOException, ParseException {
        File file = this.file.getFile();
        Log.v("PFConv writeMessage", "file path: " + file.getAbsolutePath() + " vs old: " + this.file.getName());
        PrintWriter out = new PrintWriter(new FileWriter(file, true));
        out.println(String.format("<|%s|%s|%s|>\n", sender, getNewStringDate(), body));
        this.file = new ParseFile(file);
        try {
            updateToServer();
        } catch (PFException e) {
            Log.e("PFConversation", "couldn't update on server");
        }
    }

    public void writeConversationInformation() throws PFException {
        try {
            File file = this.file.getFile();
            PrintWriter out = new PrintWriter(new FileWriter(file, true));
            out.println(String.format("<|%s|%s|%s|>\n", getId(), conversationName, groupId));
            this.file = new ParseFile(file);
            updateToServer();
        } catch (IOException|ParseException e) {
            throw new PFException(e);
        }
    }


    public static PFConversation createNewConversation(String conversationName, String groupId, Context context) throws FileNotFoundException, ParseException {
        ParseObject object = new ParseObject(TABLE_NAME);
        File newFile = new File(context.getFilesDir(), conversationName + "_" + groupId + ".txt");
        PrintWriter writer = new PrintWriter(newFile);
        writer.print("");
        writer.close();
        ParseFile file = new ParseFile(newFile);
        file.saveInBackground();
        object.put(TABLE_ENTRY_NAME, conversationName);
        object.put(TABLE_ENTRY_FILE, file);
        object.put(TABLE_ENTRY_GROUPID, groupId);
        object.save();
        return new PFConversation(object.getObjectId(), object.getUpdatedAt(), conversationName, groupId, file);
    }

    public static PFConversation fetchExistingConversation(String id) throws PFException {
        if (id == null) {
            throw new PFException("Id is null");
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_NAME);
        query.whereEqualTo(PFConstants.OBJECT_ID, id);
        try {
            ParseObject object = query.getFirst();
            if (object != null) {
                ParseFile file = object.getParseFile(TABLE_ENTRY_FILE);
                String conversationName = object.getString(TABLE_ENTRY_NAME);
                String groupId = object.getString(TABLE_ENTRY_GROUPID);
                return new PFConversation(id, object.getUpdatedAt(), conversationName, groupId, file);
            } else {
                throw new PFException("Parse query for id " + id + " failed");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new PFException("Parse query for id " + id + " failed");
        }
    }

    @Override
    public void reload() throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_NAME);
        try {
            ParseObject object = query.get(getId());
            if (hasBeenModified(object)) {
                setLastModified(object);
                mergeConflicts(file, object.getParseFile(TABLE_ENTRY_FILE));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    private void mergeConflicts(ParseFile file, ParseFile serverFile) throws ParseException, IOException {
        File newFile = new File(file.getFile().getAbsolutePath());
        PrintWriter printWriter = new PrintWriter(newFile);
        List<String> strings = new ArrayList<>();
        BufferedReader localReader = new BufferedReader(new InputStreamReader(file.getDataStream()));
        BufferedReader remoteReader = new BufferedReader(new InputStreamReader(serverFile.getDataStream()));
        String localLine = localReader.readLine();
        String remoteLine = remoteReader.readLine();
        while (localLine != null && remoteLine != null) {
            String localName = Utils.extractConversationName(localLine);
            String remoteName = Utils.extractConversationName(remoteLine);
            Date localDate = Utils.extractConversationDate(localLine);
            Date remoteDate = Utils.extractConversationDate(remoteLine);
            if (localName.equals(remoteName)) {
                strings.add(localLine);
                localLine = localReader.readLine();
                remoteLine = remoteReader.readLine();
            } else if (localDate.before(remoteDate)) {
                strings.add(localLine);
                localLine = localReader.readLine();
            } else {
                strings.add(remoteLine);
                remoteLine = remoteReader.readLine();
            }
        }
        while (localLine != null) {
            strings.add(localLine);
            localLine = localReader.readLine();
        }
        while (remoteLine != null) {
            strings.add(remoteLine);
            remoteLine = remoteReader.readLine();
        }
        for (String s : strings) {
            printWriter.println(s);
        }
    }

    @Override
    public String toString() {
        return String.format("<|%s|%s|%s|%s|>", getId(), dateToString(lastModified), conversationName, groupId);
    }

    public void updateToServer() throws PFException {
        updateToServer(TABLE_ENTRY_FILE);
    }

    @Override
    protected void updateToServer(final String entry) throws PFException {
        Log.v("PFConversation", "update to server");
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_NAME);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    switch (entry) {
                        case TABLE_NAME:
                            object.put(TABLE_ENTRY_FILE, file);
                            file.saveInBackground();
                            break;
                        default:
                            return;
                    }
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                // throw new ParseException("No object for the selected id.");
                            }
                        }
                    });
                } else {
                    // throw new ParseException("No object for the selected id.");
                }
            }
        });
    }

    public String getConversationName() {
        return conversationName;
    }

    public File getConversationFile() throws ParseException {
        return file.getFile();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(dateToString(lastModified));
        dest.writeString(conversationName);
        dest.writeString(groupId);
        try {
            dest.writeString(file.getFile().getPath());
            dest.writeString(file.getFile().getName());
        } catch (ParseException e) {
            Log.e("PFConversation", "couldn't write to parcel the file");
        }
    }

    public static final Creator<PFConversation> CREATOR = new Creator<PFConversation>() {
        @Override
        public PFConversation createFromParcel(Parcel in) {
            return new PFConversation(in);
        }

        @Override
        public PFConversation[] newArray(int size) {
            return new PFConversation[size];
        }
    };

    public String getGroupId() {
        return groupId;
    }
}
