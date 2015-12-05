package ch.epfl.sweng.opengm.messages;

/**
 * Created by virgile on 20/11/2015.
 */
public class MessageAdapter {
    private String sender;
    private String body;
    private String sendDate;

    public MessageAdapter(String sender, String sendDate, String body) {
        this.sender = sender;
        this.body = body;
        this.sendDate = sendDate;
    }

    public String getSenderName() {
        return sender;
    }

    public String getMessage() {
        return body;
    }

    @Override
    public String toString() {
        return sendDate +" : "+sender+" - "+body;
    }
}
