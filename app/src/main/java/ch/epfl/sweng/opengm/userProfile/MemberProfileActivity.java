package ch.epfl.sweng.opengm.userProfile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFMember;


public class MemberProfileActivity extends AppCompatActivity {

    public static final String MEMBER_KEY = "ch.epfl.sweng.opengm.memberprofileactivity.key";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        PFMember currentMember = getIntent().getParcelableExtra(MEMBER_KEY);

        if (currentMember != null) {

            setTitle(currentMember.getUsername() + " Profile");

            // Display profile picture of user :
            ImageView photoImageView = (ImageView) findViewById(R.id.userPhoto);
            photoImageView.setImageResource(R.drawable.avatar_male1);

            // Display first name of user :
            TextView firstNameTextView = (TextView) findViewById(R.id.firstNameTV);
            firstNameTextView.setText(currentMember.getFirstName());

            // Display last name of user :
            TextView lastNameTextView = (TextView) findViewById(R.id.lastNameTV);
            lastNameTextView.setText(currentMember.getLastName());

            // Display username of user :
            TextView usernameTextView = (TextView) findViewById(R.id.usernameTV);
            usernameTextView.setText(currentMember.getUsername());

            // Display e-mail address of user :
            TextView emailTextView = (TextView) findViewById(R.id.emailTV);
            emailTextView.setText(currentMember.getEmail());

            // Display phone number of user :
            TextView phoneNumberTextView = (TextView) findViewById(R.id.phoneTV);
            phoneNumberTextView.setText(currentMember.getPhoneNumber());

            // Display description of user :
            TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTV);
            descriptionTextView.setText(currentMember.getAbout());

        }
    }


}
