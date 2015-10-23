package ch.epfl.sweng.opengm.groups;

import android.graphics.Bitmap;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.Alert;

public class CreateGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
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

    public void createGroup(View view){
        String groupName = ((EditText) findViewById(R.id.enterGroupName)).getText().toString();
        String groupDescription = ((EditText) findViewById(R.id.enterGroupDescription)).getText().toString();
        // TODO : retrieve image from button
        // TODO : call intent for next activity
        // If next activity is group page, also call function to put new gorup in the databse

        if(isGroupNameCorrect(groupName)){
            try {
                PFGroup newGroup = PFGroup.createNewGroup(OpenGMApplication.getCurrentUser(), groupName, groupDescription, null);
                OpenGMApplication.getCurrentUser().addToAGroup(newGroup);
            } catch (PFException e) {
                Alert.displayAlert("Couldn't create the group, there were problems when contacting the server.");
            }
        } else {
            ((EditText) findViewById(R.id.enterGroupName)).setError("Group name cannot be empty");
        }
    }

    private boolean isGroupNameCorrect(String groupName){
        return true;
    }
}
