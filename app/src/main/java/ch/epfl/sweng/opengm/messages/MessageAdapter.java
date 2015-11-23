package ch.epfl.sweng.opengm.messages;

import java.util.Calendar;
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
        sendDate = getNewDate();
    }

    public String getSenderName() {
        return sender;
    }

    public String getMessage() {
        return body;
    }

    private Date getNewDate() {
        Calendar c = Calendar.getInstance();
        return new Date(c.YEAR, c.MONTH+1, c.DATE, c.HOUR_OF_DAY, c.MINUTE);
    }

    @Override
    public String toString() {
        return dateToString(sendDate) +" : "+sender+" - "+body;
    }
}
