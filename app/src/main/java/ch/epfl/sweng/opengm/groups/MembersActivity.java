package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.identification.InputUtils;
import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

public class MembersActivity extends AppCompatActivity {

    private AlertDialog addMember;
    private ListView list;
    private PFGroup group;
    private MembersAdapter adapter;
    private List<PFMember> members;
    private int groupIndex;
    private boolean selectMode;

    public static final String GROUP_INDEX = "ch.epfl.sweng.opengm.groups.members.groupindex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        // get the group in which we are
        groupIndex = getIntent().getIntExtra(GROUP_INDEX, -1);
        PFUser user = OpenGMApplication.getCurrentUser();
        group = user.getGroups().get(groupIndex);
        members = new ArrayList<>();
        members.add(group.getMember(user.getId()));
        members.addAll(group.getMembersWithoutUser(user.getId()));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        selectMode = false;

        // create the dialog that add members which then only need to be shown
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_add_member, null);
        final EditText edit = (EditText) dialogLayout.findViewById(R.id.dialog_add_member_username);
        builder.setView(dialogLayout)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String usernameOrMail = String.valueOf(edit.getText());
                        addUser(usernameOrMail);
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
                ((CheckBox) view.findViewById(R.id.member_checkbox)).setChecked(true);
                return true;
            }
        });

        // when select mode click on item selects it
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectMode) {
                    view.findViewById(R.id.member_checkbox).performClick();
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
                onBackPressed();
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
            Intent i = new Intent();
            i.putExtra(GROUP_INDEX, groupIndex);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    private void addPerson() {
        // display the keyboard
        addMember.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        addMember.show();

        // focus the field to enter username
        EditText edit = (EditText)addMember.findViewById(R.id.dialog_add_member_username);
        edit.requestFocus();
    }

    // if no connection we'll get a consitency problem between members and what actually is on parse
    private void removePerson() {
        ArrayList<String> userIds = getCheckedIds(true);

        adapter.notifyDataSetChanged();

        for (String userId : userIds) {
            group.removeUser(userId);
        }

        setSelectMode(false);
    }

    private void changeRoles() {
        ArrayList<String> userIds = getCheckedIds(false);

        if (!userIds.isEmpty()) {
            Intent intent = new Intent(this, ManageRolesActivity.class);
            intent.putExtra(ManageRolesActivity.GROUP_ID, group.getId());
            intent.putStringArrayListExtra(ManageRolesActivity.USER_IDS, userIds);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(getBaseContext(), "Please select at least one member", Toast.LENGTH_LONG).show();
        }
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
    private void addUser(String usernameOrMail) {
        // select between username or mail
        String field;
        if (InputUtils.isEmailValid(usernameOrMail)) {
            field = PFConstants._USER_TABLE_EMAIL;
        } else {
            field = PFConstants._USER_TABLE_USERNAME;
        }

        // the actual query
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PFConstants._USER_TABLE_NAME);
        query.whereEqualTo(field, usernameOrMail);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) {
                    String userId = parseObject.getObjectId();
                    if (!group.containsMember(userId)) {
                        // add the user to the group and to the list
                        group.addUserWithId(userId);
                        members.add(group.getMember(userId));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getBaseContext(), "User already belongs to this group.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Could not find this user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // get the user ids of the checked user and delete them from members if rm is true
    private ArrayList<String> getCheckedIds(boolean rm) {
        ArrayList<String> userIds = new ArrayList<>();
        ArrayList<PFMember> membersToRemove = new ArrayList<>();

        for (int i = 0; i < list.getCount(); i++) {
            View v = list.getChildAt(i);
            CheckBox c = (CheckBox) v.findViewById(R.id.member_checkbox);
            if (c.isChecked()) {
                c.setChecked(false);
                userIds.add(members.get(i).getId());
                if (rm) {
                    if (i != 0) {
                        membersToRemove.add(members.get(i));
                    } else {
                        userIds.remove(0);
                        Toast.makeText(getBaseContext(), "You can't supress yourself from a group.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        members.removeAll(membersToRemove);

        return userIds;
    }
}