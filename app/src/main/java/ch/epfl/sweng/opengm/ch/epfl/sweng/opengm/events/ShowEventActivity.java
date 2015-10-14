package ch.epfl.sweng.opengm.ch.epfl.sweng.opengm.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

import ch.epfl.sweng.opengm.R;

public class ShowEventActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        //TODO : fill event with intent
        displayEventInformation();
    }

    private void displayEventInformation() {

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
        //TODO : create intent + go to create/edit event
    }
}
