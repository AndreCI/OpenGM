package ch.epfl.sweng.opengm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.opengm.parse.PFUser;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;

public class EditUserProfileActivity extends AppCompatActivity {

    private ImageView mPhotoImageView;
    private TextView mFirstLastNameTextView;
    private TextView mUsernameTextView;
    private TextView mEmailTextView;
    private TextView mPhoneNumberTextView;
    private TextView mDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_edit_layout);

        PFUser currentUser = getCurrentUser();

        // Display profile picture of user :
        mPhotoImageView = (ImageView) findViewById(R.id.userPhoto);
        mPhotoImageView.setImageResource(R.drawable.lolcat);

        // Display first name and last name of user :
        mFirstLastNameTextView = (TextView) findViewById(R.id.nameTV);
        String firstAndLastName = currentUser.getFirstName() + "\n" + currentUser.getLastName();
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
        mDescriptionTextView.setText(currentUser.getAboutUser());


    }
}
