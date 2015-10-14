package ch.epfl.sweng.opengm.ch.epfl.sweng.opengm.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import ch.epfl.sweng.opengm.R;

public class EventListActivity extends AppCompatActivity {

    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
    }
}
