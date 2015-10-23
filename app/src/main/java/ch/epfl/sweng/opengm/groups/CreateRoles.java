package ch.epfl.sweng.opengm.groups;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFUser;
import ch.epfl.sweng.opengm.utils.Alert;

public class CreateRoles extends AppCompatActivity {
    private List<String> roles;
    private Map<Button, TableRow> buttonTableRowMap;
    private LinearLayout rolesAndButtons;

    private int rowCount;
    private int roleTextCount;
    private int roleButtonCount;

    public final static String GROUP_LIST_KEY = "ch.epfl.ch.opengm.groups.creategroup.grouplist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roles);

        buttonTableRowMap = new Hashtable<>();

        /* TODO: Grab roles from database, ideally the three default roles are
         * already there.*/
        // Hardcoding to make this application properly testable
        PFUser.Builder userBuilder = new PFUser.Builder("oqMblls8Cb");
        PFUser user = null;
        try {
            user = userBuilder.build();
        } catch (PFException e) {
            e.printStackTrace();
        }
        //

        roles = new ArrayList<>();
        Intent intent = getIntent();
        //Uncomment this when testing with real app
        //int groupId = intent.getIntExtra(GROUP_LIST_KEY, -1);
        int groupId = 0;
        if(groupId != -1){
            roles = user.getGroups().get(groupId).getRoles();
        } else {
            Alert.displayAlert("No current group");
        }


        rolesAndButtons = (LinearLayout) findViewById(R.id.rolesAndButtons);
        fillWithRoles();
        addNewRoleRow();
    }

    private void fillWithRoles() {
        for(String role : roles) {
            TextView current = getNewTextView(role);
            current.setTag("roleName" + roleTextCount);
            roleTextCount++;

            Button currentButton = getNewButton("-");
            currentButton.setEnabled(false);
            currentButton.setTag("removeRole" + roleButtonCount);
            roleButtonCount++;

            TableRow currentRow = getNewTableRow(current, currentButton);
            currentRow.setTag("roleRow" + rowCount);
            rowCount++;
            currentButton.setLayoutParams(getParamsForTableColumn());

            buttonTableRowMap.put(currentButton, currentRow);

            rolesAndButtons.addView(currentRow);
        }
    }

    private void addNewRoleRow(){
        Button addButton = getNewButton("+");
        addButton.setTag("addRole");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoleEditorField((Button) v);
            }
        });

        TableRow addRow = getNewTableRow(addButton, null);
        addRow.setTag("addRow");

        buttonTableRowMap.put(addButton, addRow);
        rolesAndButtons.addView(addRow);
    }

    private void addRoleEditorField(Button button){
        rolesAndButtons.removeView(buttonTableRowMap.get(button));
        final EditText newRoleEdit = new EditText(getApplicationContext());
        newRoleEdit.setTag("newRoleEdit");
        newRoleEdit.setHint("Enter role name.");

        Button okButton = getNewButton("OK");
        okButton.setTag("okButton");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roleName = newRoleEdit.getText().toString();
                addRole(roleName, (Button) v);
            }
        });

        TableRow newRoleRow = getNewTableRow(newRoleEdit, okButton);
        newRoleRow.setTag("editRoleRow");

        okButton.setLayoutParams(getParamsForTableColumn());

        buttonTableRowMap.put(okButton, newRoleRow);
        rolesAndButtons.addView(newRoleRow);
    }

    private void addRole(String name, Button button){
        TextView newRoleText = getNewTextView(name);
        newRoleText.setTag("roleName" + roleTextCount);
        roleTextCount++;

        Button newButton = getNewButton("-");
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRole((Button) v);
            }
        });
        newButton.setTag("removeRole" + roleButtonCount);
        roleButtonCount++;

        TableRow newRow = getNewTableRow(newRoleText, newButton);
        newRow.setTag("roleRow" + rowCount);
        rowCount++;

        newButton.setLayoutParams(getParamsForTableColumn());

        buttonTableRowMap.put(newButton, newRow);

        // TODO: Add role to database

        // TODO: Exit keyboard if currently typing.
        rolesAndButtons.addView(newRow);
        rolesAndButtons.removeView(buttonTableRowMap.get(button));
        addNewRoleRow();
    }

    @SuppressLint("RtlHardcoded")
    private TableRow.LayoutParams getParamsForTableColumn(){
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.weight = 1.0f;

        return params;
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

    private void removeRole(Button button){
        rolesAndButtons.removeView(buttonTableRowMap.get(button));
        rowCount--;
        roleButtonCount--;
        roleTextCount--;
        // TODO: Remove role from the database
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_roles, menu);
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
}
