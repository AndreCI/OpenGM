package ch.epfl.sweng.opengm.messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.File;

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
    }


    public void clickOnSaveButton(View view) {
        sendBackResult();
    }

    private void sendBackResult() {
        Intent intent = new Intent();
        String conversationName = ((EditText) findViewById(R.id.newConversationName)).getText().toString();
        File file = new File(getFilesDir(), conversationName + ".txt");
        intent.putExtra(Utils.CONVERSATION_INFO_INTENT_MESSAGE, new ConversationInformation(conversationName, currentGroup.getId(), file.getAbsolutePath()));
        setResult(ShowConversationsActivity.NEW_CONVERSATION_REQUEST_CODE, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        sendBackResult();
    }
}
