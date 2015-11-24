package ch.epfl.sweng.opengm.polls;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.Utils;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFPoll;

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

        currentGroup = getIntent().getParcelableExtra(CreatePollActivity.GROUP_POLL_INTENT);

        List<PFPoll> groupsPoll = currentGroup.getPolls();
        List<PFPoll> userPoll = new ArrayList<>();

        for (PFPoll poll : groupsPoll) {
            if (poll.isUserEnrolled(OpenGMApplication.getCurrentUser().getId()))
                userPoll.add(poll);
        }

        polls.addAll(userPoll);

        list = (ListView) findViewById(R.id.pollsListView);

        mAdapter = new PollListAdapter(this, polls);
        list.setAdapter(mAdapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PFPoll poll = mAdapter.getItem(position);
                Intent i;
                if (poll.isOpen() && poll.getDeadline().before(Calendar.getInstance().getTime())) {
                    i = new Intent(PollsListActivity.this, PollVoteActivity.class);
                } else {
                    i = new Intent(PollsListActivity.this, PollResultActivity.class);
                }
                i.putExtra(CreatePollActivity.POLL_INTENT, poll);
                startActivity(i);
            }
        });
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
