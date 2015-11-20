package ch.epfl.sweng.opengm.messages;

import java.util.Date;

/**
 * Created by virgile on 20/11/2015.
 */
public class MessageAdapter {
    private String sender;
    private String body;
    private Date sendDate;

    public MessageAdapter(String sender, String body, Date sendDate) {
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
}
