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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.CreateEditEventActivity;
import ch.epfl.sweng.opengm.identification.GroupsOverviewActivity;
import ch.epfl.sweng.opengm.parse.PFGroup;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;
import static ch.epfl.sweng.opengm.groups.Members.GROUP_INDEX;
import static ch.epfl.sweng.opengm.identification.GroupsOverviewActivity.RELOAD_USER_KEY;

public class GroupsHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String CHOOSEN_GROUP_KEY = "ch.epfl.ch.opengm.groups.groupshomeactivity.groupidx";

    DrawerLayout drawer;

    private PFGroup currentGroup;

    private ListView mEventLists;

    private int groupPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_groups_home);

        Intent comingIntent = getIntent();

        groupPos = comingIntent.getIntExtra(CHOOSEN_GROUP_KEY, 0);

        currentGroup = getCurrentUser().getGroups().get(groupPos);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setTitle(currentGroup.getName());

        TextView descriptionView  = (TextView) findViewById(R.id.textView_description);
        descriptionView.setText(currentGroup.getDescription());

        NavigationView navView = (NavigationView) drawer.findViewById(R.id.nav_view);

        mEventLists = (ListView) navView.findViewById(R.id.listViewEvents);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO add a member
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_leave:
                getCurrentUser().removeFromGroup(currentGroup.getId());
                // use the below intent then (no break)
            case R.id.nav_home:
                startActivity(new Intent(GroupsHomeActivity.this, GroupsOverviewActivity.class).putExtra(RELOAD_USER_KEY, false));
                break;
            case R.id.nav_group_overview:
                break;
            case R.id.nav_members:
                startActivity(new Intent(GroupsHomeActivity.this, Members.class).putExtra(GROUP_INDEX, groupPos));
                break;
            case R.id.nav_events:
                startActivity(new Intent(GroupsHomeActivity.this, CreateEditEventActivity.class));
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
}
