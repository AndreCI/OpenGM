package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

public class EventListActivity extends AppCompatActivity {

    public final static String EVENT_LIST_MESSAGE_EVENT = "ch.epfl.sweng.opengm.events.EVENT_LIST_EVENT";
    public final static String EVENT_LIST_MESSAGE_GROUP = "ch.epfl.sweng.opengm.events.EVENT_LIST_GROUP";


    private List<PFEvent> eventList;
    private PFGroup currentGroup;

    public static final int RESULT_CODE_FOR_CREATE_EDIT = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*//For the purpose of test only.
        //TODO : Event is not up to date, it doesn't implements PFEntity
        eventList = new ArrayList<>();
        PFEvent tester1 = new PFEvent();
        PFEvent tester2 = new PFEvent();
        PFEvent tester3 = new PFEvent();
        PFEvent tester4 = new PFEvent();
        PFEvent tester5 = new PFEvent();
        PFEvent tester6 = new PFEvent();
        PFEvent tester7 = new PFEvent();
        PFEvent tester8 = new PFEvent();
        PFEvent tester9 = new PFEvent();
        PFEvent tester10 = new PFEvent();
        PFEvent tester11 = new PFEvent();
        tester1.setName("E1");
        tester2.setName("E2");
        tester3.setName("E3");
        tester4.setName("E4");
        tester5.setName("E5");
        tester6.setName("E6");
        tester7.setName("E7");
        tester8.setName("E8");
        tester9.setName("E9");
        tester10.setName("E10");
        tester11.setName("E11");
        tester1.setDate(new Date(1995, 1, 29));
        tester2.setDate(new Date(2000,1,29));
        tester3.setDate(new Date(1999,2,22));
        tester4.setDate(new Date(1994,6,29));
        tester5.setDate(new Date(1995,1,28));
        tester6.setDate(new Date(1995,1,27));
        tester7.setDate(new Date(1995,1,26));
        tester8.setDate(new Date(1995,1,25));
        tester9.setDate(new Date(1995,1,24));
        tester10.setDate(new Date(1995,1,23));
        tester11.setDate(new Date(1995,1,22));
        eventList.add(tester1);
        eventList.add(tester2);
        eventList.add(tester3);
        eventList.add(tester4);
        eventList.add(tester5);
        eventList.add(tester6);
        eventList.add(tester7);
        eventList.add(tester8);
        eventList.add(tester9);
        eventList.add(tester10);
        eventList.add(tester11);*/
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //TODO : check with other ppl for intent name
        currentGroup = intent.getParcelableExtra("intentNameToDetermine");
        PFUser user = null;
        try {
            user = PFUser.createNewUser("testuser", "lolNoMail4U", "toto", "titi", "tata");
        } catch (PFException e) {
            e.printStackTrace();
        }
        try {
            currentGroup = PFGroup.createNewGroup(user, "testgroup", "testdescription", null);
        } catch (PFException e) {
            e.printStackTrace();
        }
        currentGroup.addEvent(new PFEvent("id", "name", "place", Utils.stringToDate("2018-0-1-12-12"), "description", new ArrayList<PFMember>()));
        currentGroup.addUser("aurel");
        eventList = currentGroup.getEvents();
        setContentView(R.layout.activity_event_list);
        displayEvents();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent eventIntent) {

        if (requestCode == RESULT_CODE_FOR_CREATE_EDIT) {
            if(resultCode == Activity.RESULT_OK){
                PFEvent event = eventIntent.getParcelableExtra(CreateEditEventActivity.CREATE_EDIT_EVENT_MESSAGE);
                eventList.add(event);
                Toast t = Toast.makeText(getApplicationContext(), getString(R.string.EventListSuccessfullAdd), Toast.LENGTH_SHORT);
                t.show();
                displayEvents();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast t = Toast.makeText(getApplicationContext(), getString(R.string.EventListFailToAdd), Toast.LENGTH_SHORT);
                t.show();
                displayEvents();
            }
        }
    }

    /**
     * When the button is click, it's supposed to open an other Activity (CreateEditEventActivity)
     * Then get the Activity created this way, add it to the calendar and then display the caledar again.
     * @param v The View.
     */
    public void clickOnAddButton(View v){
        Intent intent = new Intent(this, CreateEditEventActivity.class);
        intent.putExtra(EVENT_LIST_MESSAGE_GROUP, currentGroup);

        startActivityForResult(intent, RESULT_CODE_FOR_CREATE_EDIT);
    }

    /**
     * Call this method to refresh the calendar on the screen.
     */
    public void displayEvents(){
        RelativeLayout screenLayout = (RelativeLayout) findViewById(R.id.ScrollViewParentLayout);
        ScrollView scrollViewForEvents = new ScrollView(this);
        ScrollView.LayoutParams scrollViewLP = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,ScrollView.LayoutParams.MATCH_PARENT);
        scrollViewForEvents.setLayoutParams(scrollViewLP);
        LinearLayout linearLayoutListEvents = new LinearLayout(this);
        linearLayoutListEvents.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams eventListLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutListEvents.setLayoutParams(eventListLP);


        Collections.sort(eventList, new Comparator<PFEvent>() {
            @Override
            public int compare(PFEvent lhs, PFEvent rhs) {
                return rhs.getDate().compareTo(lhs.getDate());
            }
        });
        for(PFEvent e : eventList){
            final Button b = new Button(this);
            Date date = e.getDate();
            b.setText(String.format("%s: %d/%02d/%04d, %d : %02d", e.getName(), date.getDate(), date.getMonth(), date.getYear(), date.getHours(), date.getMinutes()));
            b.setTag(e);
            b.setLayoutParams(eventListLP);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEvent((PFEvent)b.getTag());
                }
            });


            linearLayoutListEvents.addView(b);
        }
        scrollViewForEvents.addView(linearLayoutListEvents);
        screenLayout.addView(scrollViewForEvents);
    }

    private void showEvent(PFEvent currentEvent) {
        Intent intent = new Intent(this, ShowEventActivity.class);
        intent.putExtra(EVENT_LIST_MESSAGE_GROUP, currentGroup);
        intent.putExtra(EVENT_LIST_MESSAGE_EVENT, currentEvent);
        startActivity(intent);
    }
}
