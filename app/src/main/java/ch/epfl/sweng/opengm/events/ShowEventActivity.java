package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;

public class ShowEventActivity extends AppCompatActivity {

    public final static int SHOW_EVENT_RESULT_CODE = 1000;

    private PFEvent event;
    private PFGroup currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent intent = getIntent();

        currentGroup = intent.getParcelableExtra(Utils.GROUP_INTENT_MESSAGE);
        event = intent.getParcelableExtra(Utils.EVENT_INTENT_MESSAGE);
        Log.v("group members", Integer.toString(currentGroup.getMembers().size()));
        displayEventInformation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SHOW_EVENT_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                event = data.getParcelableExtra(Utils.EVENT_INTENT_MESSAGE);
                Toast.makeText(this, "event updated", Toast.LENGTH_SHORT).show();
                displayEventInformation();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "event not updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Utils.EVENT_INTENT_MESSAGE, event);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void displayEventInformation() {
        fillEventName();
        fillEventPlace();
        fillEventDate();
        fillEventDescription();
        fillEventParticipants();
      //  fillEventBitmap();
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
        String hourString = String.format("%d : %02d", event.getHours(), event.getMinutes());
        ((TextView)findViewById(R.id.ShowEventHourText)).setText(hourString);
        String dateString = String.format("%d/%02d/%04d", event.getDay(), event.getMonth(), event.getYear());
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
        for (PFMember participant : event.getParticipants().values()) {
            stringBuilder.append('\n');

            stringBuilder.append(participant.getUsername());
        }
        ((TextView) findViewById(R.id.ShowEventParticipants)).setText(stringBuilder.toString());
    }
    private void fillEventBitmap(){
        Bitmap b = event.getPicture();
        ImageView iv = (ImageView) findViewById(R.id.ShowEventBitmap);
        iv.setImageBitmap(b);
    }

    public void onEditButtonClick(View view) {
        Intent intent = new Intent(this, CreateEditEventActivity.class);
        intent.putExtra(Utils.GROUP_INTENT_MESSAGE, currentGroup);
        intent.putExtra(Utils.EVENT_INTENT_MESSAGE, event);
        startActivityForResult(intent, SHOW_EVENT_RESULT_CODE);
    }
}
