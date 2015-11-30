package ch.epfl.sweng.opengm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.opengm.parse.PFUser;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_user_profile:
                startActivity(new Intent(MyProfileActivity.this, EditUserProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);

        // FIXME: Do we really need to specify if (user != null) ??? Where to put it ?
        PFUser currentUser = OpenGMApplication.getCurrentUser();
        if (currentUser != null) {

            // Display profile picture of user :
            ImageView photoImageView = (ImageView) findViewById(R.id.userPhoto);
            photoImageView.setImageResource(R.drawable.avatar_male1);

            // Display first name of user :
            TextView firstNameTextView = (TextView) findViewById(R.id.firstNameTV);
            firstNameTextView.setText(currentUser.getFirstName());

            // Display last name of user :
            TextView lastNameTextView = (TextView) findViewById(R.id.lastNameTV);
            lastNameTextView.setText(currentUser.getLastName());

            // Display username of user :
            TextView usernameTextView = (TextView) findViewById(R.id.usernameTV);
            usernameTextView.setText(currentUser.getUsername());

            // Display e-mail address of user :
            TextView emailTextView = (TextView) findViewById(R.id.emailTV);
            emailTextView.setText(currentUser.getEmail());

            // Display phone number of user :
            TextView phoneNumberTextView = (TextView) findViewById(R.id.phoneTV);
            phoneNumberTextView.setText(currentUser.getPhoneNumber());

            // Display description of user :
            TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTV);
            descriptionTextView.setText(currentUser.getAboutUser());

        }
    }

}
