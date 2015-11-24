package ch.epfl.sweng.opengm.polls;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFPoll;
import ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary.Bar;
import ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary.BarGraph;
import ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary.PieGraph;
import ch.epfl.sweng.opengm.polls.results.HoloGraphLibrary.PieSlice;

import static java.util.Arrays.asList;

public class PollResultActivity extends AppCompatActivity {

    private PFPoll mPoll;
    private PieGraph pieGraph;
    private BarGraph barGraph;

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

        Log.d("POLL", "number = " + mPoll.getAnswers().size());

        pieGraph = (PieGraph) findViewById(R.id.pieGraph);
        barGraph = (BarGraph) findViewById(R.id.barGraph);

        displayResult();
    }

    private void displayResult() {
        ArrayList<Bar> points = new ArrayList<>();
        int i = 0, size = COLORS.size();
        for (PFPoll.Answer answer : mPoll.getAnswers()) {
            Bar d = new Bar();
            Log.d("Answer value 2", "Answer = " + answer.getVotes());
            int color = COLORS.get((i++) % size);
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

    public void changeGraph(View v) {
        if (pieGraph.getVisibility() == View.VISIBLE) {
            pieGraph.setVisibility(View.GONE);
            barGraph.setVisibility(View.VISIBLE);
        } else {
            pieGraph.setVisibility(View.VISIBLE);
            barGraph.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }
}
