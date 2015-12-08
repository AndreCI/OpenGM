package ch.epfl.sweng.opengm.messages;

import java.util.Date;

import ch.epfl.sweng.opengm.OpenGMApplication;

public class ChatMessage implements Comparable<ChatMessage> {
    private final String id;
    private final String senderId;
    private final String body;
    private final Date sendDate;
    private final boolean sent;

    public ChatMessage(String id, String sender, Date sendDate, String body) {
        this.id = id;
        this.senderId = sender;
        this.body = body;
        this.sendDate = sendDate;
        this.sent = sender.equals(OpenGMApplication.getCurrentUser().getId());
    }

    public boolean wasSent() {
        return sent;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return body;
    }

    @Override
    public String toString() {
        return sendDate + " : " + senderId + " - " + body;
    }

    public Date getSendDate() {
        return sendDate;
    }

    @Override
    public int compareTo(ChatMessage another) {
        return sendDate.compareTo(another.sendDate);
    }

    public String getId() {
        return id;
    }
}
