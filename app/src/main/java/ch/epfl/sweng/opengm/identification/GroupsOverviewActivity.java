package ch.epfl.sweng.opengm.identification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.parse.ParseUser;

import ch.epfl.sweng.opengm.R;

public class GroupsOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_overview);
        ParseUser usr;
        if ((usr = ParseUser.getCurrentUser()) != null) {
            TextView username = (TextView) findViewById(R.id.username);
            username.setText(usr.getUsername());
            TextView email = (TextView) findViewById(R.id.email);
            email.setText(usr.getEmail());
        }
    }
}
