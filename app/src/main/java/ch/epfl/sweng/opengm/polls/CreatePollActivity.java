package ch.epfl.sweng.opengm.polls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.DatePickerFragment;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFPoll;
import ch.epfl.sweng.opengm.polls.participants.ListParticipantActivity;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;
import static java.lang.Integer.parseInt;

public final class CreatePollActivity extends AppCompatActivity {

    public final static String PARTICIPANTS_KEY = "ch.epfl.sweng.opengm.polls.createpollactivity.participants";

    public final static String ENROLLED_POLL_INTENT = "ch.epfl.sweng.opengm.polls.createpollactivity.enrolled";

    private static final int PARTICIPANTS_ACT_KEY = 328;

    private EditText mNameEdit;
    private EditText mDescriptionEdit;

    private Button mDeadlineButton;
    private Button mParticipantsButton;

    private PollAnswerAdapter mAdapter;

    private int possibleAnswers = 0;
    private Date deadline;

    private final List<String> answers = new ArrayList<>();

    private List<PFMember> participants = new ArrayList<>();

    private TextView nOfAnswersText;

    private PFGroup currentGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currentGroup = OpenGMApplication.getCurrentGroup();

        nOfAnswersText = (TextView) findViewById(R.id.nOfAnswers_textView);
        mNameEdit = (EditText) findViewById(R.id.namePollEditText);
        mDescriptionEdit = (EditText) findViewById(R.id.descriptionPollEditText);
        ListView mAnswerList = (ListView) findViewById(R.id.answersPollListView);
        mDeadlineButton = (Button) findViewById(R.id.deadlineButton);
        mParticipantsButton = (Button) findViewById(R.id.participantsButton);


        mAdapter = new PollAnswerAdapter(this, answers);
        mAnswerList.setAdapter(mAdapter);

        mAnswerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                answers.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        onTapOutsideBehaviour(findViewById(R.id.createPoll_outmostLayout), this);
        nOfAnswersText.setText(String.format("%d", possibleAnswers));

    }

    public void addParticipants(View view) {
        Intent i = new Intent(this, ListParticipantActivity.class);
        i.putParcelableArrayListExtra(ENROLLED_POLL_INTENT, new ArrayList<Parcelable>(participants));
        startActivityForResult(i, PARTICIPANTS_ACT_KEY);
    }

    public void setDeadline(View view) {
        DialogFragment dialogFragment = new PollTimePickerFragment();
        dialogFragment.show(getFragmentManager(), deadline != null ? dateToString(deadline) : "");
    }

    public void addAnswer(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.fragment_answer_input, null);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_valid_generic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_validate:
                deadline = getDateFromText();
                if (TextUtils.isEmpty(mNameEdit.getText())) {
                    mNameEdit.setError(getString(R.string.empty_name_poll));
                    mNameEdit.requestFocus();
                } else if (TextUtils.isEmpty(mDescriptionEdit.getText())) {
                    mDescriptionEdit.setError(getString(R.string.empty_description_poll));
                    mDescriptionEdit.requestFocus();
                } else if (answers.size() < 2) {
                    Toast.makeText(getBaseContext(), getString(R.string.less_two_answers_poll), Toast.LENGTH_LONG).show();
                } else if (deadline == null) {
                    mDeadlineButton.setError(getString(R.string.empty_date_poll));
                } else if (participants.isEmpty()) {
                    mParticipantsButton.setError(getString(R.string.no_participants_poll));
                } else if (possibleAnswers == 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.no_answer_poll), Toast.LENGTH_LONG).show();
                } else {
                    // Intent
                    String name = mNameEdit.getText().toString();
                    String description = mDescriptionEdit.getText().toString();
                    try {
                        PFPoll.createNewPoll(currentGroup, name, description, possibleAnswers, answers, deadline, participants);
                    } catch (PFException e) {
                        Toast.makeText(this, getString(R.string.error_poll), Toast.LENGTH_LONG).show();
                    }
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PARTICIPANTS_ACT_KEY) {
            if (resultCode == Activity.RESULT_OK) {
                participants = new ArrayList<>(data.<PFMember>getParcelableArrayListExtra(PARTICIPANTS_KEY));
                mParticipantsButton.setText(String.format(getString(R.string.participant_poll), participants.size()).concat(participants.size() > 1 ? "s" : ""));
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private Date getDateFromText() {
        String[] dateString = mDeadlineButton.getText().toString().split("/");
        if (dateString.length != 3) {
            return null;
        }
        int year = parseInt(dateString[2]);
        int month = parseInt(dateString[1]) - 1;
        int day = parseInt(dateString[0]);
        return new Date(year, month, day);
    }

    public final static class PollTimePickerFragment extends DatePickerFragment {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = String.format(Locale.getDefault(),"%d/%02d/%04d", day, month + 1, year);
            ((Button) getActivity().findViewById(R.id.deadlineButton)).setText(date);
        }
    }
}
