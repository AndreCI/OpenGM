package ch.epfl.sweng.opengm.identification;

import android.content.Intent;
import android.graphics.Point;
import android.net.LinkAddress;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

import ch.epfl.sweng.opengm.R;

public class GroupsOverviewActivity extends AppCompatActivity {

    private static final int TILES_PER_WIDTH = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_overview);

        // Retrieve and Display user information :
//        ParseUser usr;
//        if ((usr = ParseUser.getCurrentUser()) != null) {
//            TextView username = (TextView) findViewById(R.id.username);
//            username.setText(usr.getUsername());
//            TextView email = (TextView) findViewById(R.id.email);
//            email.setText(usr.getEmail());
//        }

        // TODO: At the moment, just an array of Strings, but normally : an array of "Groups" (the Object which encapsulate all group date)
        // real stuff : ArrayList<Group> groups = {g1, g2, ...., gn}; --> Then, g1.name, g1.members[], g1.admin, etc...
        ArrayList<String> groups = new ArrayList<>();
        groups.add("Satellite");
        groups.add("Coaching");
        groups.add("IC Travel");


        // Get screen size :
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;

        // Get base (main) layout
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        // TODO: SPECIAL CASE = AlertView "no groups yet ???" --> Pop-up help ??? --> groups.size() = 0
        // TODO: put all of this inside a scrollView



        int numberOfLinearLayouts = (int) Math.ceil((double)(groups.size()+1) / (double)TILES_PER_WIDTH);   // +1 pour la tile "+" add a group // TODO: image pour la tuile +

        for (int i=0; i<numberOfLinearLayouts; i++) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setLayoutParams(llParams);
//            ll.setTag(new String("tileLayout" + i));  // setId() instead ???

            for (int j=0; j<TILES_PER_WIDTH; j++) { // loop 2 times
                if (groups.size() != 0) {
                    Button tile = new Button(this);
                    tile.setLayoutParams(llParams);
                    tile.setText(groups.get(0));    // Always get elem 0, as elements get shifted...
                    tile.setWidth(screenWidth / 2);
                    tile.setHeight(screenWidth / 2);
                    tile.setGravity(Gravity.CENTER | Gravity.BOTTOM);

                    // TODO: specify WHERE does the button go ? On this particular group homescreen
                    tile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(GroupsOverviewActivity.this, GroupHomeActivity.class));
                        }
                    });

                    ll.addView(tile);
                    groups.remove(0);   // shift the elements to the left
                } else {
                    Button tile = new Button(this);
                    tile.setLayoutParams(llParams);
                    tile.setText("+");
                    tile.setWidth(screenWidth / 2);
                    tile.setHeight(screenWidth / 2);
                    tile.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

                    tile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: petit pop-up qui part du bouton avec either "Create a group" ou "Join an existing group"
                            Log.v("INFO", "create a group / join an existing group");
                        }
                    });

                    ll.addView(tile);
                    break;
                }
            }

            mainLayout.addView(ll);
        }
        // will generate Math.ceil(3/2) = 2 linear layouts : "tileLayout0" and "tileLayout1", in our case.


    }
}
