package ch.epfl.sweng.opengm.groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.NetworkUtils;

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

    private PFGroup currentGroup = null;
    private int groupIndex;

    private String initialName = null;
    private String initialDescription = null;

    public static final String GROUP_INDEX = "ch.epfl.sweng.opengm.groups.createGroupActivity.groupIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.create_group_outmostLayout);
        onTapOutsideBehaviour(layout, this);

        Intent intent = getIntent();
        groupIndex = intent.getIntExtra(GROUP_INDEX, -1);

        mGroupName = (EditText) findViewById(R.id.enterGroupName);
        mGroupDescription = (EditText) findViewById(R.id.enterGroupDescription);

        if(groupIndex >= 0){
            currentGroup = OpenGMApplication.getCurrentUser().getGroups().get(groupIndex);
            mGroupName.setText(currentGroup.getName());
            mGroupName.setText(currentGroup.getDescription());
            initialName = currentGroup.getName();
            initialDescription = currentGroup.getDescription();
        }
    }

    public void manageMembers(View view) {

    }

    public void createGroup(View view) {
        if(NetworkUtils.haveInternet(getBaseContext())) {
            String name = mGroupName.getText().toString();
            String description = mGroupDescription.getText().toString();
            // TODO : retrieve image from button
            // If next activity is group page, also call function to put new group in the database

            int groupNameValid = isGroupNameValid(name);
            if (groupNameValid == INPUT_CORRECT) {
                if(groupIndex >= 0){
                    if(!name.equals(initialName)){
                        currentGroup.setName(name);
                    }
                    if(!description.equals(initialDescription)){
                        currentGroup.setDescription(description);
                    }
                    finish();
                } else {
                    try {
                        PFGroup newGroup = PFGroup.createNewGroup(getCurrentUser(), name, description, null);
                        getCurrentUser().addToAGroup(newGroup);
                        startActivity(new Intent(CreateGroupActivity.this, GroupsHomeActivity.class).putExtra(CHOSEN_GROUP_KEY, getCurrentUser().getGroups().size() - 1));
                    } catch (PFException e) {
                        Toast.makeText(getBaseContext(), "Couldn't create the group: there where problems when contacting the server.", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                String errorMessage;
                switch (groupNameValid) {
                    case INPUT_TOO_SHORT:
                        errorMessage = getString(R.string.groupNameTooShort);
                        break;
                    case INPUT_TOO_LONG:
                        errorMessage = getString(R.string.groupNameTooLong);
                        break;
                    case INPUT_BEGINS_WITH_SPACE:
                        errorMessage = getString(R.string.groupNameStartsWithSpace);
                        break;
                    case INPUT_WITH_SYMBOL:
                        errorMessage = getString(R.string.groupNameIllegalCharacters);
                        break;
                    default:
                        errorMessage = getString(R.string.groupNameInvalid);
                        break;
                }
                mGroupName.setError(errorMessage);
                mGroupName.requestFocus();
            }
        }
    }
}
