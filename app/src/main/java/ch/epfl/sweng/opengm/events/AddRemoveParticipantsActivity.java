package ch.epfl.sweng.opengm.events;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class AddRemoveParticipantsActivity extends AppCompatActivity {

    public static int geneId = 0; //TODO : used for the quickClass to test, delete it later.
    private List<OpenGMMember> members;
    private List<OpenGMMember> membersToAdd;
    private List<CheckBox> boxes;
    private LinearLayout linearLayoutListMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_participants);
        boxes = new ArrayList<>();
        membersToAdd = new ArrayList<>();
        members = new ArrayList<>();
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
        members.add(new OpenGMMember());
        members.add(new OpenGMMember());

        linearLayoutListMembers = new LinearLayout(this);

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
        for (final OpenGMMember m : members) {
            CheckBox c = new CheckBox(this);
            c.setText(m.getName());
            c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        membersToAdd.add(m);
                    } else {
                        membersToAdd.remove(m);
                    }
                }
            });
            boxes.add(c);
        }
        displayParticipants("");
    }

    /**
     * When click on okay button, this should return all the checked members to an other activity
     * //TODO : code it!
     *
     * @param v
     */
    public void clickOnOkayButton(View v) {
        Toast t = Toast.makeText(getApplicationContext(), "m.size()= " + membersToAdd.size(), Toast.LENGTH_SHORT);
        t.show();
    }

    /**
     * A private method to compare members, depending on their name. Maybe later we can implements
     * others way to sort? //TODO : add comparator option to sort members
     *
     * @param s : if the member's name contains s, it will have a higher priority
     * @return : the comparator
     */
    private Comparator<OpenGMMember> getComparator(final String s) {
        return new Comparator<OpenGMMember>() {
            @Override
            public int compare(OpenGMMember lhs, OpenGMMember rhs) {
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

    /**
     * This method display all the boxes to add or remove participants.
     *
     * @param query : il query is non empty, it will only show members with the query in their name.
     */
    private void displayParticipants(String query) {
        linearLayoutListMembers.removeAllViews();
        linearLayoutListMembers = new LinearLayout(this);
        linearLayoutListMembers.setOrientation(LinearLayout.VERTICAL);
        final RelativeLayout memberLayout = (RelativeLayout) findViewById(R.id.memberListLayout);
        memberLayout.removeAllViews();
        ScrollView scrollViewForMembers = new ScrollView(this);
        ScrollView.LayoutParams scrollViewLP = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        scrollViewForMembers.setLayoutParams(scrollViewLP);
        LinearLayout.LayoutParams memberListLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutListMembers.setLayoutParams(memberListLP);

        for (CheckBox c : boxes) {
            if (c.getText().toString().contains(query)) {
                c.setLayoutParams(memberListLP);
                linearLayoutListMembers.addView(c);
            }
        }

        scrollViewForMembers.addView(linearLayoutListMembers);
        memberLayout.addView(scrollViewForMembers);
    }

    /**
     * A quick class to test
     * //TODO: replace it.
     */
    private class OpenGMMember {
        private int id;
        private String name;

        public OpenGMMember() {
            this.id = geneId;
            geneId++;
            this.name = "MemberTester : " + id;
        }

        public void setName(String newName) {
            name = newName;
        }

        public String getName() {
            return name;
        }
    }
}
