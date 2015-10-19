package ch.epfl.sweng.opengm.events;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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
        //event = new Event("zert","az", new GregorianCalendar(), "", new ArrayList<Event.OpenGMMember>());
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
        GregorianCalendar date = event.getDate();
        String timeString = Integer.toString(date.HOUR_OF_DAY) + " : " + Integer.toString(date.MINUTE);
        ((Button) findViewById(R.id.CreateEditEventDateText)).setText(timeString);
        String dateString = Integer.toString(date.DAY_OF_MONTH) + '/' + Integer.toString(date.MONTH + 1) + '/' + Integer.toString(date.YEAR);
        ((Button) findViewById(R.id.CreateEditEventDateText)).setText(dateString);
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
        Button button = (Button) findViewById(R.id.CreateEditEventTimeText);
        String[] timeString = button.getText().toString().split(" : ");
        if(timeString.length != 2) {
            return new int[]{};
        }
        int hours = Integer.parseInt(timeString[0]);
        int minutes = Integer.parseInt(timeString[1]);
        return new int[]{hours, minutes};
    }

    /**
     * @return an array of int with year at index 0, month at index 1 and day at index 2
     */
    private int[] getDateFromText() {
        Button button = (Button) findViewById(R.id.CreateEditEventDateText);
        String[] dateString = button.getText().toString().split("/");
        if(dateString.length != 3) {
            return new int[]{};
        }
        int year = Integer.parseInt(dateString[2]);
        int month = Integer.parseInt(dateString[1])-1;
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
        int[] timeArray = getTimeFromText();
        int[] dateArray = getDateFromText();

        if(timeArray.length == 0 || dateArray.length == 0) {
            if (timeArray.length == 0) {
                ((Button) findViewById(R.id.CreateEditEventTimeText)).setError("");
            }
            if (dateArray.length == 0) {
                ((Button) findViewById(R.id.CreateEditEventDateText)).setError("");
            }
            Toast.makeText(this, "Time and Date must be specified", Toast.LENGTH_SHORT).show();
            return false;
        }

        Date date = new Date(dateArray[0], dateArray[1], dateArray[2], timeArray[0], timeArray[1]);
        boolean err = false;
        if(date.getHours() != timeArray[0] || date.getMinutes() != timeArray[1]) {
            Toast.makeText(this, "Invalid Time " + Integer.toString(date.getHours()) + "!=" + timeArray[0] + " " + Integer.toString(date.getMinutes()) + "!=" + timeArray[1], Toast.LENGTH_SHORT).show();
            err = true;
        }
        if(date.getYear() != dateArray[0] || date.getMonth() != dateArray[1] || date.getDate()!= dateArray[2]) {
            Toast.makeText(this, "Invalid date " + date.getYear() + "!=" + dateArray[0] + " " + date.getMonth() + "!=" + (dateArray[1]) + " " + date.getDate()+ "!=" + dateArray[2], Toast.LENGTH_SHORT).show();
            err = true;
        }
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        Date currentDate = new Date(year, month, day, hour, min);
        if (date.before(currentDate)) {
            if(year == date.getYear() && month == date.getMonth() && day == date.getDate()) {
                ((Button) findViewById(R.id.CreateEditEventTimeText)).setError("");
            } else {
                ((Button) findViewById(R.id.CreateEditEventDateText)).setError("");
            }
            Toast.makeText(this, "Invalid date(prior to now)", Toast.LENGTH_SHORT).show();
            err = true;
        }
        return !err;
    }

    public void showTimePickerDialog(View view) {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View view) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getFragmentManager(), "datePicker");
    }
}
