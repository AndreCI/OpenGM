package ch.epfl.sweng.opengm.groups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.Alert;

public class ManageRolesActivity extends AppCompatActivity {
    private List<String> roles;
    private Map<CheckBox, TableRow> boxesAndRows;
    private LinearLayout rolesAndButtons;
    private TableRow addRoleRow;

    private int roleRowCount;
    private int roleTextCount;
    private int roleBoxCount;

    private List<PFMember> groupMembers;
    private PFGroup currentGroup;
    private Map<CheckBox, Boolean> modifiedCheckBoxes;

    public final static String GROUP_ID = "ch.epfl.ch.opengm.groups.manageroles.groupid";
    public final static String USER_IDS = "ch.epfl.ch.opengm.groups.manageroles.userids";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_roles);

        boxesAndRows = new Hashtable<>();
        modifiedCheckBoxes = new Hashtable<>();

        roles = new ArrayList<>();
        Intent intent = getIntent();
        //Uncomment this when testing with real app
        String groupId = intent.getStringExtra(GROUP_ID);
        List<String> memberIDs = intent.getStringArrayListExtra(USER_IDS);
        PFMember member;
        groupMembers = new ArrayList<>();
        try {
            currentGroup = PFGroup.fetchExistingGroup(groupId);
            List<String> rolesFromServer = currentGroup.getRolesForUser(memberIDs.get(0));
            if(rolesFromServer != null){
                roles = new ArrayList<>(rolesFromServer);
            } else {
                Alert.displayAlert("Problem when loading roles for user " + memberIDs.get(0) + ": the user doesn't exist.");
            }
            for(String memberID : memberIDs) {
                member = PFMember.fetchExistingMember(memberID);
                keepIntersectionRoles(currentGroup.getRolesForUser(member.getId()));
                groupMembers.add(member);
            }
        } catch (PFException e) {
            e.printStackTrace();
        }
        
        rolesAndButtons = (LinearLayout) findViewById(R.id.rolesAndButtons);
        fillWithRoles();
        addNewRoleRow();
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

    private void fillWithRoles() {
        for(String role : roles) {
            TextView currentRole = getNewTextView(role);
            currentRole.setTag("roleName" + roleTextCount);
            roleTextCount++;

            CheckBox box = new CheckBox(getApplicationContext());
            box.setTag("roleBox" + roleBoxCount);
            box.setChecked(true);
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifiedCheckBoxes.put((CheckBox) v, true);
                }
            });
            roleBoxCount++;

            TableRow currentRow = getNewTableRow(box, currentRole);
            currentRow.setTag("roleRow" + roleRowCount);
            roleRowCount++;

            boxesAndRows.put(box, currentRow);
            modifiedCheckBoxes.put(box, false);

            rolesAndButtons.addView(currentRow);
        }
    }

    private void addNewRoleRow(){
        Button addButton = getNewButton("+");
        addButton.setTag("addRole");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoleEditorField();
            }
        });

        TableRow addRow = getNewTableRow(addButton, null);
        addRow.setTag("addRow");

        addRoleRow = addRow;
        rolesAndButtons.addView(addRow);
    }

    private void addRoleEditorField(){
        rolesAndButtons.removeView(addRoleRow);
        final EditText newRoleEdit = new EditText(getApplicationContext());
        newRoleEdit.setTag("newRoleEdit");
        newRoleEdit.setHint("Enter role name.");
        newRoleEdit.setSingleLine(true);

        Button okButton = getNewButton("OK");
        okButton.setTag("okButton");

        final TableRow newRoleRow = getNewTableRow(newRoleEdit, okButton);
        newRoleRow.setTag("editRoleRow");

        okButton.setLayoutParams(getParamsForTableColumn());

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roleName = newRoleEdit.getText().toString();
                if(roleName.length() == 0){
                    newRoleEdit.setError("Role name shouldn't be empty");
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(newRoleEdit.getWindowToken(), 0);
                    addRole(roleName, newRoleRow);
                }
            }
        });

        rolesAndButtons.addView(newRoleRow);
    }

    private void addRole(String name, TableRow editRow){
        TextView newRoleText = getNewTextView(name);
        newRoleText.setTag("roleName" + roleTextCount);
        roleTextCount++;

        CheckBox box = new CheckBox(getApplicationContext());
        box.setChecked(true);
        box.setEnabled(false);
        box.setTag("roleBox" + roleBoxCount);

        Button newButton = getNewButton("-");
        newButton.setTag("removeRole" + roleBoxCount);
        roleBoxCount++;

        final TableRow newRow = getNewTableRow(box, newRoleText);
        newRow.setTag("roleRow" + roleRowCount);
        roleRowCount++;
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRole(newRow);
            }
        });
        newRow.addView(newButton);
        newButton.setLayoutParams(getParamsForTableColumn());

        boxesAndRows.put(box, newRow);
        modifiedCheckBoxes.put(box, true);

        rolesAndButtons.addView(newRow);
        rolesAndButtons.removeView(editRow);
        addNewRoleRow();
    }

    @SuppressLint("RtlHardcoded")
    private TableRow.LayoutParams getParamsForTableColumn(){
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.weight = 1.0f;

        return params;
    }

    public void removeRole(TableRow roleRow){
        CheckBox toDelete = ((CheckBox)roleRow.getChildAt(0));
        toDelete.setChecked(false);
        modifiedCheckBoxes.put(toDelete, false);
        rolesAndButtons.removeView(roleRow);
        roleRowCount--;
        roleTextCount--;
        roleBoxCount--;
    }

    private TableRow getNewTableRow(View elem1, View elem2){
        TableRow currentRow = new TableRow(getApplicationContext());
        currentRow.setGravity(Gravity.CENTER_VERTICAL);
        currentRow.addView(elem1);
        if(elem2 != null){
            currentRow.addView(elem2);
        }
        return currentRow;
    }

    private TextView getNewTextView(String text){
        TextView newText = new TextView(getApplicationContext());
        newText.setText(text);
        return newText;
    }

    private Button getNewButton(String text){
        Button newButton = new Button(getApplicationContext());
        newButton.setText(text);
        return newButton;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_roles, menu);
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

    public void doneManageRoles(View view){
        for(CheckBox box : boxesAndRows.keySet()){
            for(PFMember member : groupMembers){
                if(box.isChecked() && modifiedCheckBoxes.get(box)){
                    currentGroup.addRoleToUser(((TextView) boxesAndRows.get(box).getChildAt(1)).getText().toString(), member.getId());
                } else if(!box.isChecked() && modifiedCheckBoxes.get(box)){
                    currentGroup.removeRoleToUser(((TextView) boxesAndRows.get(box).getChildAt(1)).getText().toString(), member.getId());
                }
            }
        }
        finish();
    }
}
