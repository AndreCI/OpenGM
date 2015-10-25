package ch.epfl.sweng.opengm.events;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFMember;

public class CreateEditEventActivity extends AppCompatActivity {
    public final static String CREATE_EDIT_EVENT_MESSAGE = "ch.epfl.sweng.opengm.events.CREATE_EDIT_EVENT";
    private PFEvent editedEvent;
    private boolean editing;
    private List<PFMember> participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_event);

        Intent intent = getIntent();
        PFEvent event = intent.getParcelableExtra(ShowEventActivity.SHOW_EVENT_MESSAGE);
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

    private void fillTexts(PFEvent event) {
        ((EditText) findViewById(R.id.CreateEditEventNameText)).setText(event.getName());
        ((EditText) findViewById(R.id.CreateEditEventPlaceText)).setText(event.getPlace());
        ((MultiAutoCompleteTextView) findViewById(R.id.CreateEditEventDescriptionText)).setText(event.getDescription());
        Date date = event.getDate();
        String timeString = String.format("%d : %02d", date.getHours(), date.getMinutes());
        ((Button) findViewById(R.id.CreateEditEventTimeText)).setText(timeString);
        String dateString = String.format("%d/%02d/%04d", date.getDate(), date.getMonth()+1, date.getYear());
        ((Button) findViewById(R.id.CreateEditEventDateText)).setText(dateString);
    }

    private PFEvent createEditEvent() {
        if (editing) {
            return editEvent();
        } else {
            return createEvent();
        }
    }

    private PFEvent createEvent() {

        int[] timeArray = getTimeFromText();
        int[] dateArray = getDateFromText();
        Date date = new Date(dateArray[0], dateArray[1]-1, dateArray[2], timeArray[0], timeArray[1]);
        String name = ((TextView) findViewById(R.id.CreateEditEventNameText)).getText().toString();
        String description = ((TextView) findViewById(R.id.CreateEditEventDescriptionText)).getText().toString();
        String place = ((TextView) findViewById(R.id.CreateEditEventPlaceText)).getText().toString();

        //TODO : get new id for creating event maybe asynchronously in onCreate
        ParseObject parseObject = new ParseObject(PFConstants.EVENT_TABLE_NAME);
        try {
            parseObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new PFEvent(parseObject.getObjectId(), name, place, date, description, participants);
    }

    private PFEvent editEvent() {
        int[] timeArray = getTimeFromText();
        int[] dateArray = getDateFromText();
        Date date = new Date(dateArray[0], dateArray[1]-1, dateArray[2], timeArray[0], timeArray[1]);
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
            eventNameText.setError(getString(R.string.CreateEditEmptyNameErrorMessage));
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
            Toast.makeText(this, getString(R.string.CreateEditEmptyTimeDateErrorMessage), Toast.LENGTH_SHORT).show();
            return false;
        }

        Date date = new Date(dateArray[0], dateArray[1], dateArray[2], timeArray[0], timeArray[1]);
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
            Toast.makeText(this, getString(R.string.CreateEditEarlyDate), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(participants.isEmpty()) {
            Toast.makeText(this, getString(R.string.CreateEditNoParticipants), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
