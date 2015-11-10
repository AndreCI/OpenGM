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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.utils.NetworkUtils;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;

public class CreateEditEventActivity extends AppCompatActivity {
    public static final String CREATE_EDIT_EVENT_MESSAGE = "ch.epfl.sweng.opengm.events.CREATE_EDIT_EVENT";
    public static String CREATE_EDIT_GROUP_MESSAGE = "ch.epfl.sweng.opengm.events.CREATE_EDIT_GROUP";
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
        currentGroup = intent.getParcelableExtra(ShowEventActivity.SHOW_EVENT_MESSAGE_GROUP);
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
                ArrayList<PFMember> members = data.getParcelableArrayListExtra(CREATE_EDIT_EVENT_MESSAGE);
                participants.clear();
                for(PFMember member : members) {
                    participants.put(member.getId(), member);
                }

                Toast.makeText(this, getString(R.string.CreateEditSuccessfullAddParticipants), Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.CreateEditFailToAddParticipants), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onOkButtonClick(View v) {
        if (legalArguments()) {
            if (participants != null) {
                Intent intent = new Intent(/*this, EventListActivity.class*/);
                intent.putExtra(CREATE_EDIT_EVENT_MESSAGE, createEditEvent());
                setResult(EventListActivity.EVENT_LIST_RESULT_CODE, intent);
                if(NetworkUtils.haveInternet(getBaseContext())) {
                    //TODO: update to serv
                }
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
        intent.putExtra(CREATE_EDIT_GROUP_MESSAGE, currentGroup);
        startActivityForResult(intent, CREATE_EDIT_EVENT_RESULT_CODE);
    }

    private void fillTexts(PFEvent event) {
        ((EditText) findViewById(R.id.CreateEditEventNameText)).setText(event.getName());
        ((EditText) findViewById(R.id.CreateEditEventPlaceText)).setText(event.getPlace());
        ((MultiAutoCompleteTextView) findViewById(R.id.CreateEditEventDescriptionText)).setText(event.getDescription());
        String timeString = String.format("%d : %02d", event.getHours(), event.getMinutes());
        ((Button) findViewById(R.id.CreateEditEventTimeText)).setText(timeString);
        String dateString = String.format("%d/%02d/%04d", event.getDay(), event.getMonth(), event.getYear());
        ((Button) findViewById(R.id.CreateEditEventDateText)).setText(dateString);
        TextView participantsList = ((TextView) findViewById(R.id.CreateEditEventParticipantsTextView));
        String participantsStringList = "";
        for(PFMember member : event.getParticipants().values()) {
            participantsStringList += member.getName() + "; ";
        }
        participantsList.setText(participantsStringList.substring(0, participantsStringList.length() - 2));
    }

    private PFEvent createEditEvent() {
        if (editing) {
            return editEvent();
        } else {
            return createEvent();
        }
    }

    private PFEvent createEvent() {

        Date date = getDateFromText();
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
        Date date = getDateFromText();

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
     * @return an array of int with year at index 0, month at index 1 and day at index 2
     */
    public Date getDateFromText() {
        String[] dateString = ((Button) findViewById(R.id.CreateEditEventDateText)).getText().toString().split("/");
        String[] timeString = ((Button) findViewById(R.id.CreateEditEventTimeText)).getText().toString().split(" : ");
        if (dateString.length != 3 || timeString.length != 2) {
            return null;
        }
        int year = Integer.parseInt(dateString[2]);
        int month = Integer.parseInt(dateString[1]) - 1;
        int day = Integer.parseInt(dateString[0]);
        int hours = Integer.parseInt(timeString[0]);
        int minutes = Integer.parseInt(timeString[1]);
        return new Date(year, month, day, hours, minutes);
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
        Date date = getDateFromText();

        if (date == null) {
            ((Button) findViewById(R.id.CreateEditEventTimeText)).setError("");
            ((Button) findViewById(R.id.CreateEditEventDateText)).setError("");
            Toast.makeText(this, getString(R.string.CreateEditEmptyTimeDateErrorMessage), Toast.LENGTH_SHORT).show();
            return false;
        }

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
        Date date = getDateFromText();
        if(date != null) {
            dialogFragment.show(getFragmentManager(), dateToString(date));
        } else {
            dialogFragment.show(getFragmentManager(), "");
        }
    }

    public void showDatePickerDialog(View view) {
        DialogFragment dialogFragment = new DatePickerFragment();
        Date date = getDateFromText();
        if(date != null) {
            dialogFragment.show(getFragmentManager(), dateToString(date));
        } else {
            dialogFragment.show(getFragmentManager(), "");
        }
    }
}
