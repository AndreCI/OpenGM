package ch.epfl.sweng.opengm;

import android.annotation.SuppressLint;
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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CreateRoles extends AppCompatActivity {
    private List<String> roles;
    private Map<Button, TableRow> buttonTableRowMap;
    private LinearLayout rolesAndButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roles);

        buttonTableRowMap = new Hashtable<>();

        /* TODO: Grab roles from database, ideally the three default roles are
         * already there.*/
        String[] rolesArray = {"Administrator", "Moderator", "User"};
        roles = Arrays.asList(rolesArray);

        rolesAndButtons = (LinearLayout) findViewById(R.id.rolesAndButtons);
        fillWithRoles();
        addNewRoleRow();
    }

    private void fillWithRoles() {
        for(String role : roles) {
            TextView current = getNewTextView(role);

            Button currentButton = getNewButton("-");
            currentButton.setEnabled(false);

            TableRow currentRow = getNewTableRow(current, currentButton);
            currentButton.setLayoutParams(getParamsForTableColumn());

            buttonTableRowMap.put(currentButton, currentRow);

            rolesAndButtons.addView(currentRow);
        }
    }

    private void addNewRoleRow(){
        Button addButton = getNewButton("+");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoleEditorField((Button) v);
            }
        });

        TableRow addRow = getNewTableRow(addButton, null);

        buttonTableRowMap.put(addButton, addRow);
        rolesAndButtons.addView(addRow);
    }

    private void addRoleEditorField(Button button){
        rolesAndButtons.removeView(buttonTableRowMap.get(button));
        final EditText newRoleEdit = new EditText(getApplicationContext());
        newRoleEdit.setHint("Enter role name.");

        Button okButton = getNewButton("OK");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roleName = newRoleEdit.getText().toString();
                addRole(roleName, (Button) v);
            }
        });

        TableRow newRoleRow = getNewTableRow(newRoleEdit, okButton);

        okButton.setLayoutParams(getParamsForTableColumn());

        buttonTableRowMap.put(okButton, newRoleRow);
        rolesAndButtons.addView(newRoleRow);
    }

    private void addRole(String name, Button button){
        TextView newRoleText = getNewTextView(name);

        Button newButton = getNewButton("-");
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRole((Button) v);
            }
        });

        TableRow newRow = getNewTableRow(newRoleText, newButton);

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
        newButton.setText("-");
        return newButton;
    }

    private void removeRole(Button button){
        rolesAndButtons.removeView(buttonTableRowMap.get(button));

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
