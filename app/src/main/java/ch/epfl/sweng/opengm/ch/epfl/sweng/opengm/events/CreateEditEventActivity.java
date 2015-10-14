package ch.epfl.sweng.opengm.ch.epfl.sweng.opengm.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class CreateEditEventActivity extends AppCompatActivity {

    private Event editedEvent;
    private boolean editing;
    private List<Event.OpenGMMember> participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_event);

        //TODO : fill editedEvent, editing, participants with intent
    }

    protected void onCancelButtonClick() {
        //TODO : go back to where you belong !!
    }

    protected void onOkButtonClick() {
        //TODO : create intent + go back to event list
        if(legalArguments() && participants != null) {

        }
    }

    protected void onParticipantsTextClick() {
        //TODO : rename + crate intent + go to Add/Remove participants
        if(legalArguments()){

        }
    }

    private Event createEditEvent() {
        if(editing) {
            return editEvent();
        } else {
            return createEvent();
        }
    }

    private Event createEvent() {
        //TODO : create event with filled texts
        return new Event();
    }

    private Event editEvent() {
        //TODO : update event with filled texts
        return editedEvent;
    }

    /**
     *
     * @return an array of int with year at index 0, month between 0 and 11 at index 1 and day at index 2
     */
    private int[] getDateFromText() {
        String[] dateString = ((TextView) findViewById(R.id.EventDateText)).getText().toString().split("/");
        if (dateString.length != 3) {
            throw new IllegalArgumentException("invalid dateString format");
        }
        int year = Integer.parseInt(dateString[2]);
        int month = Integer.parseInt(dateString[1]) - 1; //because reasons java month are (0-11)
        int day = Integer.parseInt(dateString[0]);
        return new int[]{year, month, day};
    }

    /**
     *
     * @return true if all arguments except the list of participants are legal for building an event
     * display a toast while it's not.
     */
    private boolean legalArguments() {
        try {
            int[] dateArray = getDateFromText();
            Calendar currentDate = Calendar.getInstance();
            GregorianCalendar date = new GregorianCalendar(dateArray[0], dateArray[1], dateArray[2]);
            if (date.before(currentDate)) {
                throw new IllegalArgumentException("invalid date (prior to now)");
            }
            String name = ((TextView) findViewById(R.id.EventNameText)).getText().toString();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("name must be specified");
            }
            return true;
        } catch(IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
