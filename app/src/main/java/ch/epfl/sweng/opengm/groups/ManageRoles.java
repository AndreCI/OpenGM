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

public class ManageRoles extends AppCompatActivity {
    private List<String> roles;
    private Map<CheckBox, TableRow> boxesAndRows;
    private LinearLayout rolesAndButtons;
    private TableRow addRoleRow;

    private int roleRowCount;
    private int roleTextCount;
    private int roleBoxCount;

    private List<PFMember> groupMembers;
    private PFGroup currentGroup;

    public final static String GROUP_ID = "ch.epfl.ch.opengm.groups.manageroles.groupid";
    public final static String USER_ID = "ch.epfl.ch.opengm.groups.manageroles.userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_roles);

        boxesAndRows = new Hashtable<>();

        /* TODO: Grab roles from database, ideally the three default roles are
         * already there.*/
        // Hardcoding to make this application properly testable -----------------------------------
//        PFUser user = null;
//        try {
//            user = PFUser.fetchExistingUser("f9PMNCFLXN");
//        } catch (PFException e) {
//            e.printStackTrace();
//        }
//        try {
//            currentGroup = PFGroup.fetchExistingGroup("9E0kzVZF4i");
//        } catch (PFException e) {
//            e.printStackTrace();
//        }
//        //Receive this from intent
//        groupMembers = new ArrayList<>();
//        try {
//            groupMembers.add(PFMember.fetchExistingMember("f9PMNCFLXN"));
//        } catch (PFException e) {
//            e.printStackTrace();
//        }
        // -----------------------------------------------------------------------------------------

        roles = new ArrayList<>();
        Intent intent = getIntent();
        //Uncomment this when testing with real app
        String groupId = intent.getStringExtra(GROUP_ID);
        String memberID = intent.getStringExtra(USER_ID);
        PFMember member;
        groupMembers = new ArrayList<>();
        try {
            currentGroup = PFGroup.fetchExistingGroup(groupId);
            member = PFMember.fetchExistingMember(memberID);
            roles = currentGroup.getRolesForUser(member.getId());
            groupMembers.add(member);
        } catch (PFException e) {
            e.printStackTrace();
        }



        rolesAndButtons = (LinearLayout) findViewById(R.id.rolesAndButtons);
        fillWithRoles();
        addNewRoleRow();
    }

    private void fillWithRoles() {
        for(String role : roles) {
            TextView currentRole = getNewTextView(role);
            currentRole.setTag("roleName" + roleTextCount);
            roleTextCount++;

            CheckBox box = new CheckBox(getApplicationContext());
            box.setTag("roleBox" + roleBoxCount);
            box.setChecked(true);
            roleBoxCount++;

            TableRow currentRow = getNewTableRow(box, currentRole);
            currentRow.setTag("roleRow" + roleRowCount);
            roleRowCount++;

            boxesAndRows.put(box, currentRow);

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
        ((CheckBox)roleRow.getChildAt(0)).setChecked(false);
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
                if(box.isChecked()){
                    currentGroup.addRoleToUser(((TextView) boxesAndRows.get(box).getChildAt(1)).getText().toString(), member.getId());
                } else {
                    currentGroup.removeRoleToUser(((TextView) boxesAndRows.get(box).getChildAt(1)).getText().toString(), member.getId());
                }
            }
        }
    }
}
