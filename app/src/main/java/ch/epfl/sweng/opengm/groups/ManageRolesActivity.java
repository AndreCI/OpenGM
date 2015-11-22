package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;
import ch.epfl.sweng.opengm.utils.NetworkUtils;

public class ManageRolesActivity extends AppCompatActivity {
    private List<String> roles;
    private ListView rolesListView;

    private AlertDialog addRole;
    private AlertDialog modifyPermissions;

    private RolesAdapter adapter;
    private PermissionsAdapter permissionsAdapter;

    private List<PFGroup.Permission> initialPermissions = new ArrayList<>();
    private List<PFGroup.Permission> permissions;
    private List<Boolean> checks;
    private List<String> modifyRoles = new ArrayList<>();

    private List<PFMember> groupMembers;
    private PFGroup currentGroup;

    private List<String> addedRoles = new ArrayList<>();
    private List<String> removedRoles = new ArrayList<>();

    private boolean isAdministrator = false;

    private int selected = 0;

    public final static String GROUP_INDEX = "ch.epfl.ch.opengm.groups.manageroles.groupIndex";
    public final static String USER_IDS = "ch.epfl.ch.opengm.groups.manageroles.userids";

    private Map<List<String>, Set<PFGroup.Permission>> addPermissionsForRoles = new HashMap<>();
    private Map<List<String>, Set<PFGroup.Permission>> removePermissionsForRoles = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_roles);
        roles = new ArrayList<>();
        Intent intent = getIntent();
        currentGroup = OpenGMApplication.getCurrentUser().getGroups().get(intent.getIntExtra(GROUP_INDEX, 1));
        List<String> memberIDs = intent.getStringArrayListExtra(USER_IDS);
        HashMap<String, PFMember> idsMembers = currentGroup.getMembers();
        groupMembers = new ArrayList<>();
        for(String memberID : memberIDs){
            groupMembers.add(idsMembers.get(memberID));
        }
        List<String> rolesFromServer = currentGroup.getRolesForUser(groupMembers.get(0).getId());
        if (rolesFromServer != null) {
            roles = new ArrayList<>(rolesFromServer);
        } else {
            Toast.makeText(getBaseContext(), "Problem when loading roles for user " + memberIDs.get(0) + ": the user doesn't exist.", Toast.LENGTH_LONG).show();
        }
        for (PFMember member : groupMembers) {
            keepIntersectionRoles(currentGroup.getRolesForUser(member.getId()));
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
                    if(!isAdministrator && role.equals("Administrator")){
                        Toast.makeText(getBaseContext(), "You cannot add administrator role if you're not an administrator.", Toast.LENGTH_LONG).show();
                    } else {
                        roles.add(role);
                        addedRoles.add(role);
                        adapter.notifyDataSetChanged();
                        edit.getText().clear();
                        invalidateOptionsMenu();
                    }
                }
            }).setNegativeButton(R.string.cancel, null);
        addRole = alertBuilder.create();

        permissions = new ArrayList<>(Arrays.asList(PFGroup.Permission.values()));
        checks = new ArrayList<>();
        for(int i = 0; i < permissions.size(); i++){
            checks.add(false);
        }

        AlertDialog.Builder permissionBuilder = new AlertDialog.Builder(this);
        View permissionLayout = getLayoutInflater().inflate(R.layout.dialog_modify_permissions, null);
        final ListView permissionList = (ListView) permissionLayout.findViewById(R.id.dialog_modify_permissions_list);
        permissionsAdapter = new PermissionsAdapter(this, R.layout.item_role, permissions, checks);
        permissionList.setAdapter(permissionsAdapter);
        permissionBuilder.setView(permissionLayout).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePermissionChanges(permissionList);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyRoles.clear();
                initialPermissions.clear();
                for(int i = 0; i < checks.size(); i++){
                    checks.set(i, false);
                }
                permissionsAdapter.notifyDataSetChanged();
            }
        });
        modifyPermissions = permissionBuilder.create();

        List<String> rolesForCurrent = currentGroup.getRolesForUser(OpenGMApplication.getCurrentUser().getId());
        if(rolesForCurrent != null){
            isAdministrator = rolesForCurrent.contains("Administrator");
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

    protected void updateOptions(boolean isChecked){
        if (isChecked) {
            selected++;
        } else {
            selected--;
        }
        invalidateOptionsMenu();
    }

    private void savePermissionChanges(ListView listView){
        List<PFGroup.Permission> addPermission = new ArrayList<>();
        List<PFGroup.Permission> removePermission = new ArrayList<>();
        for(int i = 0; i < listView.getCount(); i++){
            View view = listView.getChildAt(i);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.role_checkbox);
            PFGroup.Permission current = PFGroup.Permission.forName(((TextView) view.findViewById(R.id.role_name)).getText().toString());
            if(checkBox.isChecked()){
                if(!initialPermissions.contains(current)){
                    addPermission.add(current);
                }
            } else {
                if(initialPermissions.contains(current)){
                    removePermission.add(current);
                }
            }
            checks.set(i, checkBox.isChecked());
            permissionsAdapter.notifyDataSetChanged();
        }
        boolean found = false;
        for(Map.Entry<List<String>, Set<PFGroup.Permission>> entry : addPermissionsForRoles.entrySet()){
            if(entry.getKey().containsAll(modifyRoles)) {
                entry.getValue().addAll(new HashSet<>(addPermission));
                found = true;
            }
        }
        if(!found){
            addPermissionsForRoles.put(new ArrayList<>(modifyRoles), new HashSet<>(addPermission));
            found = false;
        }
        for(Map.Entry<List<String>, Set<PFGroup.Permission>> entry : removePermissionsForRoles.entrySet()){
            if(entry.getKey().containsAll(modifyRoles)){
                entry.getValue().addAll(new HashSet<>(removePermission));
                found = true;
            }
        }
        if(!found){
            removePermissionsForRoles.put(new ArrayList<>(modifyRoles), new HashSet<>(removePermission));
        }
//        for(String role : modifyRoles){
//            for(PFGroup.Permission permission : addPermission){
//                currentGroup.addPermissionToRole(role, permission);
//            }
//            for(PFGroup.Permission permission : removePermission){
//                currentGroup.removePermissionFromRole(role, permission);
//            }
//        }
        modifyRoles.clear();
        initialPermissions.clear();
        for(int i = 0; i < checks.size(); i++){
            checks.set(i, false);
        }
        permissionsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_roles, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_modify_permissions).setVisible(selected != 0);
        menu.findItem(R.id.action_remove_role).setVisible(selected != 0);
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
        List<String> checkedRoles = getCheckedRoles(false);
        List<PFGroup.Permission> permissions;
        if(currentGroup.getRoles().contains(checkedRoles.get(0))){
            permissions = new ArrayList<>(currentGroup.getPermissionsForRole(checkedRoles.get(0)));
        } else {
            permissions = new ArrayList<>();
        }

        for(int i = 1; i < checkedRoles.size(); i++){
            permissions = keepPermissionIntersection(permissions, new ArrayList<>(currentGroup.getPermissionsForRole(checkedRoles.get(i))));
        }
        // To the ones that have been fetched, add those that could have been added before saving.
        for(List<String> roles : addPermissionsForRoles.keySet()){
            if(roles.containsAll(checkedRoles)){
                permissions.addAll(addPermissionsForRoles.get(roles));
            }
        }
        // To the ones that have been fetched, remove those that could have been removed before saving.
        for(List<String> roles : removePermissionsForRoles.keySet()){
            if(roles.containsAll(checkedRoles)) {
                permissions.removeAll(removePermissionsForRoles.get(roles));
            }
        }
        modifyRoles.addAll(checkedRoles);
        initialPermissions.addAll(permissions);
        for(int i = 0; i < checks.size(); i++){
            if(permissions.contains(this.permissions.get(i))){
                checks.set(i, true);
            }
        }
        Log.d("CHECKS", checks.toString());
        modifyPermissions.show();
        permissionsAdapter.notifyDataSetChanged();
        invalidateOptionsMenu();
    }

    private List<PFGroup.Permission> keepPermissionIntersection(List<PFGroup.Permission> base, List<PFGroup.Permission> other){
        ArrayList<PFGroup.Permission> toRemove = new ArrayList<>();
        for(PFGroup.Permission permission : base){
            if(!other.contains(permission)){
                toRemove.add(permission);
            }
        }
        base.removeAll(toRemove);
        return base;
    }

    private void removeRole() {
        List<String> rolesToRemove = getCheckedRoles(true);
        if(rolesToRemove.contains("Administrator") && !isAdministrator){
            Toast.makeText(this, "You cannot remove Administrator role without being an Administrator.", Toast.LENGTH_LONG).show();
            return;
        }
        for(String role : rolesToRemove){
            if(!addedRoles.contains(role)){
                removedRoles.add(role);
            }
            roles.remove(role);
        }
        adapter.notifyDataSetChanged();
        invalidateOptionsMenu();
    }

    private void addRole(){
        addRole.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        addRole.show();

        EditText editText = (EditText) addRole.findViewById(R.id.dialog_add_role_role);
        editText.requestFocus();
    }

    private List<String> getCheckedRoles(boolean uncheck){
        List<String> roles = new ArrayList<>();
        for(int i = 0; i < rolesListView.getCount(); i++){
            View v = rolesListView.getChildAt(i);
            CheckBox checkBox = (CheckBox)v.findViewById(R.id.role_checkbox);
            if(checkBox.isChecked()){
                TextView roleText = (TextView)v.findViewById(R.id.role_name);
                roles.add(roleText.getText().toString());
                checkBox.setChecked(!uncheck);
                selected--;
                invalidateOptionsMenu();
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
            for(Map.Entry<List<String>, Set<PFGroup.Permission>> entry : addPermissionsForRoles.entrySet()){
                for(String role : entry.getKey()){
                    for(PFGroup.Permission permission : entry.getValue()){
                        currentGroup.addPermissionToRole(role, permission);
                    }
                }
            }
            Log.d("TESTING", removePermissionsForRoles.toString());
            for(Map.Entry<List<String>, Set<PFGroup.Permission>> entry : removePermissionsForRoles.entrySet()){
                for(String role : entry.getKey()){
                    for(PFGroup.Permission permission : entry.getValue()){
                        currentGroup.removePermissionFromRole(role, permission);
                    }
                }
            }
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "No internet connection, cannot save.", Toast.LENGTH_LONG).show();
        }
    }
}
