package ch.epfl.sweng.opengm.groups;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ch.epfl.sweng.opengm.R;

public class Members extends AppCompatActivity {

    private ListView members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        members = (ListView) findViewById(R.id.member_list);
    }
}
