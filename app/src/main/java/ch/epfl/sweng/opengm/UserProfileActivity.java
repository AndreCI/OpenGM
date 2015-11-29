package ch.epfl.sweng.opengm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.opengm.parse.PFMember;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentGroup;


public class UserProfileActivity extends AppCompatActivity {

    private ImageView mPhotoImageView;
    private TextView mFirstLastNameTextView;
    private TextView mUsernameTextView;
    private TextView mEmailTextView;
    private TextView mPhoneNumberTextView;
    private TextView mDescriptionTextView;

    public static final String USER_ID = "ch.epfl.sweng.opengm.userprofileactivity.userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);

        Intent i = getIntent();
        String userId = i.getStringExtra(USER_ID);
        PFMember currentUser = getCurrentGroup().getMember(userId);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(currentUser.getUsername() + " profile");

        // TODO: tests with setters --> See whether it prints the real information

        // TODO: proper names in strings.xml file. + Name of activity


        // Display profile picture of user :
        mPhotoImageView = (ImageView) findViewById(R.id.userPhoto);
        mPhotoImageView.setImageResource(R.drawable.lolcat);

        // Display first name and last name of user :
        mFirstLastNameTextView = (TextView) findViewById(R.id.nameTV);
        String firstAndLastName = currentUser.getFirstname() + "\n" + currentUser.getLastname();
        mFirstLastNameTextView.setText(firstAndLastName);

        // Display username of user :
        mUsernameTextView = (TextView) findViewById(R.id.usernameTV);
        mUsernameTextView.setText(currentUser.getUsername());

        // Display e-mail adress of user :
        mEmailTextView = (TextView) findViewById(R.id.emailTV);
        mEmailTextView.setText(currentUser.getEmail());

        // Display phone number of user :
        mPhoneNumberTextView = (TextView) findViewById(R.id.phoneTV);
        mPhoneNumberTextView.setText(currentUser.getPhoneNumber());

        // Display description of user :
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionTV);
        mDescriptionTextView.setText(currentUser.getAbout());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
