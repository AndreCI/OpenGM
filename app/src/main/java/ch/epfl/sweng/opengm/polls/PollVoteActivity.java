package ch.epfl.sweng.opengm.polls;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFPoll;

import static ch.epfl.sweng.opengm.parse.PFPoll.Answer;

public class PollVoteActivity extends AppCompatActivity {

    private PFPoll mPoll;

    private LinearLayout srollViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_vote);

        setTitle("Reply to this poll");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPoll = getIntent().getParcelableExtra(CreatePollActivity.POLL_INTENT);

        srollViewLayout = (LinearLayout) findViewById(R.id.container_scrollView_answers);

        TextView mName = (TextView) findViewById(R.id.nameVotePollText);
        mName.setText(mPoll.getName());

        TextView mDescription = (TextView) findViewById(R.id.descriptionVotePollText);
        mDescription.setText(mPoll.getDescription());

        TextView mPossibleAnswers = (TextView) findViewById(R.id.possiblePollVoteText);
        mPossibleAnswers.setText(mPossibleAnswers.getText().toString().concat(" " + mPoll.getNOfAnswers()));

        fillWithAnswers(mPoll.getNOfAnswers() != 1);

    }


    private void fillWithAnswers(boolean areCheckbox) {
        if (areCheckbox) {
            for (Answer answer : mPoll.getAnswers()) {
                CheckBox button = new CheckBox(this);
                button.setText(answer.getAnswer());
                button.setTextColor(getResources().getColor(R.color.bluegreen));
                button.setTag(answer);
                srollViewLayout.addView(button);
            }
        } else {
            RadioGroup rg = new RadioGroup(getBaseContext());
            for (Answer answer : mPoll.getAnswers()) {
                RadioButton button = new RadioButton(this);
                button.setText(answer.getAnswer());
                button.setTextColor(getResources().getColor(R.color.bluegreen));
                button.setTag(answer);
                rg.addView(button);
            }
            srollViewLayout.addView(rg);
        }

    }

    public void validateVote(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }
}
