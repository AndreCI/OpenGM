package ch.epfl.sweng.opengm.identification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.groups.CreateGroupActivity;
import ch.epfl.sweng.opengm.groups.GroupsHomeActivity;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;

public class MyGroupsActivity extends AppCompatActivity {

    public static final String COMING_FROM_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.coming";
    public static final String RELOAD_USER_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.reloadUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        RecyclerView groupsRecyclerView = (RecyclerView) findViewById(R.id.groups_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        groupsRecyclerView.setLayoutManager(linearLayoutManager);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
//        groupsRecyclerView.setLayoutManager(gridLayoutManager);
        groupsRecyclerView.setHasFixedSize(true);

//        List<PFGroup> groups = new ArrayList<>(OpenGMApplication.getCurrentUser().getGroups());

        List<PFGroup> groups = new ArrayList<>();
        try {
            PFGroup group1 = PFGroup.fetchExistingGroup("p7grbYzKMj");
            Log.v("ORNYTHO", group1.getName() + " | " + group1.getDescription());
            groups.add(group1);
            PFGroup group2 = PFGroup.fetchExistingGroup("OXsMb6BrSa");
            Log.v("ORNYTHO", group2.getName() + " | " + group2.getDescription());
            groups.add(group2);
            PFGroup group3 = PFGroup.fetchExistingGroup("3RSp15oVOr");
            Log.v("ORNYTHO", group3.getName() + " | " + group3.getDescription());
            groups.add(group3);
        } catch (PFException e) {
            e.printStackTrace();
        }

        GroupCardViewAdapter groupCardViewAdapter = new GroupCardViewAdapter(groups);
        groupsRecyclerView.setAdapter(groupCardViewAdapter);
    }

    public void gotoGroup(View view) {
//         FIXME: change color --> Add behaviour of clicked card
//        view.setBackgroundColor(0xBA1027);

        int groupPosition = (int) view.getTag();
        Log.v("ORNYTHO", "datTag = [" + groupPosition + "]");

//        Intent intent = new Intent(MyGroupsActivity.this, GroupsHomeActivity.class);
//        intent.putExtra(GroupsHomeActivity.CHOOSEN_GROUP_KEY, groupPosition);
//        startActivity(intent);
    }

    public void addGroup(View view) {
        Log.v("ORNYTHO", "Add Group button pressed !");
        Intent intent = new Intent(MyGroupsActivity.this, CreateGroupActivity.class);
        startActivity(intent);
    }
}
