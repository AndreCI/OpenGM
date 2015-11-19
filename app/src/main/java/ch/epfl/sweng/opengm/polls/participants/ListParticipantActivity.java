package ch.epfl.sweng.opengm.polls.participants;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;

import static ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE;

public class ListParticipantActivity extends AppCompatActivity {

    private PFGroup group;
    private final List<Participant> participants = new ArrayList<>();

    private ParticipantAdapter mAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_particpant);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        group = getIntent().getParcelableExtra(GROUP_INTENT_MESSAGE);

        fillList();

        list = (ListView) findViewById(R.id.listView_PollParticipants);

        mAdapter = new ParticipantAdapter(this, R.layout.item_poll_participant, participants);
        list.setAdapter(mAdapter);
    }

    private void fillList() {

        for (PFMember member : group.getMembers().values()) {
            participants.add(new Participant(member));
        }

        for (String role : group.getRoles()) {
            List<PFMember> membersForRole = group.getMembersWithRole(role);
            participants.add(new Participant(role, membersForRole));
        }

        Collections.sort(participants);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_valid_generic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_validate:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
