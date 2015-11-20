package ch.epfl.sweng.opengm.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowMessagesActivity extends AppCompatActivity {
    private ConversationInformation conversationInformation;
    private CustomAdapter customAdapter;
    private List<MessageAdapter> messages;
    private EditText textBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);
        Intent intent = getIntent();
        conversationInformation = intent.getParcelableExtra(Utils.FILE_INFO_INTENT_MESSAGE);
        messages = new ArrayList<>();
        fillMessages();
        customAdapter = new CustomAdapter(this, R.layout.message_info, messages);
        textBar = (EditText) findViewById(R.id.message_text_bar);

        textBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void sendMessage() {
        //TODO: get back text from textBar + send it.
    }

    private void fillMessages() {
        //TODO: get File on serv or local device + read and parse it for messages and fill messages
    }

    public void clickOnSendButton(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        sendMessage();
    }


    private class CustomAdapter extends ArrayAdapter<MessageAdapter> {
        private List<MessageAdapter> messages;

        public CustomAdapter(Context context, int resource, List<MessageAdapter> messages) {
            super(context, resource, messages);
            this.messages = new ArrayList<>();
            this.messages.addAll(messages);
        }

        private class ViewHolder {
            TextView sender;
            TextView message;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.message_info, null);
                holder = new ViewHolder();
                holder.sender = (TextView) convertView.findViewById(R.id.message_sender_name);
                holder.sender = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MessageAdapter messageAdapter = messages.get(position);
            holder.sender.setText(messageAdapter.getSenderName());
            holder.message.setTag(messageAdapter.getMessage());

            return convertView;
        }
    }
}
