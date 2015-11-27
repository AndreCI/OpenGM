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

import com.parse.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFConversation;
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
        //Check that conv name doesn't already exist. Check in file !!!!!!!
        Intent intent = new Intent();
        String conversationName = ((EditText) findViewById(R.id.newConversationName)).getText().toString();
        String path = getFilesDir().getAbsolutePath() + '/' + conversationName + ".txt";
        PFConversation conversation = null;
        try {
            conversation = PFConversation.createNewConversation(conversationName, currentGroup.getId(), this);
        } catch (FileNotFoundException e) {
            Log.e("CreateNewConv", "couldn't createFile", e);
        } catch (ParseException e) {
            Log.e("CreateNewConv", "error with parse");
        }
        intent.putExtra(Utils.CONVERSATION_INFO_INTENT_MESSAGE, conversation.toConversationInformation());
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
}
