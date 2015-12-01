package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentGroup;
import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class AddRemoveParticipantsActivity extends AppCompatActivity {
    public static final String PARTICIPANTS_LIST_RESULT = "ch.epfl.opengm.participants_list_result";

    private CustomAdapter participantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_participants);
        Intent intent = getIntent();
        PFGroup currentGroup = getCurrentGroup();
        PFEvent currentEvent = intent.getParcelableExtra(Utils.EVENT_INTENT_MESSAGE);
        setTitle("Adding participants for Event"); //DONOT ADD currentEvent.name() or it will probably fail

        HashMap<String, PFMember> membersToAdd = new HashMap<>();
        if (currentEvent != null && !currentEvent.getParticipants().isEmpty()) {
            membersToAdd.putAll(currentEvent.getParticipants());
        }
        HashMap<String, PFMember> allMembers = new HashMap<>();
        if (currentGroup != null && currentGroup.hasMembers()) {
            allMembers.putAll(currentGroup.getMembers());
        }

        List<CheckParticipant> checkParticipants = new ArrayList<>(allMembers.size());

        for (PFMember member : allMembers.values()) {
            checkParticipants.add(new CheckParticipant(member, membersToAdd.keySet().contains(member.getId())));
        }

        participantsAdapter = new CustomAdapter(this, R.layout.check_participant_info, checkParticipants);

        ListView listView = (ListView) findViewById(R.id.memberListView);
        listView.setAdapter(participantsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkParticipantCheckBox);
                checkBox.performClick();
                CheckParticipant checkParticipant = (CheckParticipant) checkBox.getTag();
                checkParticipant.setCheck(checkBox.isChecked());
            }
        });

        final SearchView sv = (SearchView) findViewById(R.id.searchMember);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Collections.sort(participantsAdapter.participants, getComparator(query));
                displayParticipants(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                Collections.sort(participantsAdapter.participants, getComparator(newText));
                displayParticipants(newText);
                return true;
            }
        });
        Collections.sort(participantsAdapter.participants, getComparator(""));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        displayParticipants("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            default:
                return true;
        }
    }

    public void clickOnOkayButton(View v) {
        Intent intent = new Intent();
        ArrayList<PFMember> result = participantsAdapter.checkList();
        intent.putParcelableArrayListExtra(PARTICIPANTS_LIST_RESULT, result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * A private method to compare members, depending on their name. Maybe later we can implements
     * others way to sort?
     *
     * @param s : if the member's name contains s, it will have a higher priority
     * @return : the comparator
     */
    private Comparator<CheckParticipant> getComparator(final String s) {
        return new Comparator<CheckParticipant>() {
            @Override
            public int compare(CheckParticipant lhs, CheckParticipant rhs) {
                String lName = lhs.getName();
                String rName = rhs.getName();
                if (lName.contains(s) && rName.contains(s)) {
                    return lName.compareTo(rName);
                } else if (lName.contains(s) && !rName.contains(s)) {
                    return -1;
                } else if (!lName.contains(s) && rName.contains(s)) {
                    return 1;
                } else {
                    return lName.compareTo(rName);
                }
            }
        };
    }

    private void displayParticipants(String query) {
        List<CheckParticipant> displayedParticipants = new ArrayList<>();
        for (CheckParticipant participant : participantsAdapter.participants) {
            if (query.isEmpty() || stripAccents(participant.getName()).contains(stripAccents(query))) {
                displayedParticipants.add(participant);
            }
        }
        ListView listView = (ListView) findViewById(R.id.memberListView);
        listView.setAdapter(new CustomAdapter(this, R.layout.check_participant_info, displayedParticipants));
    }

    private class CustomAdapter extends ArrayAdapter<CheckParticipant> {

        private final List<CheckParticipant> participants;

        public CustomAdapter(Context context, int resource, List<CheckParticipant> participants) {
            super(context, resource, participants);
            this.participants = new ArrayList<>();
            this.participants.addAll(participants);
        }

        private class ViewHolder {
            TextView textView;
            CheckBox checkBox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.check_participant_info, null);

                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.checkParticipantTextView);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkParticipantCheckBox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CheckParticipant checkParticipant = participants.get(position);
            holder.textView.setText(checkParticipant.getName());
            holder.checkBox.setChecked(checkParticipant.isChecked());
            holder.checkBox.setTag(checkParticipant);

            return convertView;
        }

        private ArrayList<PFMember> checkList() {
            ArrayList<PFMember> list = new ArrayList<>();
            for(int i = 0; i < participants.size(); ++i) {
                CheckParticipant checkParticipant = participants.get(i);
                if(checkParticipant.isChecked()) {
                    list.add(checkParticipant.getParticipant());
                }
            }
            return list;
        }
    }
}
