package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class CreateEditEventActivity extends AppCompatActivity {
    public final static String CREATE_EDIT_EVENT_MESSAGE = "ch.epfl.sweng.opengm.events.CREATE_EDIT_EVENT";
    private Event editedEvent;
    private boolean editing;
    private List<Event.OpenGMMember> participants;

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
        } else {
            editedEvent = event;
            editing = true;
            participants = editedEvent.getParticipants();
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
        //GregorianCalendar date = event.getDate();
        Date date = event.getDate();
        String dateString = Integer.toString(date.getDay()) + '/' + Integer.toString(date.getMonth()) + '/' + Integer.toString(date.getYear());
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
        int[] dateArray = getDateFromText((TextView) findViewById(R.id.CreateEditEventDateText));
        //GregorianCalendar date = new GregorianCalendar(dateArray[0], dateArray[1], dateArray[2]);
        Date date = new Date(dateArray[0], dateArray[1], dateArray[2]);
        String name = ((TextView) findViewById(R.id.CreateEditEventNameText)).getText().toString();
        String description = ((TextView) findViewById(R.id.CreateEditEventDescriptionText)).getText().toString();
        String place = ((TextView) findViewById(R.id.CreateEditEventPlaceText)).getText().toString();
        return new Event(name, place, date, description, participants);
    }

    private Event editEvent() {
        int[] dateArray = getDateFromText((TextView) findViewById(R.id.CreateEditEventDateText));
        //GregorianCalendar date = new GregorianCalendar(dateArray[0], dateArray[1], dateArray[2]);
        Date date = new Date(dateArray[0], dateArray[1], dateArray[2]);
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
     * @return an array of int with year at index 0, month between 0 and 11 at index 1 and day at index 2
     */
    private int[] getDateFromText(TextView textView) {
        String[] dateString = textView.getText().toString().split("/");
        if (dateString.length != 3) {
            textView.setError("Invalid date format, must be dd/mm/yyyy");
        }
        int year = Integer.parseInt(dateString[2]);
        int month = Integer.parseInt(dateString[1]) - 1; //because reasons java month are (0-11)
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
        EditText eventDateText = (EditText) findViewById(R.id.CreateEditEventDateText);
        int[] dateArray = getDateFromText(eventDateText);
        Calendar currentDate = Calendar.getInstance();
        GregorianCalendar date = new GregorianCalendar(dateArray[0], dateArray[1], dateArray[2]);
        if (date.before(currentDate)) {
            eventDateText.setError("Invalid date (prior to now)");
            return false;
        }
        return true;
    }
}
