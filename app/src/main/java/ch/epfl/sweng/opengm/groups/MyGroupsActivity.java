package ch.epfl.sweng.opengm.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.identification.LogoutDialogFragment;
import ch.epfl.sweng.opengm.parse.PFGroup;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;

public class MyGroupsActivity extends AppCompatActivity {

    public static final String COMING_FROM_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.coming";
    public static final String RELOAD_USER_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.reloadUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        OpenGMApplication.setCurrentUser(ParseUser.getCurrentUser().getObjectId());

        RecyclerView groupsRecyclerView = (RecyclerView) findViewById(R.id.groups_recycler_view);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        groupsRecyclerView.setLayoutManager(linearLayoutManager);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        groupsRecyclerView.setLayoutManager(gridLayoutManager);
        groupsRecyclerView.setHasFixedSize(true);

        List<PFGroup> groups = new ArrayList<>(getCurrentUser().getGroups());

        GroupCardViewAdapter groupCardViewAdapter = new GroupCardViewAdapter(groups);
        groupsRecyclerView.setAdapter(groupCardViewAdapter);

        if (groups.size() == 0) {
            DialogFragment noGroupsFragment = new NoGroupsDialogFragment();
            noGroupsFragment.show(getFragmentManager(), "noGroupsYetDialog");
        }
    }

    public void gotoGroup(View view) {
//         FIXME: change color --> Add behaviour of clicked card
//        view.setBackgroundColor(0xBA1027);

        int groupPosition = (int) view.getTag();
        Intent intent = new Intent(MyGroupsActivity.this, GroupsHomeActivity.class);
        intent.putExtra(GroupsHomeActivity.CHOSEN_GROUP_KEY, groupPosition);
        startActivity(intent);
    }

    public void addGroup(View view) {
        Intent intent = new Intent(MyGroupsActivity.this, CreateGroupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DialogFragment logoutFragment = new LogoutDialogFragment();
        logoutFragment.show(getFragmentManager(), "logoutDialog");
    }

    public static class NoGroupsDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.noGroupsYet)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }

    }
}
