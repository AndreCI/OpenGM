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

        ImageView userPhoto = (ImageView) findViewById(R.id.userPhoto);
        userPhoto.setImageResource(R.drawable.emma);

        TextView nameTextView = (TextView) findViewById(R.id.nameTV);
//        String text = getText(R.string.firstLastName);
        String firstAndLastName = "[name]";
        firstAndLastName = firstAndLastName.replace(firstAndLastName, currentUser.getFirstName());
        firstAndLastName += "\n" + currentUser.getLastName();
        nameTextView.setText(firstAndLastName);

        TextView usernameTextView = (TextView) findViewById(R.id.usernameTV);
//        String username = getText(R.string.username);
        String username = "[username]";
        username = username.replace(username, currentUser.getUsername());
        usernameTextView.setText(username);

        TextView emailTextView = (TextView) findViewById(R.id.emailTV);
//        String email = getText(R.string.email);
        String email = "[email]";
        email = email.replace(email, currentUser.getEmail());
        emailTextView.setText(email);

        TextView phoneNumberTextView = (TextView) findViewById(R.id.phoneTV);
//        String phoneNumber = getText(R.string.phoneNumber);
        String phoneNumber = "[phone]";
        phoneNumber = phoneNumber.replace(phoneNumber, currentUser.getPhoneNumber());
        phoneNumberTextView.setText(phoneNumber);

        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTV);
//        String description = getText(R.string.description);
        String description = "[description]";
        description = description.replace(description, currentUser.getAboutUser());
        descriptionTextView.setText(description);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
