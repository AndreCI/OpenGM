package ch.epfl.sweng.opengm.polls;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFPoll;

public class PollsListActivity extends AppCompatActivity {

    private final static int CREATE_POLL_KEY = 32697;

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

        currentGroup = OpenGMApplication.getCurrentGroup();

        final List<PFPoll> groupsPoll = currentGroup.getPolls();
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
                if (poll.isOpen()) {
                    i = new Intent(PollsListActivity.this, PollVoteActivity.class);
                } else {
                    i = new Intent(PollsListActivity.this, PollResultActivity.class);
                }
                i.putExtra(CreatePollActivity.POLL_INTENT, poll);
                startActivity(i);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final PFPoll poll = mAdapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(PollsListActivity.this);
                builder.setMessage(getString(R.string.confirm_deletion_poll))
                        .setPositiveButton(getString(R.string.delete_poll), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete the poll
                                currentGroup.removePoll(poll);
                                try {
                                    poll.delete();
                                } catch (PFException e) {
                                    // Just do nothing, the poll is still on the server but can't be reach
                                }
                                updateList();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    public void addPoll(View view) {
        Intent intent1 = new Intent(this, CreatePollActivity.class);
        startActivityForResult(intent1, CREATE_POLL_KEY);
    }

    public void showPastPolls(View view) {
        updateList();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_POLL_KEY) {
            if (resultCode == Activity.RESULT_OK) {
                updateList();
            }
        }
    }

    private void updateList() {
        CheckBox box = (CheckBox) findViewById(R.id.pastPollsBox);

        polls.clear();
        List<PFPoll> groupsPoll = currentGroup.getPolls();
        List<PFPoll> userPoll = new ArrayList<>();

        if (!box.isChecked()) {
            for (PFPoll poll : groupsPoll) {
                if (poll.isUserEnrolled(OpenGMApplication.getCurrentUser().getId()) && poll.isOpen())
                    userPoll.add(poll);
            }
        } else {
            for (PFPoll poll : groupsPoll) {
                if (poll.isUserEnrolled(OpenGMApplication.getCurrentUser().getId()))
                    userPoll.add(poll);
            }
        }
        polls.addAll(userPoll);
        Collections.sort(polls);
        mAdapter.notifyDataSetChanged();
    }

}
