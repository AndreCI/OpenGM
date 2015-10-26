package ch.epfl.sweng.opengm.identification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;

import ch.epfl.sweng.opengm.groups.CreateGroup;
import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardGridView;

public class GroupsOverviewActivity extends AppCompatActivity {

     public static final String COMING_FROM_KEY = "ch.epfl.ch.opengm.connexion.signup.groupsActivity.coming";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_overview);

        OpenGMApplication.setCurrentUser(ParseUser.getCurrentUser());

         boolean newUser = getIntent().getBooleanExtra(COMING_FROM_KEY, false);
//         if newUser is true => user is new (register) so show the hints
//         Not a good idea : Alex prefers if (groups.size() == 0) --> Show hints ! "No groups Yet ? Click + to add one..."
        Log.d("USER", OpenGMApplication.getCurrentUser().toString());

        ArrayList<PFGroup> groups = new ArrayList<>(OpenGMApplication.getCurrentUser().getGroups());
//        ArrayList<String> groups = new ArrayList<>(Arrays.asList("Satellite", "IC Travel", "Ya d'la joa", "Facebook", "EPFL"));
//        ArrayList<String> admins = new ArrayList<>(Arrays.asList("Un monsieur", "Lionel Fleury", "An extra long name that cannot possibly enter in the field we provided", "Mark Zuckerberg", "Patou"));


        ArrayList<Card> cards = new ArrayList<>();

        if (groups.size() == 0) {

            Log.v("INFO", "No groups yet ? Click + to add one");

        } else {

            for (int i = 0; i < groups.size(); i++) {
                Card card = new Card(this);
                CardHeader cardHeader = new CardHeader(this);
                cardHeader.setTitle(groups.get(i).getName());
                card.setTitle("Members : " + groups.get(i).getMembers().toString());
                card.addCardHeader(cardHeader);
                card.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {
                        Log.v("INFO", "clicked on card : " + card.getCardHeader().getTitle() + " !!!");
                    }
                });
                cards.add(card);
            }

        }

        Card addGroupCard = new Card(this);
        CardHeader addGroupHeader = new CardHeader(this);
        addGroupHeader.setTitle("+");   // Instead of putting in a tile, draw a round button with a "plus" sign
        addGroupCard.addCardHeader(addGroupHeader);
        addGroupCard.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Log.v("INFO", "Going to add group activity");
                startActivity(new Intent(GroupsOverviewActivity.this, CreateGroup.class));
            }
        });
        cards.add(addGroupCard);


        CardGridView gridView = (CardGridView) findViewById(R.id.myGroupsGrid);
        gridView.setAdapter(new CardArrayAdapter(this, cards));

    }
}
