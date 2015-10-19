package ch.epfl.sweng.opengm.events;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class EventListActivity extends AppCompatActivity {

    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //For the purpose of test only.
        //TODO : Event is not up to date, it doesn't implements PFEntity
        eventList = new ArrayList<Event>();
        Event tester1 = new Event();
        Event tester2 = new Event();
        Event tester3 = new Event();
        Event tester4 = new Event();
        Event tester5 = new Event();
        Event tester6 = new Event();
        Event tester7 = new Event();
        Event tester8 = new Event();
        Event tester9 = new Event();
        Event tester10 = new Event();
        Event tester11 = new Event();
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
        tester1.setDate(new GregorianCalendar(1995, 1, 29));
        tester2.setDate(new GregorianCalendar(2000,1,29));
        tester3.setDate(new GregorianCalendar(1999,2,22));
        tester4.setDate(new GregorianCalendar(1994,6,29));
        tester5.setDate(new GregorianCalendar(1995,1,28));
        tester6.setDate(new GregorianCalendar(1995,1,27));
        tester7.setDate(new GregorianCalendar(1995,1,26));
        tester8.setDate(new GregorianCalendar(1995,1,25));
        tester9.setDate(new GregorianCalendar(1995,1,24));
        tester10.setDate(new GregorianCalendar(1995,1,23));
        tester11.setDate(new GregorianCalendar(1995,1,22));
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
        eventList.add(tester11);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_list);
        displayEvents();


    }

    /**
     * When the button is click, it's supposed to open an other Activity (CreateEditEventActivity)
     * Then get the Activity created this way, add it to the calendar and then display the caledar again.
     * @param v The View.
     */
    public void clickOnAddButton(View v){
        Event toAdd = new Event();
        toAdd.setName("AddByButton");
        toAdd.setDate(new GregorianCalendar(2015, 10, 18));
        eventList.add(toAdd);
        Toast t = Toast.makeText(getApplicationContext(), "Event Added.", Toast.LENGTH_SHORT);
        t.show();
        displayEvents();
    }

    /**
     * Call this method to refresh the calendar on the screen.
     */
    public void displayEvents(){
        //TODO : Sort Events by Date
        RelativeLayout screenLayout = (RelativeLayout) findViewById(R.id.ScrollViewParentLayout);
        ScrollView scrollViewForEvents = new ScrollView(this);
        ScrollView.LayoutParams scrollViewLP = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,ScrollView.LayoutParams.MATCH_PARENT);
        scrollViewForEvents.setLayoutParams(scrollViewLP);
        LinearLayout linearLayoutListEvents = new LinearLayout(this);
        linearLayoutListEvents.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams eventListLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutListEvents.setLayoutParams(eventListLP);

        /**
         * Comparator in order to sort the events by date. Maybe later we can allow multiple way
         * to sort?
         */
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                return rhs.getDate().compareTo(lhs.getDate());
            }
        });
        for(Event e : eventList){
            final Button b = new Button(this);
            b.setText(e.getName() + ":"+e.getDate().get(Calendar.YEAR)+"/"+e.getDate().get(Calendar.MONTH)+"/"+e.getDate().get(Calendar.DATE));

            b.setLayoutParams(eventListLP);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showEvent(); //TODO : gerer ce qu'il se passe quand on clique sur l'event
                }
            });


            linearLayoutListEvents.addView(b);
        }
        scrollViewForEvents.addView(linearLayoutListEvents);
        screenLayout.addView(scrollViewForEvents);
    }
}
