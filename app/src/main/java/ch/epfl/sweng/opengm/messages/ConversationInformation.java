package ch.epfl.sweng.opengm.messages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by virgile on 20/11/2015.
 */
public class ConversationInformation implements Parcelable {
    private String conversationName;
    private String groupId;
    private String filePath;

    public ConversationInformation(Parcel in) {
        conversationName = in.readString();
        groupId = in.readString();
        filePath = in.readString();
    }

    public ConversationInformation(String conversationName, String groupId, String textFilePath) {
        this.conversationName = conversationName;
        this.groupId = groupId;
        this.filePath = textFilePath;
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

        return "<|" + conversationName + '|' + groupId + '|' + filePath + "|>";
    }

    public String getConversationName() {
        return conversationName;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationName);
        dest.writeString(groupId);
        dest.writeString(filePath);
    }
}
