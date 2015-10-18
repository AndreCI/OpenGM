package ch.epfl.sweng.opengm.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;

import static android.widget.LinearLayout.*;

public class EventListActivity extends AppCompatActivity {

    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public void clickOnAddButton(View v){
        Event toAdd = new Event();
        toAdd.setName("AddByButton");
        eventList.add(toAdd);
        Toast t = Toast.makeText(getApplicationContext(), "Event Added.", Toast.LENGTH_SHORT);
        t.show();
        displayEvents();
    }

    public void displayEvents(){
        RelativeLayout r = (RelativeLayout) findViewById(R.id.ScrollViewParentLayout);
        ScrollView sv = new ScrollView(this);
        ScrollView.LayoutParams slp = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,ScrollView.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(slp);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(lp);
        for(Event e : eventList){
            final Button b = new Button(this);
            b.setText(e.getName());

            b.setLayoutParams(lp);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showEvent(); //TODO : gerer ce qu'il se passe quand on clique sur l'event
                }
            });


            linearLayout.addView(b);
        }
        sv.addView(linearLayout);
        r.addView(sv);
        //TODO : reafficher sv

    }
}
