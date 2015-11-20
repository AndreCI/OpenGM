package ch.epfl.sweng.opengm.messages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.events.*;
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
        intent.putExtra(Utils.CONVERSATION_INFO_INTENT_MESSAGE, new ConversationInformation("", "", ""));
        setResult(ShowConversationsActivity.NEW_CONVERSATION_REQUEST_CODE, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        sendBackResult();
    }
}
