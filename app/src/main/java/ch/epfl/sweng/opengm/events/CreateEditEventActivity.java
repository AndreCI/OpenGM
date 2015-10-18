package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class CreateEditEventActivity extends AppCompatActivity {
    public final static String CREATE_EDIT_EVENT_MESSAGE = "ch.epfl.sweng.opengm.events.CREATE_EDIT_EVENT";
    private Event editedEvent;
    private boolean editing;
    private List<Event.OpenGMMember> participants;
    private boolean timeSet = false;
    private boolean dateSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_event);

        //TODO : fill editedEvent, editing, participants with intent + prefill text with existing values of event
        Intent intent = getIntent();
        Event event = (Event) intent.getSerializableExtra(ShowEventActivity.SHOW_EVENT_MESSAGE);
        if (event == null) {
            editing = false;
            participants = new ArrayList<>();
            timeSet = false;
            dateSet = false;
        } else {
            editedEvent = event;
            editing = true;
            participants = editedEvent.getParticipants();
            timeSet = true;
            dateSet = true;
            fillTexts(event);
        }
    }

    public void onOkButtonClick(View v) {
        if (legalArguments()) {
            if (participants != null) {
                Intent intent = new Intent(this, EventListActivity.class);
                intent.putExtra(CREATE_EDIT_EVENT_MESSAGE, createEditEvent());
                startActivity(intent);
            } else {
                Toast.makeText(this, "You must specify participants", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onParticipantsButtonClick(View v) {
        if (legalArguments()) {
            Intent intent = new Intent(this, AddRemoveParticipantsActivity.class);
            intent.putExtra(CREATE_EDIT_EVENT_MESSAGE, createEditEvent());
            startActivity(intent);
        }
    }

    private void fillTexts(Event event) {
        ((EditText) findViewById(R.id.CreateEditEventNameText)).setText(event.getName());
        ((EditText) findViewById(R.id.CreateEditEventPlaceText)).setText(event.getPlace());
        ((MultiAutoCompleteTextView) findViewById(R.id.CreateEditEventDescriptionText)).setText(event.getDescription());
        GregorianCalendar date = event.getDate();
        String dateString = Integer.toString(date.DAY_OF_MONTH) + '/' + Integer.toString(date.MONTH + 1) + '/' + Integer.toString(date.YEAR);
        ((EditText) findViewById(R.id.CreateEditEventDateText)).setText(dateString);
    }

    private Event createEditEvent() {
        if (editing) {
            return editEvent();
        } else {
            return createEvent();
        }
    }

    private Event createEvent() {
        int[] timeArray = getTimeFromText();
        int[] dateArray = getDateFromText();
        GregorianCalendar date = new GregorianCalendar(dateArray[0], dateArray[1]-1, dateArray[2], timeArray[0], timeArray[1]);
        String name = ((TextView) findViewById(R.id.CreateEditEventNameText)).getText().toString();
        String description = ((TextView) findViewById(R.id.CreateEditEventDescriptionText)).getText().toString();
        String place = ((TextView) findViewById(R.id.CreateEditEventPlaceText)).getText().toString();
        return new Event(name, place, date, description, participants);
    }

    private Event editEvent() {
        int[] timeArray = getTimeFromText();
        int[] dateArray = getDateFromText();
        GregorianCalendar date = new GregorianCalendar(dateArray[0], dateArray[1]-1, dateArray[2], timeArray[0], timeArray[1]);
        String name = ((TextView) findViewById(R.id.CreateEditEventNameText)).getText().toString();
        String description = ((TextView) findViewById(R.id.CreateEditEventDescriptionText)).getText().toString();
        String place = ((TextView) findViewById(R.id.CreateEditEventPlaceText)).getText().toString();
        editedEvent.setName(name);
        editedEvent.setDate(date);
        editedEvent.setDescription(description);
        editedEvent.setPlace(place);
        return editedEvent;
    }

    /**
     * @return an array of int with hours(24h format) at index 0 and minutes at index 1
     */
    private int[] getTimeFromText() {
        TextView textView = (TextView) findViewById(R.id.CreateEditEventTimeText);
        String[] timeString = textView.getText().toString().split(":");
        if (timeString.length != 2) {
            timeSet = false;
            textView.setError("Invalid hour format, must be hh:mm");
            return new int[]{-1, -1};
        }
        timeSet = true;
        int hours = Integer.parseInt(timeString[0]);
        int minutes = Integer.parseInt(timeString[1]);
        ((EditText) findViewById(R.id.CreateEditEventDescriptionText)).setText(timeString[0] + " - " + Integer.toString(hours));
        return new int[]{hours, minutes};
    }

    /**
     * @return an array of int with year at index 0, month at index 1 and day at index 2
     */
    private int[] getDateFromText() {
        TextView textView = (TextView) findViewById(R.id.CreateEditEventDateText);
        String[] dateString = textView.getText().toString().split("/");
        if (dateString.length != 3) {
            dateSet = false;
            textView.setError("Invalid date format, must be dd/mm/yyyy");
            return new int[]{-1, -1, -1};
        }
        dateSet = true;
        int year = Integer.parseInt(dateString[2]);
        int month = Integer.parseInt(dateString[1]);
        int day = Integer.parseInt(dateString[0]);
        return new int[]{year, month, day};
    }

    /**
     * @return true if all arguments except the list of participants are legal for building an event
     * display a toast while it's not.
     */
    private boolean legalArguments() {
        EditText eventNameText = (EditText) findViewById(R.id.CreateEditEventNameText);
        String name = eventNameText.getText().toString();
        if (name.isEmpty()) {
            eventNameText.setError("Event name should not be empty");
            return false;
        }
        Calendar currentDate = Calendar.getInstance();
        int[] timeArray = getTimeFromText();
        int[] dateArray = getDateFromText();

        if(!timeSet || !dateSet) {
            if (!timeSet) {
                ((TextView) findViewById(R.id.CreateEditEventTimeText)).setError("Time must be specified");
            }
            if (!dateSet) {
                ((TextView) findViewById(R.id.CreateEditEventDateText)).setError("Date must be specified");
            }
            return false;
        }

        //GregorianCalendar date = new GregorianCalendar(dateArray[0], dateArray[1]-1, dateArray[2], timeArray[0], timeArray[1]);
        GregorianCalendar date = buildCalendar(timeArray, dateArray);
        boolean err = false;
        if((timeArray[0] == -1 && timeArray[1] == -1) || date.HOUR_OF_DAY != timeArray[0] || date.MINUTE != timeArray[1]) {
            ((TextView) findViewById(R.id.CreateEditEventTimeText)).setError("Invalid Time "+  Integer.toString(date.HOUR_OF_DAY) +"!="+ timeArray[0]+" "+ Integer.toString(date.MINUTE) +"!="+ timeArray[1]);
            err = true;
        }
        if((dateArray[0] == -1 && dateArray[1] == -1 && dateArray[2] == -1) || date.YEAR != dateArray[0] || date.MONTH != dateArray[1]-1 || date.DAY_OF_MONTH != dateArray[2]) {
            ((TextView) findViewById(R.id.CreateEditEventDateText)).setError("Invalid date " + date.YEAR +"!="+ dateArray[0]+" "+  date.MONTH +"!="+ (dateArray[1]-1)+" "+  date.DAY_OF_MONTH +"!="+ dateArray[2]);
            err = true;
        }
        if (date.before(currentDate)) {
            ((TextView) findViewById(R.id.CreateEditEventDateText)).setError("Invalid date (prior to now)");
            err = true;
        }
        return !err;
    }

    private GregorianCalendar buildCalendar(int[] timeArray, int[] dateArray) {
        GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
        cal.set(Calendar.YEAR, dateArray[0]);
        cal.set(Calendar.MONTH, dateArray[1]-1);
        cal.set(Calendar.DATE, dateArray[2]);
        cal.set(Calendar.HOUR_OF_DAY, timeArray[0]);
        cal.set(Calendar.MINUTE, timeArray[1]);
        
        return cal;
    }
}
