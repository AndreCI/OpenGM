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
import ch.epfl.sweng.opengm.identification.InputUtils;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.Alert;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;
import static ch.epfl.sweng.opengm.groups.GroupsHomeActivity.CHOOSEN_GROUP_KEY;
import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;

public class CreateGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.create_group_outmostLayout);
        onTapOutsideBehaviour(layout, this);
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
        String groupName = ((EditText) findViewById(R.id.enterGroupName)).getText().toString();
        String groupDescription = ((EditText) findViewById(R.id.enterGroupDescription)).getText().toString();
        // TODO : retrieve image from button
        // TODO : call intent for next activity
        // If next activity is group page, also call function to put new gorup in the databse

        int groupNameValid = InputUtils.isGroupNameValid(groupName);
        if (groupNameValid == InputUtils.INPUT_CORRECT) {
            try {
                PFGroup newGroup = PFGroup.createNewGroup(getCurrentUser(), groupName, groupDescription, null);
                getCurrentUser().addToAGroup(newGroup);
                startActivity(new Intent(CreateGroup.this, GroupsHomeActivity.class).putExtra(CHOOSEN_GROUP_KEY, getCurrentUser().getGroups().size() - 1));
            } catch (PFException e) {
                Alert.displayAlert("Couldn't create the group, there were problems when contacting the server.");
            }
        } else {
            String errorMessage;
            switch (groupNameValid) {
                case InputUtils.INPUT_TOO_SHORT:
                    errorMessage = "Group name is too short";
                    break;
                case InputUtils.INPUT_TOO_LONG:
                    errorMessage = "Group name is too long";
                    break;
                case InputUtils.INPUT_BEGINS_WITH_SPACE:
                    errorMessage = "Group name cannot start with a space";
                    break;
                case InputUtils.INPUT_WITH_SYMBOL:
                    errorMessage = "Group name contains illegal characters, only letters, numbers and spaces allowed.";
                    break;
                default:
                    errorMessage = "Group name is invalid";
                    break;
            }
            ((EditText) findViewById(R.id.enterGroupName)).setError(errorMessage);
        }
    }
}
