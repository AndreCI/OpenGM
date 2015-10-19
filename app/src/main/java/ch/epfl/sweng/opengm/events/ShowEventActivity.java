package ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import ch.epfl.sweng.opengm.R;

public class ShowEventActivity extends AppCompatActivity {
    public final static String SHOW_EVENT_MESSAGE = "ch.epfl.sweng.opengm.events.SHOW_EVENT";

    private Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent intent = getIntent();
        //event = (Event) intent.getSerializableExtra("todo");
        event = new Event();
        event.setPlace("DTC");
        event.setName("Event");
        event.setDescription("ça va etre bien");
        event.setDate(new GregorianCalendar(2015, 10, 10, 18, 30));
        event.setParticipants(new ArrayList<Event.OpenGMMember>(1));
        displayEventInformation();
    }

    private void displayEventInformation() {
        fillEventName();
        fillEventPlace();
        fillEventDate();
        fillEventDescription();
        fillEventParticipants();
    }

    private void fillEventName() {
        ((TextView) findViewById(R.id.ShowEventNameText)).setText(event.getName());
    }

    private void fillEventPlace() {
        TextView textView = (TextView) findViewById(R.id.ShowEventPlaceText);
        if (event.getPlace().isEmpty()) {
            textView.setHeight(0);
        } else {
            textView.setText(event.getPlace());
        }
    }

    private void fillEventDate() {
        GregorianCalendar date = event.getDate();
        String hourString = (Integer.toString(date.HOUR_OF_DAY)+':'+Integer.toString(date.MINUTE));
        ((TextView)findViewById(R.id.ShowEventHourText)).setText(hourString);
        String dateString = Integer.toString(date.DAY_OF_MONTH) + '/' + Integer.toString(date.MONTH+1) + '/' + Integer.toString(date.YEAR);
        ((TextView)findViewById(R.id.ShowEventDateText)).setText(dateString);
    }

    private void fillEventDescription() {
        TextView textView = (TextView) findViewById(R.id.ShowEventDescriptionText);
        if (event.getDescription().isEmpty()) {
            textView.setHeight(0);
        } else {
            String description = "Description:\n" + event.getDescription();
            textView.setText(description);
        }
    }

    private void fillEventParticipants() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Participants:");
        for (Event.OpenGMMember participant : event.getParticipants()) {
            stringBuilder.append('\n');
            stringBuilder.append(participant.getName());
        }
        ((TextView) findViewById(R.id.ShowEventParticipants)).setText(stringBuilder.toString());
    }

    public void onEditButtonClick(View view) {
        Intent intent = new Intent(this, CreateEditEventActivity.class);
        intent.putExtra(SHOW_EVENT_MESSAGE, event);
        startActivity(intent);

    }
}
