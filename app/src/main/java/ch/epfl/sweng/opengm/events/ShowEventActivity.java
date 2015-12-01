package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUtils;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentGroup;

public class ShowEventActivity extends AppCompatActivity {

    public final static int SHOW_EVENT_RESULT_CODE = 1000;

    private PFEvent event;
    private PFGroup currentGroup;
    private boolean modified=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        Intent intent = getIntent();
        currentGroup = getCurrentGroup();
        event = intent.getParcelableExtra(Utils.EVENT_INTENT_MESSAGE);
        Log.v("group members", Integer.toString(currentGroup.getMembers().size()));
        setTitle("Event : "+event.getName() + " for the group : "+currentGroup.getName());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        displayEventInformation();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Utils.SILENCE);
                finish();
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SHOW_EVENT_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                event = data.getParcelableExtra(Utils.EVENT_INTENT_MESSAGE);
                modified=true;
                Toast.makeText(this, "event successfully edited", Toast.LENGTH_SHORT).show();
                Log.v("event received in Show", event.getId());
                displayEventInformation();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "event not updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if(modified) {
            intent.putExtra(Utils.EVENT_INTENT_MESSAGE, event);
            intent.putExtra(Utils.EDIT_INTENT_MESSAGE, true);
            setResult(Activity.RESULT_OK, intent);
        }else{
            setResult(Utils.SHOWING_EVENT, intent);
        }
        finish();
    }

    private void displayEventInformation() {
        fillEventName();
        fillEventPlace();
        fillEventDate();
        fillEventDescription();
        fillEventParticipants();
        fillEventBitmap();
    }

    private void fillEventName() {
        TextView tt =
        ((TextView) findViewById(R.id.ShowEventNameText));
        tt.setText(" " +event.getName()+ " ");
    }

    private void fillEventPlace() {
        TextView textView = (TextView) findViewById(R.id.ShowEventPlaceText);
        if (event.getPlace().isEmpty()) {
            textView.setHeight(0);
        } else {
            textView.setText("      A lieu à : "+event.getPlace()+ "  ");
        }
    }

    private void fillEventDate() {
        String hourString = String.format("%d : %02d", event.getHours(), event.getMinutes());
        ((TextView)findViewById(R.id.ShowEventHourText)).setText("      A "+hourString+ "  ");
        String dateString = String.format("%d/%02d/%04d", event.getDay(), event.getMonth(), event.getYear());
        ((TextView)findViewById(R.id.ShowEventDateText)).setText("      Le : "+dateString+ "  ");
    }

    private void fillEventDescription() {
        TextView textView = (TextView) findViewById(R.id.ShowEventDescriptionText);
        if (event.getDescription().isEmpty()) {
            textView.setHeight(0);
        } else {
            String description = "Description de l'evenement :\n" + event.getDescription();
            textView.setText(description);
        }
    }

    private void fillEventParticipants() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Liste des participants:");
        for (PFMember participant : event.getParticipants().values()) {
            stringBuilder.append('\n');

            stringBuilder.append(participant.getUsername());
        }
        ((TextView) findViewById(R.id.ShowEventParticipants)).setText(stringBuilder.toString());
    }
    private void fillEventBitmap(){
        String imagePath = event.getPicturePath();
        String imageName = event.getPictureName();
        if(imagePath!= PFUtils.pathNotSpecified && imageName!=PFUtils.nameNotSpecified) {
            Bitmap b;
            try {
                b = ch.epfl.sweng.opengm.utils.Utils.loadImageFromStorage(imagePath, imageName+".jpg");
                ImageView iv = (ImageView) findViewById(R.id.ShowEventBitmap);
                iv.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "Couldn't retrieve the image : Error 390 File not Found" + imagePath+ "  "+imageName, Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Couldn't retrieve the image : Error 400 No Path Specified", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEditButtonClick(View view) {
        if(currentGroup.userHavePermission(OpenGMApplication.getCurrentUser().getId(), PFGroup.Permission.MANAGE_EVENT)) {
            Intent intent = new Intent(this, CreateEditEventActivity.class);
            intent.putExtra(Utils.GROUP_INTENT_MESSAGE, currentGroup);
            intent.putExtra(Utils.EVENT_INTENT_MESSAGE, event);
            startActivityForResult(intent, SHOW_EVENT_RESULT_CODE);
        }else{
            Toast.makeText(getApplicationContext(), "You don't have the permission to edit this event", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDeleteButtonClick(View v){
        if(currentGroup.userHavePermission(OpenGMApplication.getCurrentUser().getId(), PFGroup.Permission.MANAGE_EVENT)) {
            Intent intent = new Intent(this, ShowEventActivity.class);
            intent.putExtra(Utils.EVENT_INTENT_MESSAGE, event);
            setResult(Utils.DELETE_EVENT, intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "You don't have the permission to delete this event", Toast.LENGTH_SHORT).show();
        }
    }
}
