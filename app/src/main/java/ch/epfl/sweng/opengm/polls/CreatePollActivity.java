package ch.epfl.sweng.opengm.polls;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class CreatePollActivity extends AppCompatActivity {

    private EditText mNameEdit;
    private EditText mDescriptionEdit;
    private ListView mAnswerList;

    private PollAnswerAdapter mAdapter;

    private int possibleAnswers = 0;
    private final List<String> answers = new ArrayList<>();

    private TextView nOfAnswersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        nOfAnswersText = (TextView) findViewById(R.id.nOfAnswers_textView);
        mNameEdit = (EditText) findViewById(R.id.namePollEditText);
        mDescriptionEdit = (EditText) findViewById(R.id.descriptionPollEditText);
        mAnswerList = (ListView) findViewById(R.id.answersPollListView);

        mAdapter = new PollAnswerAdapter(this, R.layout.item_answer_poll, answers);
        mAnswerList.setAdapter(mAdapter);

        mAnswerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                answers.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });


        nOfAnswersText.setText(String.format("%d", possibleAnswers));
    }

    public void addParticipants(View view) {

    }

    public void setDeadline(View view) {

    }

    public void addAnswer(View view) {
        answers.add(answers.size() + "");
        mAdapter.notifyDataSetChanged();
    }

    public void increasePossibleAnswers(View view) {
        if (possibleAnswers < answers.size()) {
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
