package ch.epfl.sweng.opengm.ch.epfl.sweng.opengm.events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.Date;

import ch.epfl.sweng.opengm.R;

public class ShowEventActivity extends AppCompatActivity {
    public final static String SHOW_EVENT_MESSAGE = "ch.epfl.sweng.opengm.events.SHOW_EVENT";

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("todo");

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
        ((TextView)findViewById(R.id.EventNameText)).setText(event.getName());
    }

    private void fillEventPlace() {
        TextView textView =(TextView) findViewById(R.id.EventNameText);
        if(event.getPlace().isEmpty()) {
            textView.setHeight(0);
        } else {
            textView.setText(event.getPlace());
        }
    }

    private void fillEventDate() {
        Date date = event.getDate();
        ((TextView)findViewById(R.id.EventDateText)).setText(date.getDay() + '/' + date.getMonth() + '/' + date.getYear());
    }

    private void fillEventDescription() {
        TextView textView =(TextView) findViewById(R.id.EventNameText);
        if(event.getDescription().isEmpty()) {
            textView.setHeight(0);
        } else {
            textView.setText("Description:\n" + event.getDescription());
        }
    }

    private void fillEventParticipants() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Participants:");
        for(Event.OpenGMMember participant : event.getParticipants()) {
            stringBuilder.append('\n');
            stringBuilder.append(participant.getName());
        }
        ((TextView) findViewById(R.id.EventNameText)).setText(stringBuilder.toString());
    }

    protected void onEditButtonClick() {
        Intent intent = new Intent(this, CreateEditEventActivity.class);
        intent.putExtra(SHOW_EVENT_MESSAGE, (Serializable) event);
    }
}
