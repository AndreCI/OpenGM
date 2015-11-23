package ch.epfl.sweng.opengm.messages;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;

import static ch.epfl.sweng.opengm.events.Utils.stringToDate;
import static ch.epfl.sweng.opengm.messages.Utils.writeMessageLocal;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowMessagesActivity extends AppCompatActivity {
    private ConversationInformation conversationInformation;
    private CustomAdapter customAdapter;
    private List<MessageAdapter> messages;
    private EditText textBar;
    private String path;

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
        path = new File(getFilesDir(), conversationInformation.getConversationName()).getAbsolutePath();
        ListView listView = (ListView) findViewById(R.id.message_list);
        listView.setAdapter(customAdapter);
    }

    private void sendMessage() {
        EditText editText = (EditText) findViewById(R.id.message_text_bar);
        String message = editText.getText().toString();
        MessageAdapter messageAdapter = new MessageAdapter(OpenGMApplication.getCurrentUser().getId(), Utils.getNewDate(), message);
        writeMessageLocal(conversationInformation.getConversationName()+".txt", messageAdapter, this);
        editText.setText("");
        /* TODO: get back text from textBar + send it.
         * do it in back ground on the serv and localy while instantly adding it to the layout
         */

    }

    private void fillMessages() {
        ListView listView = (ListView) findViewById(R.id.message_list);
        try {
            listView.setAdapter((new DisplayMessages().execute(conversationInformation.getFilePath())).get());
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
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

    class DisplayMessages extends AsyncTask<String, Void, CustomAdapter> {

        @Override
        protected CustomAdapter doInBackground(String... params) {
            List<String> strings = null;
            try {
                strings = Utils.readTextFile(params[0]);
            } catch (IOException e) {
                Log.v("ShowMessageActivity", "couldn't read file "+params[0]);
            }
            messages.clear();
            for(String s : strings) {
                String[] data = Utils.extractMessage(s);
                messages.add(new MessageAdapter(data[0], stringToDate(data[1]), data[2]));
            }
            return new CustomAdapter(ShowMessagesActivity.this, R.layout.message_info, messages);
        }

        @Override
        protected void onPostExecute(CustomAdapter res) {
            ListView listView = (ListView) findViewById(R.id.message_list);
            listView.setAdapter(res);
        }
    }

    class UploadMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
           /* TODO: get back text from textBar + send it.
            * do it in back ground on the serv and localy while instantly adding it to the layout
            */
            return null;
        }
    }

    class FetchMessages extends AsyncTask<ConversationInformation, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ConversationInformation... params) {
           /* TODO: get File on serv or local device + read and parse it for messages and fill messages
            * idea : get serv file in background while displaying local one, then compare, then if modification, do them
            */
            return null;
        }
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