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
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.polls.CreatePollActivity;

import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class ListParticipantActivity extends AppCompatActivity {

    private PFGroup group;

    private final List<Participant> participants = new ArrayList<>();

    private final Map<String, Boolean> membersEnrolled = new HashMap<>();

    private ParticipantAdapter mAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_particpant);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //group = getIntent().getParcelableExtra(CreatePollActivity.GROUP_POLL_INTENT);

        group = OpenGMApplication.getCurrentGroup();

        List<PFMember> enrolled = getIntent().getParcelableArrayListExtra(CreatePollActivity.ENROLLED_POLL_INTENT);

        for (String memberId : group.getMembers().keySet()) {
            membersEnrolled.put(memberId, false);
        }
        for (PFMember member : enrolled) {
            membersEnrolled.put(member.getId(), true);
        }

        fillList();

        list = (ListView) findViewById(R.id.listView_PollParticipants);

        mAdapter = new ParticipantAdapter(this, participants);
        list.setAdapter(mAdapter);
        list.setClickable(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox box = (CheckBox) view.findViewById(R.id.participant_box_poll);
                box.setChecked(!box.isChecked());
                updateView(box);
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

        checkCorrespondingBoxes();
    }

    public void updateView(View view) {
        CheckBox box = (CheckBox) view;
        boolean isChecked = box.isChecked();

        Participant p = (Participant) box.getTag();

        for (PFMember member : p.getParticipants()) {
            membersEnrolled.put(member.getId(), isChecked);
        }
        checkCorrespondingBoxes();
    }

    private void checkCorrespondingBoxes() {
        Log.d("ENROLLED", "LIST SIZE " + list.getChildCount());
        for (int i = 0; i < list.getChildCount(); i++) {
            View row = list.getChildAt(i);
            Participant child = mAdapter.getItem(i);

            boolean userAreAllChecks = true;

            for (PFMember childMember : child.getParticipants()) {
                if (!membersEnrolled.get(childMember.getId())) {
                    userAreAllChecks = false;
                    break;
                }
            }
            ((CheckBox) row.findViewById(R.id.participant_box_poll)).setChecked(userAreAllChecks);
        }
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
        }

        for (String role : group.getRoles()) {
            List<PFMember> membersForRole = group.getMembersWithRole(role);
            Participant p = new Participant(role, membersForRole);
            participants.add(p);
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

                List<PFMember> members = new ArrayList<>();

                for (Map.Entry<String, Boolean> entry : membersEnrolled.entrySet()) {
                    if (entry.getValue()) {
                        members.add(group.getMember(entry.getKey()));
                    }
                }

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
