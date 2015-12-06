package ch.epfl.sweng.opengm.parse;

import android.os.Parcel;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;

public class PFMessage extends PFEntity {
    public static final String TABLE_NAME = "Conversations";
    public static final String TABLE_ENTRY_SENDER = "Sender";
    public static final String TABLE_ENTRY_TIMESTAMP = "Timestamp";
    public static final String TABLE_ENTRY_NAME = "ConversationName";
    public static final String TABLE_ENTRY_BODY = "Body";
    public static final String TABLE_ENTRY_GROUPID = "GroupId";
    String senderId;
    Long timestamp;
    String conversationName;
    String body;
    String groupId;

    public String getSenderId() {
        return senderId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getConversationName() {
        return conversationName;
    }

    public String getBody() {
        return body;
    }

    public PFMessage(Parcel in) {
        super(in, TABLE_NAME);
        senderId = in.readString();
        timestamp = in.readLong();
        conversationName = in.readString();
        body = in.readString();
        groupId = in.readString();
    }

    private PFMessage(String id, Date modifiedDate, String senderId, Long timestamp, String conversationName, String body, String groupId) {
        super(id, TABLE_NAME, modifiedDate);
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.conversationName = conversationName;
        this.body = body;
        this.groupId = groupId;
    }

    public static PFMessage getExistingMessage(String id, Date modifiedDate, String sender, Long timestamp, String conversationName, String body, String groupId) {
        return new PFMessage(id, modifiedDate, sender, timestamp, conversationName, body, groupId);
    }

    public static PFMessage writeMessage(String conversationName, String groupId, String senderId, String body) throws IOException, PFException, ParseException {
        ParseObject object = new ParseObject(TABLE_NAME);
        long timestamp = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
        object.put(TABLE_ENTRY_BODY, body);
        object.put(TABLE_ENTRY_SENDER, senderId);
        object.put(TABLE_ENTRY_TIMESTAMP, timestamp);
        object.put(TABLE_ENTRY_NAME, conversationName);
        object.put(TABLE_ENTRY_GROUPID, groupId);
        object.save();
        return new PFMessage(object.getObjectId(), object.getUpdatedAt(), senderId, timestamp, conversationName, body, groupId);
    }

    @Override
    public void reload() throws PFException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void updateToServer(final String entry) throws PFException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(dateToString(lastModified));
        dest.writeString(senderId);
        dest.writeLong(timestamp);
        dest.writeString(conversationName);
        dest.writeString(body);
        dest.writeString(groupId);
    }

    public static final Creator<PFMessage> CREATOR = new Creator<PFMessage>() {
        @Override
        public PFMessage createFromParcel(Parcel in) {
            return new PFMessage(in);
        }

        @Override
        public PFMessage[] newArray(int size) {
            return new PFMessage[size];
        }
    };

    public String getGroupId() {
        return groupId;
    }
}