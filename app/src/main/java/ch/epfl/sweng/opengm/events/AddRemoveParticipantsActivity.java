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
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFEvent;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;

import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class AddRemoveParticipantsActivity extends AppCompatActivity {

    public static final String ADD_REMOVE_PARTICIPANTS_RESULT = "CL4P-TP";
    public static int geneId = 0; //TODO : used for the quickClass to test, delete it later.
    private List<PFMember> members;
    private List<PFMember> membersToAdd;
    private CustomAdapter participantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_participants);
        //boxes = new ArrayList<>();
        Intent intent = getIntent();
        PFEvent currentEvent = intent.getParcelableExtra(CreateEditEventActivity.CREATE_EDIT_EVENT_MESSAGE);
        if (currentEvent != null && currentEvent.getParticipants() != null && !currentEvent.getParticipants().isEmpty()) {
            membersToAdd = currentEvent.getParticipants();
        } else {
            membersToAdd = new ArrayList<>();
        }
        PFGroup currentGroup = intent.getParcelableExtra(EventListActivity.EVENT_LIST_MESSAGE_GROUP);
        if (currentGroup != null && currentGroup.hasMembers()) {
            members = currentGroup.getMembers();
        } else {
            members = new ArrayList<>();
            try {
                members.add(PFMember.fetchExistingMember("oqMblls8Cb"));
            } catch (PFException e) {
                e.printStackTrace();
            }
        }
        assert (members.containsAll(membersToAdd));

        List<CheckParticipant> checkParticipants = new ArrayList<>(members.size());

        for (PFMember m : membersToAdd) {
            checkParticipants.add(new CheckParticipant(m, true));
        }
        List<PFMember> notAddedMembers = new ArrayList<>();
        notAddedMembers.addAll(members);
        notAddedMembers.removeAll(membersToAdd);
        for (PFMember m : notAddedMembers) {
            checkParticipants.add(new CheckParticipant(m, false));
        }

        participantsAdapter = new CustomAdapter(this, R.layout.check_participant_info, checkParticipants);

        ListView listView = (ListView) findViewById(R.id.memberListView);
        listView.setAdapter(participantsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckParticipant checkParticipant = (CheckParticipant) parent.getItemAtPosition(position);
                checkParticipant.setCheck(!checkParticipant.getCheck());
            }
        });

        /*members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());

        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());*/


        final SearchView sv = (SearchView) findViewById(R.id.searchMember);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Collections.sort(members, getComparator(query));
                displayParticipants(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                Collections.sort(members, getComparator(newText));
                displayParticipants(newText);
                return true;
            }
        });
        Collections.sort(members, getComparator(""));
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
        intent.putParcelableArrayListExtra(ADD_REMOVE_PARTICIPANTS_RESULT, (ArrayList<PFMember>) membersToAdd);
        setResult(Activity.RESULT_OK, intent);
        Toast.makeText(this, "members to add size" + membersToAdd.size(), Toast.LENGTH_SHORT).show();

        finish();
    }

    /**
     * A private method to compare members, depending on their name. Maybe later we can implements
     * others way to sort? //TODO : add comparator option to sort members
     *
     * @param s : if the member's name contains s, it will have a higher priority
     * @return : the comparator
     */
    private Comparator<PFMember> getComparator(final String s) {
        return new Comparator<PFMember>() {
            @Override
            public int compare(PFMember lhs, PFMember rhs) {
                if (lhs.getName().contains(s) && rhs.getName().contains(s)) {
                    return lhs.getName().compareTo(rhs.getName());
                } else if (lhs.getName().contains(s) && !rhs.getName().contains(s)) {
                    return -1;
                } else if (!lhs.getName().contains(s) && rhs.getName().contains(s)) {
                    return 1;
                } else {
                    return lhs.getName().compareTo(rhs.getName());
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

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        CheckParticipant checkParticipant = (CheckParticipant) cb.getTag();
                        checkParticipant.setCheck(cb.isChecked());
                        if (cb.isChecked()) {
                            membersToAdd.add(checkParticipant.getParticipant());
                        } else {
                            membersToAdd.remove(checkParticipant.getParticipant());
                        }
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CheckParticipant checkParticipant = participants.get(position);
            holder.textView.setText(checkParticipant.getName());
            holder.checkBox.setChecked(checkParticipant.getCheck());
            holder.checkBox.setTag(checkParticipant);

            return convertView;
        }
    }
}
