package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.utils.Alert;

public class MembersActivity extends AppCompatActivity {

    private AlertDialog addMember;
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

        // need to call it at the creation of each activity
        OpenGMApplication.setCurrentActivity(this);

        // get the group in which we are
        int groupId = getIntent().getIntExtra(GROUP_INDEX, -1);
        group = OpenGMApplication.getCurrentUser().getGroups().get(groupId);
        members = group.getMembers();

        if (getActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        selectMode = false;

        // create the dialog that add members which then only need to be shown
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        final EditText edit = (EditText)dialogLayout.findViewById(R.id.dialog_add_member_username);
        builder.setView(dialogLayout)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = String.valueOf(edit.getText());
                        addUser(username);
                        edit.getText().clear();
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        addMember = builder.create();

        // set the adapter for the list of member
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

        // when select mode click on item selects it
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectMode) {
                    ((CheckBox) view.findViewById(R.id.member_checkbox)).performClick();
                }
            }
        });
    }

    // to leave select mode when done managing roles
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
        addMember.show();
    }

    private void removePerson() {
        //TODO: implement method
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
        startActivityForResult(intent, 1);
    }

    // change to select mode or back to normal mode
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

    // add a user to the group and to the list that is displayed in background according to a username
    private void addUser(String username) {
        ParseQuery query = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
        query.whereEqualTo(PFConstants.USER_ENTRY_USERNAME, username);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) {
                    String userId = parseObject.getString(PFConstants.USER_ENTRY_USERID);
                    if (!group.containsMember(userId)) {
                        group.addUser(userId);
                        members.add(group.getMember(userId));
                        adapter.notifyDataSetChanged();
                    } else {
                        Alert.displayAlert("User already belongs to this group.");
                    }
                } else {
                    Alert.displayAlert("Could not found this username");
                }
            }
        });
    }
}