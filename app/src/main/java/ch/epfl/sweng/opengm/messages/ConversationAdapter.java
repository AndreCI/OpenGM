package ch.epfl.sweng.opengm.messages;

/**
 * Created by virgile on 18/11/2015.
 */
public class ConversationAdapter {
    private ConversationInformation conversationInformation;

    public ConversationAdapter(ConversationInformation conversationInformation) {
        this.conversationInformation = conversationInformation;
    }

    public String getTitle() {
        return conversationInformation.getConversationName();
    }
}
