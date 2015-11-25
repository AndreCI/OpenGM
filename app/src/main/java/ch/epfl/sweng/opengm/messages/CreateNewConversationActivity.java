package ch.epfl.sweng.opengm.messages;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

public class CreateNewConversationActivity extends AppCompatActivity {
    PFGroup currentGroup;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_conversation);
        Intent intent = getIntent();
        currentGroup = intent.getParcelableExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE);

        editText = (EditText) findViewById(R.id.newConversationName);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                for(int i = 0; i < string.length(); ++i) {
                    if(string.charAt(i) == '|') {
                        s.delete(i, i+1);
                    }
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendBackResult();
                    handled = true;
                }
                return handled;
            }
        });
    }


    public void clickOnSaveButton(View view) {
        sendBackResult();
    }

    private void sendBackResult() {
        Intent intent = new Intent();
        String conversationName = ((EditText) findViewById(R.id.newConversationName)).getText().toString();
        String path = getFilesDir().getAbsolutePath() + '/' + conversationName + ".txt";
        ConversationInformation conversationInformation = new ConversationInformation(conversationName, currentGroup.getId(), path);
        new CreateFileInBackground().execute(conversationInformation);
        intent.putExtra(Utils.CONVERSATION_INFO_INTENT_MESSAGE, conversationInformation);
        setResult(Activity.RESULT_OK, intent);
        Log.v("CreateNewConversation", conversationName + ", " + path);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(!((TextView) findViewById(R.id.conversation_title)).getText().toString().isEmpty()) {
            sendBackResult();
        } else {
            super.onBackPressed();
        }
    }

    class CreateFileInBackground extends AsyncTask<ConversationInformation, Void, Void> {

        @Override
        protected Void doInBackground(ConversationInformation... params) {
            ConversationInformation conversationInformation = params[0];
            try {
                FileOutputStream fOut = openFileOutput(conversationInformation.getConversationName() + ".txt", MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(conversationInformation.toString()+'\n');
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
