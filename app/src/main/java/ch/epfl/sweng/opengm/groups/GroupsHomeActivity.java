package ch.epfl.sweng.opengm.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.EventListActivity;
import ch.epfl.sweng.opengm.parse.PFGroup;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;
import static ch.epfl.sweng.opengm.groups.MembersActivity.GROUP_INDEX;
import static ch.epfl.sweng.opengm.groups.MyGroupsActivity.RELOAD_USER_KEY;

public class GroupsHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String CHOSEN_GROUP_KEY = "ch.epfl.ch.opengm.groups.groupshomeactivity.groupidx";

    DrawerLayout drawer;

    private PFGroup currentGroup;

    private ListView mEventLists;

    private int groupPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_groups_home);

        Intent comingIntent = getIntent();

        groupPos = comingIntent.getIntExtra(CHOSEN_GROUP_KEY, 0);

        currentGroup = getCurrentUser().getGroups().get(groupPos);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setTitle(currentGroup.getName());

        TextView descriptionView  = (TextView) findViewById(R.id.textView_description);
        descriptionView.setText(currentGroup.getDescription());

        NavigationView navView = (NavigationView) drawer.findViewById(R.id.nav_view);

        mEventLists = (ListView) navView.findViewById(R.id.listViewEvents);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddMember);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_leave:
                LeaveGroupDialogFragment leaveGroupDialog = new LeaveGroupDialogFragment().setGroupToLeave(currentGroup);
                leaveGroupDialog.show(getFragmentManager(), "leaveGroupDialog");
                break;
            case R.id.nav_home:
                startActivity(new Intent(GroupsHomeActivity.this, MyGroupsActivity.class).putExtra(RELOAD_USER_KEY, false));
                break;
            case R.id.nav_group_overview:
                break;
            case R.id.nav_members:
                startActivityForResult(new Intent(GroupsHomeActivity.this, MembersActivity.class).putExtra(GROUP_INDEX, groupPos), 1);
                break;
            case R.id.nav_events:
                Intent intent = new Intent(GroupsHomeActivity.this, EventListActivity.class);
                intent.putExtra(EventListActivity.EVENT_LIST_INTENT_GROUP, currentGroup);
                startActivity(intent);
                break;
            case R.id.nav_messages:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_my_settings:
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // stay in the correct group home, comming back from MembersActivity
            case 1:
                groupPos = data.getIntExtra(GROUP_INDEX, -1);
                break;
            default:
        }
    }
}
