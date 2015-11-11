package ch.epfl.sweng.opengm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFUser;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;


public class UserProfileActivity extends AppCompatActivity {

    private ImageView mPhotoImageView;
    private TextView mFirstLastNameTextView;
    private TextView mUsernameTextView;
    private TextView mEmailTextView;
    private TextView mPhoneNumberTextView;
    private TextView mDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);

        PFUser currentUser = getCurrentUser();

        // TODO: tests with setters :
        // *****************************************
        try {
            currentUser.setPhoneNumber("06 45 72 12 43");
            currentUser.setAboutUser("I'm just a random person, with a random name. This is a description." +
                "I know, it's a bit long, but we'll see if evrything can fit in !");
        } catch (PFException e) {
            e.printStackTrace();
        }
        // *****************************************


        // TODO: proper names in strings.xml file. + Name of activity


        // Display profile picture of user :
        mPhotoImageView = (ImageView) findViewById(R.id.userPhoto);
        mPhotoImageView.setImageResource(R.drawable.lolcat);

        // Display first name and last name of user :
        mFirstLastNameTextView = (TextView) findViewById(R.id.nameTV);
//        String text = getText(R.string.firstLastName);
        String firstAndLastName = "[name]";
        firstAndLastName = firstAndLastName.replace(firstAndLastName, currentUser.getFirstName());
        firstAndLastName += "\n" + currentUser.getLastName();
        mFirstLastNameTextView.setText(firstAndLastName);

        // Display username of user :
        mUsernameTextView = (TextView) findViewById(R.id.usernameTV);
//        String username = getText(R.string.username);
        String username = "[username]";
        username = username.replace(username, currentUser.getUsername());
        mUsernameTextView.setText(username);

        // Display e-mail adress of user :
        mEmailTextView = (TextView) findViewById(R.id.emailTV);
//        String email = getText(R.string.email);
        String email = "[email]";
        email = email.replace(email, currentUser.getEmail());
        mEmailTextView.setText(email);

        // Display phone number of user :
        mPhoneNumberTextView = (TextView) findViewById(R.id.phoneTV);
        mPhoneNumberTextView.setText(currentUser.getPhoneNumber());

        // Display description of user :
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionTV);
//        String description = getText(R.string.description);
        String description = "[description]";
        description = description.replace(description, currentUser.getAboutUser());
        mDescriptionTextView.setText(description);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
