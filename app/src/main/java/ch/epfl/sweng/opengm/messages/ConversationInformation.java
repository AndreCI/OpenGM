package ch.epfl.sweng.opengm.messages;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by virgile on 20/11/2015.
 */
public class ConversationInformation implements Parcelable {
    private String conversationId;
    private String conversationName;
    private String groupId;
    private String textFilePath;

    public ConversationInformation(Parcel in) {
        conversationId = in.readString();
        conversationName = in.readString();
        groupId = in.readString();
        textFilePath = in.readString();
    }

    public ConversationInformation(String conversationId, String conversationName, String groupId, String textFilePath) {
        this.conversationId = conversationId;
        this.conversationName = conversationName;
        this.groupId = groupId;
        this.textFilePath = textFilePath;
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

    public String getConversationId() {
        return conversationId;
    }

    public String getConversationName() {
        return conversationName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTextFilePath() {
        return textFilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean upToDate() {
        //TODO: implement
        return false;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conversationId);
        dest.writeString(conversationName);
        dest.writeString(groupId);
        dest.writeString(textFilePath);
    }
}
