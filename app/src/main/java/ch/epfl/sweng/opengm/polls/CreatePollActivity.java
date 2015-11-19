package ch.epfl.sweng.opengm.polls;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.DatePickerFragment;
import ch.epfl.sweng.opengm.utils.Utils;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;

public class CreatePollActivity extends AppCompatActivity {

    private EditText mNameEdit;
    private EditText mDescriptionEdit;
    private ListView mAnswerList;

    private Button mDeadlineButton;

    private PollAnswerAdapter mAdapter;

    private int possibleAnswers = 0;
    private Date deadline;

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
        mDeadlineButton = (Button) findViewById(R.id.deadlineButton);

        mAdapter = new PollAnswerAdapter(this, R.layout.item_answer_poll, answers);
        mAnswerList.setAdapter(mAdapter);

        mAnswerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                answers.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        Utils.onTapOutsideBehaviour(findViewById(R.id.createPoll_outmostLayout), this);
        nOfAnswersText.setText(String.format("%d", possibleAnswers));
    }

    public void addParticipants(View view) {

    }

    public void setDeadline(View view) {
        DialogFragment dialogFragment = new PollTimePickerFragment();
        deadline = getDateFromText();
        dialogFragment.show(getFragmentManager(), deadline != null ? dateToString(deadline) : "");
    }

    public void addAnswer(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_answer_input, null);

        builder.setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText input = (EditText) view.findViewById(R.id.answer_editText);
                        answers.add(input.getText().toString());
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel_dialog_password, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing just hide this dialog
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
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

    private Date getDateFromText() {
        String[] dateString = mDeadlineButton.getText().toString().split("/");
        if (dateString.length != 3) {
            return null;
        }
        int year = Integer.parseInt(dateString[2]);
        int month = Integer.parseInt(dateString[1]) - 1;
        int day = Integer.parseInt(dateString[0]);
        return new Date(year, month, day);
    }

    public static class PollTimePickerFragment extends DatePickerFragment {

        public PollTimePickerFragment(){

        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = String.format("%d/%02d/%04d", day, month + 1, year);
            ((Button) getActivity().findViewById(R.id.deadlineButton)).setText(date);
        }
    }
}
