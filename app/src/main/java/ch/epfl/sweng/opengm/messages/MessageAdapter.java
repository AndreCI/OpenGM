package ch.epfl.sweng.opengm.messages;

import java.util.Date;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;

/**
 * Created by virgile on 20/11/2015.
 */
public class MessageAdapter {
    private String sender;
    private String body;
    private Date sendDate;

    public MessageAdapter(String sender, Date sendDate, String body) {
        this.sender = sender;
        this.body = body;
        this.sendDate = sendDate;
    }

    public MessageAdapter(String sender, String body) {
        this(sender, null, body);
        sendDate = Utils.getNewDate();
    }

    public String getSenderName() {
        return sender;
    }

    public String getMessage() {
        return body;
    }

    @Override
    public String toString() {
        return dateToString(sendDate) +" : "+sender+" - "+body;
    }
}
