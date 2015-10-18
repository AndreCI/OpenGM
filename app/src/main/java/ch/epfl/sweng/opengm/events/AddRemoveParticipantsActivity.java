package ch.epfl.sweng.opengm.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class AddRemoveParticipantsActivity extends AppCompatActivity {

    private List<OpenGMMember> members;
    private List<OpenGMMember> membersToAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_participants);
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
        displayParticipants();
    }

    public void displayParticipants(){

        RelativeLayout screenLayout = (RelativeLayout) findViewById(R.id.memberListLayout);
        ScrollView scrollViewForMembers = new ScrollView(this);
        ScrollView.LayoutParams scrollViewLP = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,ScrollView.LayoutParams.MATCH_PARENT);
        scrollViewForMembers.setLayoutParams(scrollViewLP);
        LinearLayout linearLayoutListMembers = new LinearLayout(this);
        linearLayoutListMembers.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams memberListLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutListMembers.setLayoutParams(memberListLP);
        /**
         * Comparator in order to sort the events by date. Maybe later we can allow multiple way
         * to sort?
         */
        Collections.sort(members, new Comparator<OpenGMMember>() {
            @Override
            public int compare(OpenGMMember lhs, OpenGMMember rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        for(final OpenGMMember m : members) {
            CheckBox c = new CheckBox(this);
            c.setText(m.getName());
            c.setLayoutParams(memberListLP);
            c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        membersToAdd.remove(m);
                    } else {
                        membersToAdd.add(m);
                    }
                }
            });
            linearLayoutListMembers.addView(c);
        }
        scrollViewForMembers.addView(linearLayoutListMembers);
        screenLayout.addView(scrollViewForMembers);
    }
    private class OpenGMMember{
        public OpenGMMember(){}
        public String getName(){
            return "MemberTester";
        }
    }
}
