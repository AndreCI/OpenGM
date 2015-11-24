package ch.epfl.sweng.opengm.polls;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFPoll;
import ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary.Bar;
import ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary.BarGraph;
import ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary.PieGraph;
import ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary.PieSlice;
import ch.epfl.sweng.opengm.polls.results.OnSwipeTouchListener;
import ch.epfl.sweng.opengm.polls.results.PollResultAdapter;

import static java.util.Arrays.asList;

public class PollResultActivity extends AppCompatActivity {

    private PFPoll mPoll;
    private PieGraph pieGraph;
    private BarGraph barGraph;

    private ListView colorList;
    private HashMap<PFPoll.Answer, Integer> colors;
    private PollResultAdapter mAdapter;

    private final static ArrayList<Integer> COLORS = new ArrayList<>(asList(
            Color.parseColor("#FFE1AE"), Color.parseColor("#F9B32A"),
            Color.parseColor("#FD7150"), Color.parseColor("#FA4228"),
            Color.parseColor("#336E7B")));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_result);

        setTitle("Result of the poll");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPoll = getIntent().getParcelableExtra(CreatePollActivity.POLL_INTENT);

        colorList = (ListView) findViewById(R.id.results_colors_listView);

        colors = new HashMap<>();

        int i = 0, size = COLORS.size();
        for (PFPoll.Answer answer : mPoll.getAnswers()) {
            int color = COLORS.get((i++) % size);
            colors.put(answer, color);
        }

        mAdapter = new PollResultAdapter(this, mPoll.getAnswers(), colors);
        colorList.setAdapter(mAdapter);
        colorList.setClickable(true);


        pieGraph = (PieGraph) findViewById(R.id.pieGraph);
        barGraph = (BarGraph) findViewById(R.id.barGraph);

        displayResult();

        findViewById(R.id.graphLayout).setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                changeGraph(null);
            }

            @Override
            public void onSwipeRight() {
                changeGraph(null);
            }
        });
    }

    public void changeGraph(View view) {
        if (pieGraph.getVisibility() == View.VISIBLE) {
            pieGraph.setVisibility(View.GONE);
            barGraph.setVisibility(View.VISIBLE);
        } else {
            pieGraph.setVisibility(View.VISIBLE);
            barGraph.setVisibility(View.GONE);
        }
    }

    private void displayResult() {
        ArrayList<Bar> points = new ArrayList<>();
        for (PFPoll.Answer answer : mPoll.getAnswers()) {
            Bar d = new Bar();
            int color = colors.get(answer);
            d.setColor(color);
            d.setName(answer.getAnswer());
            d.setValue(answer.getVotes());
            points.add(d);
            PieSlice slice = new PieSlice();
            slice.setColor(color);
            slice.setValue(answer.getVotes());
            pieGraph.addSlice(slice);
        }

        barGraph.setBars(points);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result_poll, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_poll_result_change:
                changeGraph(null);
                return true;
            case R.id.action_poll_result_validate:
                finish();
                return true;
            default:
                return true;
        }
    }
}
