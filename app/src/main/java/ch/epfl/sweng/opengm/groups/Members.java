package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;

public class Members extends AppCompatActivity {

    private ListView list;
    private PFGroup group;
    private MembersAdapter adapter;
    private List<PFMember> members;
    private boolean selectMode;

    public static final String GROUP_INDEX = "ch.epfl.sweng.opengm.groups.members.groupindex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        if (getActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        selectMode = false;

        int groupId = getIntent().getIntExtra(GROUP_INDEX, -1);
        group = OpenGMApplication.getCurrentUser().getGroups().get(groupId);
        members = group.getMembers();

//        // hardcoded the getting of user for tests
//        //-----------------------------------------
//        int groupId = 0;
//        PFUser user = null;
//        try {
//            user = PFUser.fetchExistingUser("oqMblls8Cb");
//        } catch (PFException e) {
//            e.printStackTrace();
//        }
//        group = user.getGroups().get(groupId);
//        members = group.getMembers();
//        //-----------------------------------------

        list = (ListView) findViewById(R.id.member_list);

        adapter = new MembersAdapter(this, R.layout.item_member, members, selectMode);
        list.setAdapter(adapter);

        // change for the select mode when long click on item
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectMode(true);
                ((CheckBox)view.findViewById(R.id.member_checkbox)).setChecked(true);
                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    setSelectMode(false);
                }
                break;
            default:
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_members, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // display or not these option according to the select mode
        menu.findItem(R.id.action_remove_person).setVisible(selectMode);
        menu.findItem(R.id.action_change_roles).setVisible(selectMode);
        menu.findItem(R.id.action_members_select).setVisible(!selectMode);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // handle up navigation according to select mode
                if (selectMode) {
                    setSelectMode(false);
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            case R.id.action_add_person:
                addPerson();
                return true;
            case R.id.action_remove_person:
                removePerson();
                return true;
            case R.id.action_change_roles:
                changeRoles();
                return true;
            case R.id.action_members_select:
                setSelectMode(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (selectMode) {
            setSelectMode(false);
        } else {
            super.onBackPressed();
        }
    }

    private void addPerson() {

    }

    private void removePerson() {

    }

    private void changeRoles() {
        ArrayList<String> userIds = new ArrayList<>();

        for (int i = 0; i < list.getCount(); i++) {
            View v = list.getChildAt(i);
            CheckBox c = (CheckBox)v.findViewById(R.id.member_checkbox);
            if (c.isChecked()) {
                userIds.add(members.get(i).getId());
            }
        }

        Intent intent = new Intent(this, ManageRolesActivity.class);
        intent.putExtra(ManageRolesActivity.GROUP_ID, group.getId());
        intent.putStringArrayListExtra(ManageRolesActivity.USER_IDS, userIds);
        startActivity(intent);
    }

    private void setSelectMode(boolean m) {
        selectMode = m;
        adapter.setSelectMode(selectMode);
        adapter.notifyDataSetChanged();
        invalidateOptionsMenu();
        if (selectMode) {
            setTitle(getString(R.string.title_members_select_mode));
        } else {
            setTitle(group.getName());
        }
    }
}