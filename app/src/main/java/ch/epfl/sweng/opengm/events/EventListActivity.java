package ch.epfl.sweng.opengm.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
        tester1.setName("E1");
        tester2.setName("E2");
        tester3.setName("E3");
        tester4.setName("E4");
        tester5.setName("E5");
        tester6.setName("E6");
        eventList.add(tester1);
        eventList.add(tester2);
        eventList.add(tester3);
        eventList.add(tester4);
        eventList.add(tester5);
        eventList.add(tester6);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_list);
        displayEvents();


    }

    public void displayEvents(){
        ScrollView sv = (ScrollView) findViewById(R.id.scrollViewEventList);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(lp);
        int i=0;
        for(Event e : eventList){
            final Button b = new Button(this);
            b.setText(e.getName());
            b.setWidth(40);
            b.setHeight(40);
            b.setY(i * 40 + 20);
            b.setX(40);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showEvent(); //TODO : gerer ce qu'il se passe quand on clique sur l'event
                }
            });

            i++;
            // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            linearLayout.addView(b, lp);
        }
        sv.addView(linearLayout);

        //TODO : reafficher sv

    }
}
