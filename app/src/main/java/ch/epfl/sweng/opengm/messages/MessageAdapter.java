package ch.epfl.sweng.opengm.messages;

/**
 * Created by virgile on 20/11/2015.
 */
public class MessageAdapter {
    private String sender;
    private String body;
    private Long sendDate;

    public MessageAdapter(String sender, Long sendDate, String body) {
        this.sender = sender;
        this.body = body;
        this.sendDate = sendDate;
    }

    public Long getSendDate() {
        return sendDate;
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
