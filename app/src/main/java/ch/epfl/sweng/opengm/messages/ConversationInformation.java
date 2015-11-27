package ch.epfl.sweng.opengm.messages;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by virgile on 20/11/2015.
 */
public class ConversationInformation implements Parcelable {
    private String conversationName;
    private String groupId;
    private Calendar creationDate;

    public ConversationInformation(Parcel in) {
        conversationName = in.readString();
        groupId = in.readString();
    }

    public ConversationInformation(String conversationName, String groupId) {
        this.conversationName = conversationName;
        this.groupId = groupId;
        creationDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    }

    public ConversationInformation(String conversationName, String groupId, Date creationDate) {
        this.conversationName = conversationName;
        this.groupId = groupId;
        this.creationDate = Calendar.getInstance();
        this.creationDate.setTime(creationDate);
    }

    public static final Creator<ConversationInformation> CREATOR = new Creator<ConversationInformation>() {
        @Override
        public ConversationInformation createFromParcel(Parcel in) {
            return new ConversationInformation(in);
        }

        @Override
        public ConversationInformation[] newArray(int size) {
            return new ConversationInformation[size];
        }
    };

    @Override
    public String toString() {

        return "<|" + conversationName + '|' + groupId + "|>";
    }

    public String getConversationName() {
        return conversationName;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationName);
        dest.writeString(groupId);
    }
}
