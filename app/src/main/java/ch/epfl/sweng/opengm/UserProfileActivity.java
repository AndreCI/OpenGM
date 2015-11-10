package ch.epfl.sweng.opengm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import com.parse.ParseUser;

import ch.epfl.sweng.opengm.parse.PFUser;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;


public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);

        PFUser currentUser = getCurrentUser();

        TextView nameTextView = (TextView) findViewById(R.id.firstLastNameTextView);
//        String text = getText(R.string.firstLastName);
        String firstAndLastName = "[Firstname]\n[Lastname]";
        firstAndLastName = firstAndLastName.replaceFirst("[Firstname]", currentUser.getFirstName());
        firstAndLastName = firstAndLastName.replace("[Lastname]", currentUser.getLastName());
        nameTextView.setText(firstAndLastName);

        TextView usernameTextView = (TextView) findViewById(R.id.usernameTextView);
//        String username = getText(R.string.username);
        String username = "[username]";
        username = username.replace("[username]", currentUser.getUsername());
        usernameTextView.setText(username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
