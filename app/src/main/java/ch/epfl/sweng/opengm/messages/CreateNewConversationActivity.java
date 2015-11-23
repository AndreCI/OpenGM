package ch.epfl.sweng.opengm.messages;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_conversation);
        Intent intent = getIntent();
        currentGroup = intent.getParcelableExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE);

        EditText editText = (EditText) findViewById(R.id.newConversationName);
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
        new CreateFileInBackground().execute(conversationName, path);
        intent.putExtra(Utils.CONVERSATION_INFO_INTENT_MESSAGE, new ConversationInformation(conversationName, currentGroup.getId(), path));
        setResult(Activity.RESULT_OK, intent);
        Log.v("CreateNewConversation", conversationName + ", " + path);
        finish();
    }

    @Override
    public void onBackPressed() {
        sendBackResult();
    }

    class CreateFileInBackground extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                FileOutputStream fOut = openFileOutput(params[0] + ".txt", MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(params[0] + "-*-" + currentGroup.getId() + "-*-" + params[1]);
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
