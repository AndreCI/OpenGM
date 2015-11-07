package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;

public class CreateEditEventActivity extends AppCompatActivity {
    public final static String CREATE_EDIT_EVENT_MESSAGE = "ch.epfl.sweng.opengm.events.CREATE_EDIT_EVENT";
    public static final int CREATE_EDIT_EVENT_RESULT_CODE = 42;
    private PFEvent editedEvent;
    private boolean editing;
    private HashMap<String, PFMember> participants;
    private PFGroup currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_event);

        Intent intent = getIntent();
        PFEvent event = intent.getParcelableExtra(ShowEventActivity.SHOW_EVENT_MESSAGE_EVENT);
        currentGroup = intent.getParcelableExtra(EventListActivity.EVENT_LIST_MESSAGE_GROUP);
        if (event == null) {
            editing = false;
            participants = new HashMap<>();
        } else {
            editedEvent = event;
            editing = true;
            participants = editedEvent.getParticipants();
            fillTexts(event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_EDIT_EVENT_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<PFMember> members = data.getParcelableArrayListExtra(AddRemoveParticipantsActivity.ADD_REMOVE_PARTICIPANTS_RESULT);
                for(PFMember member : members) {
                    participants.put(member.getId(), member);
                }
                Toast.makeText(this, getString(R.string.CreateEditSuccessfullAddParticipants), Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.CreateEditFailToAddParticipants), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onOkButtonClick(View v) {
        if (legalArguments()) {
            if (participants != null) {
                Intent intent = new Intent(this, EventListActivity.class);
                intent.putExtra(CREATE_EDIT_EVENT_MESSAGE, createEditEvent());
                setResult(EventListActivity.RESULT_CODE_FOR_CREATE_EDIT, intent);
                finish();
            } else {
                Toast.makeText(this, "You must specify participants", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onParticipantsButtonClick(View v) {
        Intent intent = new Intent(this, AddRemoveParticipantsActivity.class);
        if (editing) {
            intent.putExtra(CREATE_EDIT_EVENT_MESSAGE, createEditEvent());
        }
        intent.putExtra(EventListActivity.EVENT_LIST_MESSAGE_GROUP, currentGroup);
        startActivityForResult(intent, CREATE_EDIT_EVENT_RESULT_CODE);
    }

    private void fillTexts(PFEvent event) {
        ((EditText) findViewById(R.id.CreateEditEventNameText)).setText(event.getName());
        ((EditText) findViewById(R.id.CreateEditEventPlaceText)).setText(event.getPlace());
        ((MultiAutoCompleteTextView) findViewById(R.id.CreateEditEventDescriptionText)).setText(event.getDescription());
        Date date = event.getDate();
        String timeString = String.format("%d : %02d", date.getHours(), date.getMinutes());
        ((Button) findViewById(R.id.CreateEditEventTimeText)).setText(timeString);
        String dateString = String.format("%d/%02d/%04d", date.getDate(), date.getMonth() + 1, date.getYear());
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
        Date date = new Date(dateArray[0], dateArray[1] - 1, dateArray[2], timeArray[0], timeArray[1]);
        String name = ((EditText) findViewById(R.id.CreateEditEventNameText)).getText().toString();
        String description = ((MultiAutoCompleteTextView) findViewById(R.id.CreateEditEventDescriptionText)).getText().toString();
        String place = ((EditText)findViewById(R.id.CreateEditEventPlaceText)).getText().toString();

        try {
            return PFEvent.createEvent(currentGroup, name, place, date, new ArrayList<>(participants.values()), description, null);
        } catch (PFException e) {
            // TODO toast ?
            return null;
        }
    }

    private PFEvent editEvent() {
        int[] timeArray = getTimeFromText();
        int[] dateArray = getDateFromText();
        Date date = new Date(dateArray[0], dateArray[1] - 1, dateArray[2], timeArray[0], timeArray[1]);

        String name = ((EditText) findViewById(R.id.CreateEditEventNameText)).getText().toString();
        String description = ((MultiAutoCompleteTextView) findViewById(R.id.CreateEditEventDescriptionText)).getText().toString();
        String place = ((EditText)findViewById(R.id.CreateEditEventPlaceText)).getText().toString();
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
        String[] timeString = ((Button) findViewById(R.id.CreateEditEventTimeText)).getText().toString().split(" : ");
        if (timeString.length != 2) {
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
        String[] dateString = ((Button) findViewById(R.id.CreateEditEventDateText)).getText().toString().split("/");
        if (dateString.length != 3) {
            return new int[]{};
        }
        int year = Integer.parseInt(dateString[2]);
        int month = Integer.parseInt(dateString[1]) - 1;
        int day = Integer.parseInt(dateString[0]);
        return new int[]{year, month, day};
    }

    /**
     * @return true if all arguments except the list of participants are legal for building an event
     * display a toast while it's not.
     */
    private boolean legalArguments() {
        String name = ((EditText) findViewById(R.id.CreateEditEventNameText)).getText().toString();
        if (name.isEmpty()) {
            ((EditText) findViewById(R.id.CreateEditEventNameText)).setError(getString(R.string.CreateEditEmptyNameErrorMessage));
            return false;
        }
        int[] timeArray = getTimeFromText();
        int[] dateArray = getDateFromText();

        if (timeArray.length == 0 || dateArray.length == 0) {
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
            if (year == date.getYear() && month == date.getMonth() && day == date.getDate()) {
                ((Button) findViewById(R.id.CreateEditEventTimeText)).setError("");
            } else {
                ((Button) findViewById(R.id.CreateEditEventDateText)).setError("");
            }
            Toast.makeText(this, getString(R.string.CreateEditEarlyDate), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (participants.isEmpty()) {
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
