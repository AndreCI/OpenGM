package ch.epfl.sweng.opengm.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.Alert;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;
import static ch.epfl.sweng.opengm.groups.GroupsHomeActivity.CHOSEN_GROUP_KEY;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_BEGINS_WITH_SPACE;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_CORRECT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_LONG;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_SHORT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITH_SYMBOL;
import static ch.epfl.sweng.opengm.identification.InputUtils.isGroupNameValid;
import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText mGroupName;
    private EditText mGroupDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.create_group_outmostLayout);
        onTapOutsideBehaviour(layout, this);

        mGroupName = (EditText) findViewById(R.id.enterGroupName);
        mGroupDescription = (EditText) findViewById(R.id.enterGroupDescription);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
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

    public void createGroup(View view) {
        String name = mGroupName.getText().toString();
        String description = mGroupDescription.getText().toString();
        // TODO : retrieve image from button
        // If next activity is group page, also call function to put new group in the database

        int groupNameValid = isGroupNameValid(name);
        if (groupNameValid == INPUT_CORRECT) {
            try {
                PFGroup newGroup = PFGroup.createNewGroup(getCurrentUser(), name, description, null);
                getCurrentUser().addToAGroup(newGroup);
                startActivity(new Intent(CreateGroupActivity.this, GroupsHomeActivity.class).putExtra(CHOSEN_GROUP_KEY, getCurrentUser().getGroups().size() - 1));
            } catch (PFException e) {
                Alert.displayAlert("Couldn't create the group, there were problems when contacting the server.");
            }
        } else {
            String errorMessage;
            switch (groupNameValid) {
                case INPUT_TOO_SHORT:
                    errorMessage = "Group name is too short";
                    break;
                case INPUT_TOO_LONG:
                    errorMessage = "Group name is too long";
                    break;
                case INPUT_BEGINS_WITH_SPACE:
                    errorMessage = "Group name cannot start with a space";
                    break;
                case INPUT_WITH_SYMBOL:
                    errorMessage = "Group name contains illegal characters, only letters, numbers and spaces allowed.";
                    break;
                default:
                    errorMessage = "Group name is invalid";
                    break;
            }
            mGroupName.setError(errorMessage);
            mGroupName.requestFocus();
        }
    }
}