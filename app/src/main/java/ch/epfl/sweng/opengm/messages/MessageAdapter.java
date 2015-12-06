package ch.epfl.sweng.opengm.messages;

public class MessageAdapter {
    private String senderId;
    private String body;
    private String sendDate;

    public MessageAdapter(String sender, String sendDate, String body) {
        this.senderId = sender;
        this.body = body;
        this.sendDate = sendDate;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return body;
    }

    @Override
    public String toString() {
        return sendDate +" : "+ senderId +" - "+body;
    }
}
