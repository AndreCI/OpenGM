package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;

import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class AddRemoveParticipantsActivity extends AppCompatActivity {

    public static final String ADD_REMOVE_PARTICIPANTS_RESULT = "CL4P-TP";
    public static int geneId = 0; //TODO : used for the quickClass to test, delete it later.
    private HashMap<String, PFMember> members;
    private HashMap<String, PFMember> membersToAdd;
    private List<String> sortedMembers;
    private CustomAdapter participantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_participants);
        Intent intent = getIntent();
        PFEvent currentEvent = intent.getParcelableExtra(CreateEditEventActivity.CREATE_EDIT_EVENT_MESSAGE);
        sortedMembers = new ArrayList<>();
        if (currentEvent != null && !currentEvent.getParticipants().isEmpty()) {
            membersToAdd = currentEvent.getParticipants();
        } else {
            membersToAdd = new HashMap<>();
        }
        PFGroup currentGroup = intent.getParcelableExtra(EventListActivity.EVENT_LIST_MESSAGE_GROUP);
        if (currentGroup != null && currentGroup.hasMembers()) {
            members = currentGroup.getMembers();
        } else {
            members = new HashMap<>();
            try {
                members.put("oqMblls8Cb", PFMember.fetchExistingMember("oqMblls8Cb"));
            } catch (PFException e) {
                e.printStackTrace();
            }
        }

        List<CheckParticipant> checkParticipants = new ArrayList<>(members.size());

        for (PFMember m : membersToAdd.values()) {
            checkParticipants.add(new CheckParticipant(m, true));
            sortedMembers.add(m.getName());
        }
        List<PFMember> notAddedMembers = new ArrayList<>();
        notAddedMembers.addAll(members.values());
        notAddedMembers.removeAll(membersToAdd.values());
        for (PFMember m : notAddedMembers) {
            checkParticipants.add(new CheckParticipant(m, false));
            sortedMembers.add(m.getName());
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
                PFMember member = checkParticipant.getParticipant();
                if (checkParticipant.getCheck()) {
                    membersToAdd.put(member.getId(), member);
                } else {
                    membersToAdd.remove(member);
                }
            }
        });

        final SearchView sv = (SearchView) findViewById(R.id.searchMember);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Collections.sort(sortedMembers, getComparator(query));
                displayParticipants(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                Collections.sort(sortedMembers, getComparator(newText));
                displayParticipants(newText);
                return true;
            }
        });
        Collections.sort(sortedMembers, getComparator(""));
        displayParticipants("");
    }

    /**
     * When click on okay button, this should return all the checked members to an other activity
     * //TODO : code it!
     *
     * @param v
     */
    public void clickOnOkayButton(View v) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(ADD_REMOVE_PARTICIPANTS_RESULT, new ArrayList<>(membersToAdd.values()));
        setResult(Activity.RESULT_OK, intent);
        Toast.makeText(this, "members to add size" + membersToAdd.size(), Toast.LENGTH_SHORT).show();

        //finish();
    }

    /**
     * A private method to compare members, depending on their name. Maybe later we can implements
     * others way to sort? //TODO : add comparator option to sort members
     *
     * @param s : if the member's name contains s, it will have a higher priority
     * @return : the comparator
     */
    private Comparator<String> getComparator(final String s) {
        return new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if (lhs.contains(s) && rhs.contains(s)) {
                    return lhs.compareTo(rhs);
                } else if (lhs.contains(s) && !rhs.contains(s)) {
                    return -1;
                } else if (!lhs.contains(s) && rhs.contains(s)) {
                    return 1;
                } else {
                    return lhs.compareTo(rhs);
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

            ViewHolder holder = null;

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
            holder.checkBox.setChecked(checkParticipant.getCheck());
            holder.checkBox.setTag(checkParticipant);
            holder.textView.setTag(checkParticipant);

            return convertView;
        }
    }
}
