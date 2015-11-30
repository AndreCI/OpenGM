package ch.epfl.sweng.opengm.parse;

import android.content.Context;
import android.os.Parcel;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.messages.ConversationInformation;
import ch.epfl.sweng.opengm.messages.Utils;

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

    private PFConversation(String id, Date modifiedDate, String conversationName, String groupId, ParseFile file) {
        super(id, TABLE_NAME, modifiedDate);
        this.conversationName = conversationName;
        this.groupId = groupId;
        this.file = file;
    }



    public static PFConversation createNewConversation(String conversationName, String groupId, Context context) throws FileNotFoundException, ParseException {
        ParseObject object = new ParseObject(TABLE_NAME);
        File newFile = new File(context.getFilesDir(), conversationName + "_" + groupId + ".txt");
        PrintWriter writer = new PrintWriter(newFile);
        writer.print("");
        writer.close();
        ParseFile file = new ParseFile(newFile);
        file.saveInBackground();
        object.put(TABLE_ENTRY_FILE, file);
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
        } catch (ParseException|IOException e) {
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
        while(localLine != null && remoteLine != null) {
            ConversationInformation localInformation = Utils.stringToConversationInformation(localLine);
            ConversationInformation remoteInformation = Utils.stringToConversationInformation(remoteLine);
            if(localInformation.getConversationName().equals(remoteInformation.getConversationName())) {
                strings.add(localLine);
                localLine = localReader.readLine();
                remoteLine = remoteReader.readLine();
            } else if(localInformation.getCreationDate().before(remoteInformation.getCreationDate())) {
                strings.add(localLine);
                localLine = localReader.readLine();
            } else {
                strings.add(remoteLine);
                remoteLine = remoteReader.readLine();
            }
        }
        while(localLine != null) {
            strings.add(localLine);
            localLine = localReader.readLine();
        }
        while(remoteLine != null) {
            strings.add(remoteLine);
            remoteLine = remoteReader.readLine();
        }
        for(String s : strings) {
            printWriter.println(s);
        }
    }


    public void updateToServer() throws PFException {
        updateToServer(TABLE_ENTRY_FILE);
    }

    @Override
    protected void updateToServer(final String entry) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_NAME);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    switch (entry) {
                        case TABLE_NAME:
                            object.put(TABLE_ENTRY_FILE, file);
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

    public ConversationInformation toConversationInformation() {
        return new ConversationInformation(conversationName, groupId, lastModified);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
