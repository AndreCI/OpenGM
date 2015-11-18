package ch.epfl.sweng.opengm.messages;

/**
 * Created by virgile on 18/11/2015.
 */
public class ConversationAdapter {
    private String title;
    private boolean newMessage;

    public ConversationAdapter(String title) {
        this(title, false);
    }

    public ConversationAdapter(String title, boolean newMessage) {
        this.title = title;
        this.newMessage = newMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public Boolean getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }
}
