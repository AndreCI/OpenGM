package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.NetworkUtils;

public class ManageRolesActivity extends AppCompatActivity {
    private List<String> roles;
    private ListView rolesListView;

    private AlertDialog addRole;

    private RolesAdapter adapter;

    private List<PFMember> groupMembers;
    private PFGroup currentGroup;

    private List<String> addedRoles = new ArrayList<>();
    private List<String> removedRoles = new ArrayList<>();

    public final static String GROUP_ID = "ch.epfl.ch.opengm.groups.manageroles.groupid";
    public final static String USER_IDS = "ch.epfl.ch.opengm.groups.manageroles.userids";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_roles);

        if(NetworkUtils.haveInternet(getBaseContext())) {
            roles = new ArrayList<>();
            Intent intent = getIntent();
            String groupId = intent.getStringExtra(GROUP_ID);
            List<String> memberIDs = intent.getStringArrayListExtra(USER_IDS);
            PFMember member;
            groupMembers = new ArrayList<>();
            try {
                currentGroup = PFGroup.fetchExistingGroup(groupId);
                List<String> rolesFromServer = currentGroup.getRolesForUser(memberIDs.get(0));
                if (rolesFromServer != null) {
                    roles = new ArrayList<>(rolesFromServer);
                } else {
                    Toast.makeText(getBaseContext(), "Problem when loading roles for user " + memberIDs.get(0) + ": the user doesn't exist.", Toast.LENGTH_LONG).show();
                }
                for (String memberID : memberIDs) {
                    member = PFMember.fetchExistingMember(memberID);
                    keepIntersectionRoles(currentGroup.getRolesForUser(member.getId()));
                    groupMembers.add(member);
                }
            } catch (PFException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_add_role, null);


            rolesListView = (ListView) findViewById(R.id.rolesListView);
            adapter = new RolesAdapter(this, R.layout.item_role, roles);
            rolesListView.setAdapter(adapter);
            final EditText edit = (EditText)dialogLayout.findViewById(R.id.dialog_add_role_role);
            alertBuilder.setView(dialogLayout).setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String role = edit.getText().toString();
                    roles.add(role);
                    addedRoles.add(role);
                    adapter.notifyDataSetChanged();
                }
            }).setNegativeButton(R.string.cancel, null);
            addRole = alertBuilder.create();
        }
    }

    private void keepIntersectionRoles(List<String> otherRoles){
        ArrayList<String> toRemove = new ArrayList<>();
        for(String role : roles){
            if(!otherRoles.contains(role)){
                toRemove.add(role);
            }
        }
        roles.removeAll(toRemove);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_roles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.menu.menu_phone_number:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_role:
                addRole();
                return true;
            case R.id.action_remove_role:
                removeRole();
                return true;
            case R.id.action_modify_permissions:
                modifyPermissions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void modifyPermissions() {

    }

    private void removeRole() {
        List<String> rolesToRemove = getCheckedRoles();
        for(String role : rolesToRemove){
            if(!addedRoles.contains(role)){
                removedRoles.add(role);
            }
            roles.remove(role);
        }
        adapter.notifyDataSetChanged();
    }

    private void addRole(){
        addRole.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        addRole.show();

        EditText editText = (EditText) addRole.findViewById(R.id.dialog_add_role_role);
        editText.requestFocus();
    }

    private List<String> getCheckedRoles(){
        List<String> roles = new ArrayList<>();
        for(int i = 0; i < rolesListView.getCount(); i++){
            View v = rolesListView.getChildAt(i);
            CheckBox checkBox = (CheckBox)v.findViewById(R.id.role_checkbox);
            if(checkBox.isChecked()){
                TextView roleText = (TextView)v.findViewById(R.id.role_name);
                roles.add(roleText.getText().toString());
            }
        }
        return roles;
    }

    public void saveChanges(View view){
        if(NetworkUtils.haveInternet(getBaseContext())){
            for(PFMember member : groupMembers){
                for(String role : addedRoles){
                    currentGroup.addRoleToUser(role, member.getId());
                }
                for(String role : removedRoles){
                    currentGroup.removeRoleToUser(role, member.getId());
                }
            }
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "No internet connection, cannot save.", Toast.LENGTH_LONG).show();
        }
    }
}
