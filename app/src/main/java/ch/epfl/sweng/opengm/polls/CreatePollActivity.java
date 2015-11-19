package ch.epfl.sweng.opengm.polls;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class CreatePollActivity extends AppCompatActivity {

    private int possibleAnswers = 0;
    private final List<String> answers = new ArrayList<>();

    private TextView nOfAnswersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        nOfAnswersText = (TextView) findViewById(R.id.nOfAnswers_textView);
        nOfAnswersText.setText(String.format("%d", possibleAnswers));
    }

    public void increasePossibleAnswers(View view) {
        if (possibleAnswers < answers.size() + 1) {
            possibleAnswers++;
            nOfAnswersText.setText(String.format("%d", possibleAnswers));
        }
    }

    public void decreasePossibleAnswers(View view) {
        if (possibleAnswers > 1) {
            possibleAnswers--;
            nOfAnswersText.setText(String.format("%d", possibleAnswers));
        }
    }

}
