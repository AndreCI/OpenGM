package ch.epfl.sweng.opengm.polls.participants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.polls.CreatePollActivity;

import static ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE;
import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class ListParticipantActivity extends AppCompatActivity {

    private PFGroup group;
    private final List<Participant> participants = new ArrayList<>();

    private final HashMap<Participant, Boolean> participantsChecked = new HashMap<>();

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

        mAdapter = new ParticipantAdapter(this, participants);
        list.setAdapter(mAdapter);
        list.setClickable(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox box = ((CheckBox) view.findViewById(R.id.participant_box_poll));
                boolean isChecked = box.isChecked();
                box.setChecked(!isChecked);
                Participant p = mAdapter.getItem(position);
                participantsChecked.put(p, !isChecked);
                for (int i = 0; i < list.getChildCount(); i++) {
                    if (i != position) {
                        View row = list.getChildAt(i);
                        Participant child = mAdapter.getItem(i);
                        if (child.getParticipants().containsAll(p.getParticipants())) {
                            ((CheckBox) row.findViewById(R.id.participant_box_poll)).setChecked(!isChecked);
                        }
                    }
                }
            }
        });


        final SearchView sv = (SearchView) findViewById(R.id.searchView_PollParticipants);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return showResult(query);
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                return showResult(newText);
            }
        });
    }

    @SuppressWarnings("SameReturnValue")
    private boolean showResult(String query) {
        Collections.sort(mAdapter.getObjects(), sortList(query));
        List<Participant> displayedP = new ArrayList<>();
        for (Participant p : mAdapter.getObjects()) {
            if (query.isEmpty() || stripAccents(p.toString()).contains(stripAccents(query))) {
                displayedP.add(p);
            }
        }
        list.setAdapter(new ParticipantAdapter(this, displayedP));
        return true;
    }

    private Comparator<Participant> sortList(final String s) {
        return new Comparator<Participant>() {
            @Override
            public int compare(Participant lhs, Participant rhs) {
                String c1 = lhs.toString();
                String c2 = rhs.toString();
                if (c1.contains(s) && c2.contains(s)) {
                    return lhs.compareTo(rhs);
                } else if (c1.contains(s) && !c2.contains(s)) {
                    return -1;
                } else if (!c1.contains(s) && c2.contains(s)) {
                    return 1;
                } else {
                    return lhs.compareTo(rhs);
                }
            }
        };
    }

    private void fillList() {

        for (PFMember member : group.getMembers().values()) {
            Participant p = new Participant(member);
            participants.add(p);
            participantsChecked.put(p, false);
        }

        for (String role : group.getRoles()) {
            List<PFMember> membersForRole = group.getMembersWithRole(role);
            Participant p = new Participant(role, membersForRole);
            participants.add(p);
            participantsChecked.put(p, false);
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

                HashSet<PFMember> members = new HashSet<>();
                for (Map.Entry<Participant, Boolean> entry : participantsChecked.entrySet()) {
                    if (entry.getValue()) {
                        members.addAll(entry.getKey().getParticipants());
                    }
                }
                Log.d("BEFORE", members.size() + "");
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra(CreatePollActivity.PARTICIPANTS_KEY, new ArrayList<Parcelable>(members));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
