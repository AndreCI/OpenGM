package ch.epfl.sweng.opengm.polls;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.DatePickerFragment;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFPoll;
import ch.epfl.sweng.opengm.polls.participants.ListParticpantActivity;

import static ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE;
import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;

public final class CreatePollActivity extends AppCompatActivity {

    private EditText mNameEdit;
    private EditText mDescriptionEdit;
    private ListView mAnswerList;

    private Button mDeadlineButton;

    private PollAnswerAdapter mAdapter;

    private int possibleAnswers = 0;
    private Date deadline;

    private final List<String> answers = new ArrayList<>();
    private final List<String> participants = new ArrayList<>();

    private TextView nOfAnswersText;

    private PFGroup currentGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currentGroup = getIntent().getParcelableExtra(GROUP_INTENT_MESSAGE);

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

        onTapOutsideBehaviour(findViewById(R.id.createPoll_outmostLayout), this);
        nOfAnswersText.setText(String.format("%d", possibleAnswers));
    }

    public void addParticipants(View view) {
        startActivity(new Intent(this, ListParticpantActivity.class));
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
                // Intent
                String name = mNameEdit.getText().toString();
                String description = mDescriptionEdit.getText().toString();
                HashMap<String, PFMember> memberHashMap = currentGroup.getMembers();

                List<PFMember> eventsParticipants = new ArrayList<>();
                for (String memberId : participants)
                    eventsParticipants.add(memberHashMap.get(memberId));

                try {
                    PFPoll.createNewPoll(currentGroup, name, description, possibleAnswers, answers, deadline, eventsParticipants);
                } catch (PFException e) {
                    Toast.makeText(this, "Error while creating your poll", Toast.LENGTH_LONG).show();
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        int year = Integer.parseInt(dateString[2]);
        int month = Integer.parseInt(dateString[1]) - 1;
        int day = Integer.parseInt(dateString[0]);
        return new Date(year, month, day);
    }

    public final static class PollTimePickerFragment extends DatePickerFragment {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String date = String.format("%d/%02d/%04d", day, month + 1, year);
            ((Button) getActivity().findViewById(R.id.deadlineButton)).setText(date);
        }
    }
}
