package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUtils;
import ch.epfl.sweng.opengm.utils.NetworkUtils;

public class EventListActivity extends AppCompatActivity {

    public static final int EVENT_LIST_RESULT_CODE = 666;

    private Map<String,PFEvent> eventMap;
    private PFGroup currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        setTitle("List of your events");
        findViewById(R.id.Screen).setVisibility(View.GONE);
        findViewById(R.id.EventListLoadingPanel).setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Intent intent = getIntent();
                       /* new Intent(Utils.GROUP_INTENT_MESSAGE);

                try {
                    intent.putExtra(Utils.GROUP_INTENT_MESSAGE, PFGroup.fetchExistingGroup("TXxysfyRqV"));
                } catch (PFException e) {
                    e.printStackTrace();
                }*/
                currentGroup = intent.getParcelableExtra(Utils.GROUP_INTENT_MESSAGE);
                Log.v("group members", Integer.toString(currentGroup.getMembers().size()));
                eventMap = new ArrayMap<>();
                for(PFEvent e : currentGroup.getEvents()){
                    eventMap.put(e.getId(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                findViewById(R.id.Screen).setVisibility(View.VISIBLE);
                displayEvents();
                findViewById(R.id.EventListLoadingPanel).setVisibility(View.GONE);
            }
        }.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent eventIntent) {
        if (requestCode == EVENT_LIST_RESULT_CODE) {
            if(resultCode == Activity.RESULT_OK){
                PFEvent event = eventIntent.getParcelableExtra(Utils.EVENT_INTENT_MESSAGE);
                boolean edited = eventIntent.getBooleanExtra(Utils.EDIT_INTENT_MESSAGE, false);
                Log.v("event from parcel", event.getId());
                if(NetworkUtils.haveInternet(getBaseContext())) {
                    Log.v("event id", event.getId());
                    try {
                        if(edited) {
                            currentGroup.updateEvent(event);
                            Toast.makeText(getApplicationContext(), "Event updated", Toast.LENGTH_SHORT).show();
                        }else{
                            currentGroup.addEvent(event);
                            Toast.makeText(getApplicationContext(), getString(R.string.EventListSuccessfullAdd), Toast.LENGTH_SHORT).show();
                        }
                    } catch (PFException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Couldn't add the event. Refresh later.",Toast.LENGTH_SHORT).show();
                }
                eventMap.put(event.getId(), event);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), getString(R.string.EventListFailToAdd), Toast.LENGTH_SHORT).show();
            }
            if(resultCode == Utils.DELETE_EVENT){
                PFEvent event = eventIntent.getParcelableExtra(Utils.EVENT_INTENT_MESSAGE);
                if(NetworkUtils.haveInternet(getBaseContext())) {
                    Log.v("event id", event.getId());
                    try {
                       currentGroup.removeEvent(event);
                    } catch (PFException e) {
                        e.printStackTrace();
                    }
                    (Toast.makeText(getApplicationContext(), "Event deleted sucessfully", Toast.LENGTH_SHORT)).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Couldn't delete the event. Refresh later.",Toast.LENGTH_SHORT).show();
                }
                eventMap.remove(event.getId());
            }
            displayEvents();
        }
    }

    /**
     * When the button is click, it's supposed to open an other Activity (CreateEditEventActivity)
     * Then get the Activity created this way, add it to the calendar and then display the calendar again.
     * @param v The View.
     */
    public void clickOnAddButton(View v){
        Intent intent = new Intent(this, CreateEditEventActivity.class);
        intent.putExtra(Utils.GROUP_INTENT_MESSAGE, currentGroup);
        startActivityForResult(intent, EVENT_LIST_RESULT_CODE);
    }
    public void clickOnCheckBoxForPastEvent(View v){
        displayEvents();
    }//TODO : Fix it

    public void clickOnRefreshButton(View v){
        findViewById(R.id.EventListLoadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.scrollView4).setVisibility(View.GONE);
        findViewById(R.id.eventListAddButton).setClickable(false);
        Button re=(Button) findViewById(R.id.eventListRefreshButton);
        re.setClickable(false);
        re.setText("WAIT");
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void[] params){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return false;
                }
                try {
                    currentGroup.reload();
                    for (PFEvent e : currentGroup.getEvents()) {
                        if (!eventMap.containsKey(e.getId())) {
                            currentGroup.removeEvent(e);
                        }
                    }
                    for (PFEvent e : eventMap.values()) {
                        if (currentGroup.getEvents().contains(e)) {
                            currentGroup.updateEvent(e);
                        } else {
                            currentGroup.addEvent(e);
                        }
                    }
                    return true;
                }catch (PFException e){
                    return false;
                }

            }
            @Override
            protected void onPostExecute(Boolean result){
                Button re=(Button) findViewById(R.id.eventListRefreshButton);
                re.setClickable(true);
                re.setText("REFRESH");
                findViewById(R.id.EventListLoadingPanel).setVisibility(View.GONE);
                findViewById(R.id.scrollView4).setVisibility(View.VISIBLE);
                findViewById(R.id.eventListAddButton).setClickable(true);
                if(!result) {
                    Toast.makeText(getApplicationContext(), "Unable to refresh.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private boolean compareDate(Date eventDate){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        Date currentDate = new Date(year, month, day, hour, min);
        return currentDate.before(eventDate);
    }

    /**
     * Call this method to refresh the calendar on the screen.
     */
    public void displayEvents(){
        LinearLayout linearLayoutListEvents = (LinearLayout) findViewById(R.id.line3);
        linearLayoutListEvents.removeAllViews();

        CheckBox checkboxForPastEvent = (CheckBox) findViewById(R.id.eventListCheckBoxForPastEvent);
        boolean displayPastEvents = checkboxForPastEvent.isChecked();

        ArrayList<PFEvent> eventList = new ArrayList<>(eventMap.values());
        Collections.sort(eventList, new Comparator<PFEvent>() {
            @Override
            public int compare(PFEvent lhs, PFEvent rhs) {
                return rhs.getDate().compareTo(lhs.getDate());
            }
        });

        for(PFEvent event :eventList) {
            if (displayPastEvents || compareDate(event.getDate())) {
                final Button b = new Button(this);
                b.setText(String.format("%s: %d/%02d/%04d, %d : %02d", event.getName(), event.getDay(), event.getMonth(), event.getYear(), event.getHours(), event.getMinutes()));
                b.setTag(event);
                b.setLayoutParams(linearLayoutListEvents.getLayoutParams());
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEvent((PFEvent) b.getTag());
                    }
                });
                linearLayoutListEvents.addView(b);
            }
        }
    }

    private void showEvent(PFEvent currentEvent) {
        if(currentEvent.getPicturePath()==PFUtils.pathNotSpecified) {
            try {
                String imageName = currentEvent.getId() + "_event";
                currentEvent.setPicturePath(ch.epfl.sweng.opengm.utils.Utils.
                        saveToInternalSorage(currentEvent.getPicture(), getApplicationContext(),imageName));
                currentEvent.setPictureName(imageName);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Unable to write image or to retrieve image", Toast.LENGTH_SHORT).show();
                currentEvent.setPicturePath(PFUtils.pathNotSpecified);
                currentEvent.setPictureName(PFUtils.nameNotSpecified);
            }
        }

        Intent intent = new Intent(this, ShowEventActivity.class);
        intent.putExtra(Utils.EVENT_INTENT_MESSAGE, currentEvent);
        intent.putExtra(Utils.GROUP_INTENT_MESSAGE, currentGroup);
        startActivityForResult(intent, EVENT_LIST_RESULT_CODE);
    }
}
