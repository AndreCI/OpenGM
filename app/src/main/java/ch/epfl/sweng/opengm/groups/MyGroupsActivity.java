package ch.epfl.sweng.opengm.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.UserProfileActivity;
import ch.epfl.sweng.opengm.identification.LogoutDialogFragment;
import ch.epfl.sweng.opengm.identification.contacts.AppContactsActivity;
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

        OpenGMApplication.setCurrentGroup(-1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final List<PFGroup> groups = new ArrayList<>();

        final RecyclerView groupsRecyclerView = (RecyclerView) findViewById(R.id.groups_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        groupsRecyclerView.setLayoutManager(gridLayoutManager);
        groupsRecyclerView.setHasFixedSize(true);

        // Get the screen size
        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final GroupCardViewAdapter groupCardViewAdapter = new GroupCardViewAdapter(groups, metrics);
        groupsRecyclerView.setAdapter(groupCardViewAdapter);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView progressText = (TextView) findViewById(R.id.progressText);

        if (NetworkUtils.haveInternet(getBaseContext()) && getCurrentUser() == null) {

            new AsyncTask<Void, Integer, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        OpenGMApplication.setCurrentUser(ParseUser.getCurrentUser().getObjectId());
                    } catch (PFException e) {
                        Toast.makeText(getBaseContext(), "Error while retrieving the your user information", Toast.LENGTH_LONG).show();
                    }
                    final int max = getCurrentUser().getGroupsIds().size();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setMax(max);
                            progressText.setText(String.format(Locale.getDefault(), "Retrieving your groups : 0 of %d ...", max));
                        }
                    });
                    int current = 0;
                    for (String groupId : getCurrentUser().getGroupsIds()) {
                        try {
                            groups.add(getCurrentUser().fetchGroupWithId(groupId));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    groupCardViewAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (PFException e) {
                            Toast.makeText(getBaseContext(), "Error while retrieving one of your group", Toast.LENGTH_LONG).show();
                        }
                        publishProgress(++current);
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    progressBar.setProgress(values[0]);
                    progressText.setText(String.format(Locale.getDefault(), "Retrieving your groups : %d of %d ...", values[0], progressBar.getMax()));
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (groups.isEmpty()) {
                        DialogFragment noGroupsFragment = new NoGroupsDialogFragment();
                        noGroupsFragment.show(getFragmentManager(), "noGroupsYetDialog");
                    }
                    progressBar.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                }
            }.execute();

            final SwipeRefreshLayout swipeToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_swipe_layout);
            swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeToRefreshLayout.setRefreshing(true);
                    try {
                        getCurrentUser().reload();
                        groups.clear();
                        groups.addAll(getCurrentUser().getGroups());
                        findViewById(R.id.myGroupsMainLayout).invalidate();
                    } catch (PFException e) {
                        e.printStackTrace();
                    }
                    swipeToRefreshLayout.setRefreshing(false);
                }
            });
        } else if (getCurrentUser() != null) {
            groups.addAll(getCurrentUser().getGroups());
            groupCardViewAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_show_contacts:
                startActivity(new Intent(this, AppContactsActivity.class));
                return true;
            case R.id.action_show_settings:
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void gotoGroup(View view) {
//         FIXME: change color --> Add behaviour of clicked card
//        view.setBackgroundColor(0xBA1027);

        int groupPosition = (int) view.getTag();

        OpenGMApplication.setCurrentGroup(groupPosition);

        Intent intent = new Intent(MyGroupsActivity.this, GroupsHomeActivity.class);
        //intent.putExtra(GroupsHomeActivity.CHOSEN_GROUP_KEY, groupPosition);
        startActivity(intent);
    }

    public void addGroup(View view) {
        Intent intent = new Intent(MyGroupsActivity.this, CreateEditGroupActivity.class);
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
