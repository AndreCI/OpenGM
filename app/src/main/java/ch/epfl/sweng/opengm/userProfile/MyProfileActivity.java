package ch.epfl.sweng.opengm.userProfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFUser;

public class MyProfileActivity extends AppCompatActivity {

    private final static int EDIT_KEY = 37894;

    private ImageView photoImageView;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView phoneNumberTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        photoImageView = (ImageView) findViewById(R.id.userPhoto);
        firstNameTextView = (TextView) findViewById(R.id.firstNameTV);
        lastNameTextView = (TextView) findViewById(R.id.lastNameTV);
        usernameTextView = (TextView) findViewById(R.id.usernameTV);
        emailTextView = (TextView) findViewById(R.id.emailTV);
        phoneNumberTextView = (TextView) findViewById(R.id.phoneTV);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTV);

        updateFields();
    }

    private void updateFields() {
        PFUser currentUser = OpenGMApplication.getCurrentUser();

        // Display profile picture of user :
        if (currentUser.getPicture() == null) {
            photoImageView.setImageResource(R.drawable.avatar_male);
        } else {
            photoImageView.setImageBitmap(currentUser.getPicture());
        }

        // Display first name of user :
        firstNameTextView.setText(currentUser.getFirstName());

        // Display last name of user :
        lastNameTextView.setText(currentUser.getLastName());

        // Display username of user :
        usernameTextView.setText(currentUser.getUsername());

        // Display e-mail address of user :
        emailTextView.setText(currentUser.getEmail());

        // Display phone number of user :
        phoneNumberTextView.setText(currentUser.getPhoneNumber());

        // Display description of user :
        descriptionTextView.setText(currentUser.getAboutUser());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit_user_profile:
                startActivityForResult(new Intent(MyProfileActivity.this, EditUserProfileActivity.class), EDIT_KEY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_KEY) {
            if (resultCode == Activity.RESULT_OK) {
                updateFields();
            }
        }
    }

}
