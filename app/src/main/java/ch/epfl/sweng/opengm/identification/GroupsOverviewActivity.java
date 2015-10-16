package ch.epfl.sweng.opengm.identification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

public class GroupsOverviewActivity extends AppCompatActivity {

    private final static String LOGIN_KEY = "ch.epfl.sweng.opengm.identification.groupsoverviewactivity.login";

    private PFGroup[] groups;
    private boolean comesFromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_overview);

        Intent intent = getIntent();
        comesFromLogin = intent.getBooleanExtra(LOGIN_KEY, true);

        int nOfRows = (groups.length + 1) / 2;
        GridLayout layout = (GridLayout) findViewById(R.id.gridLayoutGroups);
        layout.setRowCount(nOfRows);
    }

}
