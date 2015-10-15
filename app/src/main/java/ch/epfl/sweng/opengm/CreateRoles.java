package ch.epfl.sweng.opengm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateRoles extends AppCompatActivity {
    private List<String> roles;
    private LinearLayout rolesAndButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roles);

        /* Grab roles from database, ideally the three default roles are
         * already there.*/
        String[] rolesArray = {"Administrator", "Moderator", "User"};
        roles = Arrays.asList(rolesArray);

        rolesAndButtons = (LinearLayout) findViewById(R.id.rolesAndButtons);
        fillWithRoles();
    }

    private void fillWithRoles() {
        int i = 0;
        for(String role : roles){
            TextView current = new TextView(getApplicationContext());
            current.setText(role);
            current.setTag("roleText" + i);

            Button currentButton = new Button(getApplicationContext());
            currentButton.setText("-");
            currentButton.setEnabled(false);
            currentButton.setTag("roleButton" + i);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            params.weight = 1.0f;

            TableRow currentRow = new TableRow(getApplicationContext());
            currentRow.setTag("row" + i);
            currentRow.setGravity(Gravity.CENTER_VERTICAL);
            currentRow.addView(current);
            currentRow.addView(currentButton);
            currentButton.setLayoutParams(params);

            rolesAndButtons.addView(currentRow);
        }
        Button addButton = new Button(getApplicationContext());
        addButton.setText("+");
        addButton.setTag("addButton");
        TableRow addRow = new TableRow(getApplicationContext());
        addRow.setGravity(Gravity.CENTER_VERTICAL);
        addRow.addView(addButton);
        rolesAndButtons.addView(addRow);
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
