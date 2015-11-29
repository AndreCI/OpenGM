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
import android.widget.TextView;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.EventListActivity;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.polls.PollsListActivity;

import static ch.epfl.sweng.opengm.groups.MyGroupsActivity.RELOAD_USER_KEY;

public class GroupsHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;

    private PFGroup currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_groups_home);

        currentGroup = OpenGMApplication.getCurrentGroup();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setTitle(currentGroup.getName());

        TextView descriptionView  = (TextView) findViewById(R.id.textView_description);
        descriptionView.setText(currentGroup.getDescription());

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
            OpenGMApplication.setCurrentGroup(-1);
        }
    }

    public void onManageGroup(View v){
        Intent intent = new Intent(this, CreateEditGroupActivity.class);
        startActivityForResult(intent, RESULT_FIRST_USER);
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
                OpenGMApplication.setCurrentGroup(-1);
                startActivity(new Intent(GroupsHomeActivity.this, MyGroupsActivity.class).putExtra(RELOAD_USER_KEY, false));
                break;
            case R.id.nav_group_overview:
                break;
            case R.id.nav_members:
                startActivityForResult(new Intent(GroupsHomeActivity.this, MembersActivity.class), 1);
                break;
            case R.id.nav_events:
                Intent intent = new Intent(GroupsHomeActivity.this, EventListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_messages:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_polls:
                Intent intent1 = new Intent(GroupsHomeActivity.this, PollsListActivity.class);
                startActivity(intent1);
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
                setTitle(currentGroup.getName());
                TextView descriptionView  = (TextView) findViewById(R.id.textView_description);
                descriptionView.setText(currentGroup.getDescription());
                break;
            default:
        }
    }
}
