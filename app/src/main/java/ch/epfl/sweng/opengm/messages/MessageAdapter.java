package ch.epfl.sweng.opengm.messages;

import java.util.Date;

public class MessageAdapter {
    private final String senderId;
    private final String body;
    private final long sendDate;

    public MessageAdapter(String sender, String sendDate, String body) {
        this(sender, Long.parseLong(sendDate), body);

    }

    public MessageAdapter(String sender, Long sendDate, String body) {
        this.senderId = sender;
        this.body = body;
        this.sendDate = sendDate;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return body;
    }

    @Override
    public String toString() {
        return new Date(sendDate) + " : " + senderId + " - " + body;
    }

    public long getSendDate() {
        return sendDate;
    }
}
