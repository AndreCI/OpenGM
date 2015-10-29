package ch.epfl.sweng.opengm.identification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

public class MyGroupsActivity extends AppCompatActivity {

    public static final String COMING_FROM_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.coming";
    public static final String RELOAD_USER_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.reloadUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        RecyclerView groupsRecyclerView = (RecyclerView) findViewById(R.id.groups_recycler_view);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        rv.setLayoutManager(llm);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        groupsRecyclerView.setLayoutManager(gridLayoutManager);
        groupsRecyclerView.setHasFixedSize(true);

        //List<PFGroup> groups = new ArrayList<>(OpenGMApplication.getCurrentUser().getGroups());
        List<PFGroup> groups = new ArrayList<>();
        try {
            groups.add(PFGroup.createNewGroup(PFUser.fetchExistingUser("N47JlrQTSA"), "Group", "A group", null));
            groups.add(PFGroup.createNewGroup(PFUser.fetchExistingUser("f9PMNCFLXN"), "Another Group", "Just for fun", null));
        } catch (PFException e) {
            e.printStackTrace();
        }

        GroupCardViewAdapter groupCardViewAdapter = new GroupCardViewAdapter(groups);
        groupsRecyclerView.setAdapter(groupCardViewAdapter);
    }
}
