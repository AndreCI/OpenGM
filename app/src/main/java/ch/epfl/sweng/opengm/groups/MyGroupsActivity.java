package ch.epfl.sweng.opengm.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.identification.LogoutDialogFragment;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.NetworkUtils;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;

public class MyGroupsActivity extends AppCompatActivity {

    public static final String COMING_FROM_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.coming";
    public static final String RELOAD_USER_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.reloadUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        if(NetworkUtils.haveInternet(getBaseContext())) {

            final List<PFGroup> groups = new ArrayList<>();

        try {
            OpenGMApplication.setCurrentUser(ParseUser.getCurrentUser().getObjectId());
            groups.addAll(getCurrentUser().getGroups());
        } catch (PFException e) {
            Toast.makeText(getBaseContext(), "Error while retrieving your groups" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        RecyclerView groupsRecyclerView = (RecyclerView) findViewById(R.id.groups_recycler_view);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        groupsRecyclerView.setLayoutManager(linearLayoutManager);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            groupsRecyclerView.setLayoutManager(gridLayoutManager);
            groupsRecyclerView.setHasFixedSize(true);

            // Get the screen size
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            // Pass to the adapter the group list, and the screen dimensions
            GroupCardViewAdapter groupCardViewAdapter = new GroupCardViewAdapter(groups, metrics);
            groupsRecyclerView.setAdapter(groupCardViewAdapter);

            if (groups.size() == 0) {
                DialogFragment noGroupsFragment = new NoGroupsDialogFragment();
                noGroupsFragment.show(getFragmentManager(), "noGroupsYetDialog");
            }

            final SwipeRefreshLayout swipeToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_swipe_layout);
            swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeToRefreshLayout.setRefreshing(true);
                    
                try {
                    // Thread.sleep(1000);

                    getCurrentUser().reload();  // TODO: make reload returns a boolean ???

                        groups.clear();
                        groups.addAll(getCurrentUser().getGroups());

                        // FIXME: at this point, getGroups() has all the previous groups, but has not added the new group to the list !
                        Log.v("INFO", "User Groups reloaded");
                        for (PFGroup group : groups) {
                            Log.v("INFO", group.getName());
                        }

                        // ((RecyclerView) findViewById(R.id.groups_recycler_view)).invalidateItemDecorations();
                        // findViewById(R.id.groups_recycler_view).invalidate();

                        findViewById(R.id.myGroupsMainLayout).invalidate();

                        Log.v("INFO", "Main View reloaded");

                    } catch (PFException e) {
                        e.printStackTrace();
                    }

                    swipeToRefreshLayout.setRefreshing(false);
                }
            });
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
