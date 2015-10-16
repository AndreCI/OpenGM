package ch.epfl.sweng.opengm.ch.epfl.sweng.opengm.events;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class EventListActivity extends AppCompatActivity {

    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        eventList = new ArrayList<Event>();
        Event tester1 = new Event();
        Event tester2 = new Event();
        Event tester3 = new Event();
        tester1.setName("E1");
        tester2.setName("E2");
        tester3.setName("E3");
        eventList.add(tester1);
        eventList.add(tester2);
        eventList.add(tester3);
        super.onCreate(savedInstanceState);

        displayEvents();

        setContentView(R.layout.activity_event_list);


    }

    public void displayEvents(){
        ScrollView sv = (ScrollView) findViewById(R.id.scrollViewEventList);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayoytScrollViewEventList);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ListView lv = (ListView) findViewById(R.id.ListViewScrollViewEventList);
        for(Event e : eventList){
            Button b = new Button(this);
            b.setText(e.getName());
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showEvent(); //TODO : gerer ce qu'il se passe quand on clique sur l'event
                }
            });
        lv.addView(b);
        }
        sv.addView(linearLayout);
        //TODO : reafficher sv

    }
}
