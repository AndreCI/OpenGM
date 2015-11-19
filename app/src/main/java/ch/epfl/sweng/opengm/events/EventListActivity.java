package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

    public static final int RESULT_CODE_FOR_CREATE_EDIT_EVENT_ADDED = 42;
    public static final int RESULT_CODE_FOR_CREATE_EDIT_EVENT_DELETE = 69;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        /*new Intent(Utils.GROUP_INTENT_MESSAGE);

        try {
            intent.putExtra(Utils.GROUP_INTENT_MESSAGE, PFGroup.fetchExistingGroup("AeLf1qc8u8"));
        } catch (PFException e) {
            e.printStackTrace();
        }*/
        currentGroup = intent.getParcelableExtra(Utils.GROUP_INTENT_MESSAGE);
        Log.v("group members", Integer.toString(currentGroup.getMembers().size()));
        eventMap = new ArrayMap<>();
        for(PFEvent e : currentGroup.getEvents()){
            eventMap.put(e.getId(), e);
        }
        setContentView(R.layout.activity_event_list);
        setTitle("Events for the group : " + currentGroup.getName());
        displayEvents();
    }
    private void updateWithServer() throws PFException{
        currentGroup.reload();
        for(PFEvent e : currentGroup.getEvents()){
            if(!eventMap.containsValue(e)){
                currentGroup.removeEvent(e);
            }
        }
        for(PFEvent e : eventMap.values()){
            if(currentGroup.getEvents().contains(e)){
                currentGroup.updateEvent(e);
            }else{
                currentGroup.addEvent(e);
            }
        }
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
                        }else{
                            currentGroup.addEvent(event);
                        }
                    } catch (PFException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.EventListSuccessfullAdd), Toast.LENGTH_SHORT).show();
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
    }
    public void clickOnRefreshButton(View v){
        try {
            updateWithServer();
        } catch (PFException e) {
            Toast.makeText(getApplicationContext(), "Unable to refresh.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean compareDate(Date eventDate){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY) + 1;
        int min = c.get(Calendar.MINUTE);
        Date currentDate = new Date(year, month, day, hour, min);

        return eventDate.before(currentDate);
    }

    /**
     * Call this method to refresh the calendar on the screen.
     */
    public void displayEvents(){
        RelativeLayout screenLayout = (RelativeLayout) findViewById(R.id.ScrollViewParentLayout);
        screenLayout.removeAllViews();
        ScrollView scrollViewForEvents = new ScrollView(this);
        ScrollView.LayoutParams scrollViewLP = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,ScrollView.LayoutParams.MATCH_PARENT);
        scrollViewForEvents.setLayoutParams(scrollViewLP);
        LinearLayout linearLayoutListEvents = new LinearLayout(this);
        linearLayoutListEvents.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams eventListLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutListEvents.setLayoutParams(eventListLP);

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
                b.setLayoutParams(eventListLP);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEvent((PFEvent) b.getTag());
                    }
                });


                linearLayoutListEvents.addView(b);
            }
        }
        scrollViewForEvents.addView(linearLayoutListEvents);
        screenLayout.addView(scrollViewForEvents);
    }

    private void showEvent(PFEvent currentEvent) {
        if(currentEvent.getPicturePath()==PFUtils.pathNotSpecified)
        try {
            currentEvent.setPicturePath(ch.epfl.sweng.opengm.utils.Utils.
                    saveToInternalSorage(currentEvent.getPicture(),getApplicationContext(), currentEvent.getId()+"_event.jpg"));
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Unable to write image or to retrieve image", Toast.LENGTH_SHORT).show();
            currentEvent.setPicturePath(PFUtils.pathNotSpecified);
        }

        Intent intent = new Intent(this, ShowEventActivity.class);
        intent.putExtra(Utils.EVENT_INTENT_MESSAGE, currentEvent);
        intent.putExtra(Utils.GROUP_INTENT_MESSAGE, currentGroup);
        startActivityForResult(intent, EVENT_LIST_RESULT_CODE);
    }
}
