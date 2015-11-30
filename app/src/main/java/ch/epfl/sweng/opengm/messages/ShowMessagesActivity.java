package ch.epfl.sweng.opengm.messages;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFConversation;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowMessagesActivity extends AppCompatActivity {
    private PFConversation conversation;
    private ListView messageList;
    private List<MessageAdapter> messages;
    private EditText textBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);
        messages = new ArrayList<>();
        Intent intent = getIntent();
        conversation = intent.getParcelableExtra(Utils.FILE_INFO_INTENT_MESSAGE);
        messageList = (ListView) findViewById(R.id.message_list);
        textBar = (EditText) findViewById(R.id.message_text_bar);
        textBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                for(int i = 0; i < string.length(); ++i) {
                    if(string.charAt(i) == '|' || System.lineSeparator().contains(string.charAt(i)+"")) {
                        s.delete(i, i+1);
                    }
                }
            }
        });
        fillMessages();
        Log.v("ShowMessagesAct", "just after on create, conv is: " + conversation + "groupid is: " + conversation.getGroupId());
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
        EditText editText = (EditText) findViewById(R.id.message_text_bar);
        String message = editText.getText().toString();
        if(!message.isEmpty()) {
            Log.v("ShowMessage sendMessage", message);
            MessageAdapter messageAdapter = new MessageAdapter(OpenGMApplication.getCurrentUser().getFirstName(), Utils.getNewStringDate(), message);
            new SendMessage().execute(message);
            editText.setText("");
            messages.add(messageAdapter);
        }
    }

    private void fillMessages() {
        new readMessageFile().execute(String.format("%s/%s_%s.txt", getFilesDir().getAbsolutePath(), conversation.getConversationName(), conversation.getGroupId()));
        /* TODO: get File on serv or local device + read and parse it for messages and fill messages
         * idea : get serv file in background while displaying local one, then compare, then if modification, do them
         */
    }

    public void clickOnSendButton(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        sendMessage();
    }

    class readMessageFile extends AsyncTask<String, Void, CustomAdapter> {

        @Override
        protected CustomAdapter doInBackground(String... params) {
            List<String> strings = null;
            try {
                strings = Utils.readMessagesFile(params[0]);
                messages = new ArrayList<>();
                for(String s : strings) {
                    Log.v("ShowMessages readFile", s);
                    String[] data = Utils.extractMessage(s);
                    MessageAdapter messageAdapter = new MessageAdapter(data[0], data[1], data[2]);
                    messages.add(messageAdapter);
                    Log.v("ShowMessages readFile", messageAdapter.toString());
                }
            } catch (IOException e) {
                Log.v("ShowMessageActivity", "couldn't read file "+params[0]);
            }
            return new CustomAdapter(ShowMessagesActivity.this, R.layout.message_info);
        }

        @Override
        protected void onPostExecute(CustomAdapter res) {
            ListView listView = (ListView) findViewById(R.id.message_list);
            listView.setAdapter(res);
        }
    }

    class SendMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                conversation.writeMessage(OpenGMApplication.getCurrentUser().getUsername(), params[0]);
                Utils.writeMessageLocal(OpenGMApplication.getCurrentUser().getUsername(), params[0], conversation.getConversationName(), conversation.getGroupId(), ShowMessagesActivity.this);
            } catch (IOException|ParseException e) {
                Log.e("show message activity", "couldn't write message to conversation");
            }
            return null;
        }
    }

    class FetchMessages extends AsyncTask<PFConversation, Void, Boolean> {

        @Override
        protected Boolean doInBackground(PFConversation... params) {
           /* TODO: get File on serv or local device + read and parse it for messages and fill messages
            * idea : get serv file in background while displaying local one, then compare, then if modification, do them
            */
            return null;
        }
    }


    private class CustomAdapter extends ArrayAdapter<MessageAdapter> {

        public CustomAdapter(Context context, int resource) {
            super(context, resource, messages);

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
                holder.message = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MessageAdapter messageAdapter = messages.get(position);
            holder.sender.setText(messageAdapter.getSenderName());
            holder.message.setText(messageAdapter.getMessage());

            return convertView;
        }
    }
}