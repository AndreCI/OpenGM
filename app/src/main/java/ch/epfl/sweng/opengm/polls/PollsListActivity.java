package ch.epfl.sweng.opengm.polls;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.Utils;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFPoll;

import static ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE;

public class PollsListActivity extends AppCompatActivity {

    private PFGroup currentGroup;
    private List<PFPoll> polls = new ArrayList<>();

    private PollListAdapter mAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.title_list_poll);

        currentGroup = getIntent().getParcelableExtra(GROUP_INTENT_MESSAGE);

        polls.addAll(currentGroup.getPolls());

        list = (ListView) findViewById(R.id.pollsListView);

        mAdapter = new PollListAdapter(this, polls);
        list.setAdapter(mAdapter);
    }

    public void addPoll(View view) {
        Intent intent1 = new Intent(this, CreatePollActivity.class);
        intent1.putExtra(Utils.GROUP_INTENT_MESSAGE, currentGroup);
        startActivity(intent1);
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
