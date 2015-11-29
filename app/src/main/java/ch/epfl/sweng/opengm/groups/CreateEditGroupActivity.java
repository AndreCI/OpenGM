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
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_BEGINS_WITH_SPACE;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_CORRECT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_LONG;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_SHORT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITH_SYMBOL;
import static ch.epfl.sweng.opengm.identification.InputUtils.isGroupNameValid;
import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;

public class CreateEditGroupActivity extends AppCompatActivity {

    private EditText mGroupName;
    private EditText mGroupDescription;

    private PFGroup currentGroup = null;

    private String initialName = null;
    private String initialDescription = null;

    private boolean isCreatingAGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.create_group_outmostLayout);
        onTapOutsideBehaviour(layout, this);

        mGroupName = (EditText) findViewById(R.id.enterGroupName);
        mGroupDescription = (EditText) findViewById(R.id.enterGroupDescription);

        currentGroup = OpenGMApplication.getCurrentGroup();

        if (currentGroup != null) {
            mGroupName.setText(currentGroup.getName());
            mGroupDescription.setText(currentGroup.getDescription());
            initialName = currentGroup.getName();
            initialDescription = currentGroup.getDescription();
            findViewById(R.id.createGroupsMembersButton).setVisibility(View.VISIBLE);
        }
    }

    public void manageMembers(View view) {
        Intent intent = new Intent(this, MembersActivity.class);
        startActivity(intent);
    }

    public void createGroup(View view) {
        if (NetworkUtils.haveInternet(getBaseContext()) && !isCreatingAGroup) {
            isCreatingAGroup = true;
            String name = mGroupName.getText().toString();
            String description = mGroupDescription.getText().toString();
            // TODO : retrieve image from button

            int groupNameValid = isGroupNameValid(name);

            if (groupNameValid == INPUT_CORRECT) {
                if (currentGroup != null) {
                    if (!name.equals(initialName)) {
                        currentGroup.setName(name);
                    }
                    if (!description.equals(initialDescription)) {
                        currentGroup.setDescription(description);
                    }
                    setResult(RESULT_OK);
                    finish();
                } else {
                    try {
                        PFGroup newGroup = PFGroup.createNewGroup(getCurrentUser(), name, description, null);
                        getCurrentUser().addToAGroup(newGroup);
                        OpenGMApplication.setCurrentGroup(OpenGMApplication.getCurrentUser().getGroups().size() - 1);
                        startActivity(new Intent(CreateEditGroupActivity.this, GroupsHomeActivity.class));
                    } catch (PFException e) {
                        Toast.makeText(getBaseContext(), "Couldn't create the group: there where problems when contacting the server.", Toast.LENGTH_LONG).show();
                        isCreatingAGroup = false;
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
                isCreatingAGroup = false;
            }
        }
    }
}
