package ch.epfl.sweng.opengm.messages;

import java.util.Date;

import ch.epfl.sweng.opengm.OpenGMApplication;

public class ChatMessage implements Comparable<ChatMessage> {
    private final String senderId;
    private final String body;
    private final long sendDate;
    private final boolean sent;


    public ChatMessage(String sender, String sendDate, String body) {
        this(sender, Long.parseLong(sendDate), body);

    }

    public ChatMessage(String sender, Long sendDate, String body) {
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
        return new Date(sendDate) + " : " + senderId + " - " + body;
    }

    public long getSendDate() {
        return sendDate;
    }

    @Override
    public int compareTo(ChatMessage another) {
        return Long.compare(sendDate, another.sendDate);
    }
}
