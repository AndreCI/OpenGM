package ch.epfl.sweng.opengm.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;

import java.util.ArrayList;
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
        displayParticipants();
    }

    public void displayParticipants(){

        ScrollView sv = (ScrollView) findViewById(R.id.scrollViewListOfMember);
        for(final OpenGMMember m : members){
            final CheckBox b = new CheckBox(this);
            b.setText(m.getName());
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (b.isChecked()) {
                        b.setChecked(false);
                        membersToAdd.remove(m);
                    } else {
                        b.setChecked(true);
                        membersToAdd.add(m);
                    }
                }
            });
            sv.addView(b);
        }

    }
    private class OpenGMMember{
        public OpenGMMember(){}
        public String getName(){
            return "MemberTester";
        }
    }
}
