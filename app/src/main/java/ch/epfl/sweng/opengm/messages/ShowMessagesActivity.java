package ch.epfl.sweng.opengm.messages;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

import com.parse.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFMessage;

import static ch.epfl.sweng.opengm.messages.Utils.getTimestamp;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowMessagesActivity extends AppCompatActivity {
    private static String INTENT_CONVERSATION_NAME = "ch.epfl.sweng.opengm.intent_conv_name";
    private static String INTENT_CONVERSATION_LAST_REFRESH = "ch.epfl.sweng.opengm.intent_conv_last_refresh";
    private static String BROADCAST_ACTION = "ch.epfl.sweng.opengm.broadcast_action";
    private static final String EXTENDED_DATA_STATUS = "ch.epfl.sweng.opengm.status";
    private String conversation;
    private ListView messageList;
    private List<MessageAdapter> messages;
    private EditText textBar;
    private CustomAdapter adapter;
    private Intent mServiceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);
        messages = new ArrayList<>();
        Intent intent = getIntent();
        conversation = intent.getStringExtra(Utils.FILE_INFO_INTENT_MESSAGE);
        Log.v("ShowMessages", conversation);
        new DisplayMessages().execute(conversation, "0");
        messageList = (ListView) findViewById(R.id.message_list);
        adapter = new CustomAdapter(this, R.id.message_list);
        messageList.setAdapter(adapter);
        textBar = (EditText) findViewById(R.id.message_text_bar);

        mServiceIntent = new Intent(this, RefreshMessages.class);
        mServiceIntent.putExtra(INTENT_CONVERSATION_NAME, conversation);
        mServiceIntent.putExtra(INTENT_CONVERSATION_LAST_REFRESH, getTimestamp());
        startService(mServiceIntent);

        ResponseReceiver mResponseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver, new IntentFilter(BROADCAST_ACTION));

    }

    private void sendMessage(String message) {
        textBar.setText("");
        if (!message.isEmpty()) {
            new SendMessage().execute(message);
        }
    }

    public void clickOnSendButton(View view) {
        String message = textBar.getText().toString();
        sendMessage(message);
        /*MessageAdapter messageAdapter = new MessageAdapter(OpenGMApplication.getCurrentUser().getFirstName(), getTimestamp(), message);
        messages.add(messageAdapter);
        messageList.smoothScrollToPosition(messages.size() - 1);*/
    }

    class SendMessage extends AsyncTask<String, Void, MessageAdapter> {

        @Override
        protected MessageAdapter doInBackground(String... params) {
            try {
                PFMessage.writeMessage(conversation, OpenGMApplication.getCurrentGroup().getId(), OpenGMApplication.getCurrentUser().getUsername(), params[0]);
            } catch (IOException|ParseException|PFException e) {
                e.printStackTrace();
            }
            Log.v("ShowMessage sendMessage", params[0]);
            return null;
        }
    }

    class DisplayMessages extends AsyncTask<String, Void, List<MessageAdapter>> {

        @Override
        protected List<MessageAdapter> doInBackground(String... params) {
            List<MessageAdapter> messageAdapters = new ArrayList<>();
            for (PFMessage message : Utils.getMessagesForConversationName(params[0], Long.valueOf(params[1]))) {
                messageAdapters.add(new MessageAdapter(message.getSender(), getTimestamp(), message.getBody()));
            }
            return messageAdapters;
        }

        @Override
        protected void onPostExecute(List<MessageAdapter> messages) {
            ShowMessagesActivity.this.messages.clear();
            ShowMessagesActivity.this.messages.addAll(messages);
            adapter.notifyDataSetChanged();
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

    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> messagesFragmented = intent.getStringArrayListExtra(EXTENDED_DATA_STATUS);
            if(messagesFragmented.size() > 0) {
                ArrayList<MessageAdapter> messages = new ArrayList<>();
                for (int i = 0; messagesFragmented.size() > 0 && messagesFragmented.size() % 3 == 0 && i < messagesFragmented.size() - 2; i += 3) {
                    if(Long.valueOf(messagesFragmented.get(i + 1)) > ShowMessagesActivity.this.messages.get(ShowMessagesActivity.this.messages.size() - 1).getSendDate()) {
                        Log.v("ResponseReceiver", messagesFragmented.get(i) + " - " + messagesFragmented.get(i + 1) + " - " + messagesFragmented.get(i + 2));
                        messages.add(new MessageAdapter(messagesFragmented.get(i), Long.valueOf(messagesFragmented.get(i + 1)), messagesFragmented.get(i + 2)));
                    }
                }
                ShowMessagesActivity.this.messages.addAll(messages);
                messageList.smoothScrollToPosition(ShowMessagesActivity.this.messages.size() - 1);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public static class RefreshMessages extends IntentService {
        public RefreshMessages() {
            super("RefreshMessagesService");
        }
        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param name Used to name the worker thread, important only for debugging.
         */
        public RefreshMessages(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String conversationName = intent.getStringExtra(INTENT_CONVERSATION_NAME);
            long lastRefresh = intent.getLongExtra(INTENT_CONVERSATION_LAST_REFRESH, 0);
            while (true) {
                List<PFMessage> messages = Utils.getMessagesForConversationName(conversationName, lastRefresh);
                ArrayList<String> result = new ArrayList<>();
                for (PFMessage message : messages) {
                    result.add(message.getSender());
                    result.add(message.getTimestamp().toString());
                    result.add(message.getBody());
                    lastRefresh = message.getTimestamp()+1;
                }
                Log.v("RefreshMessages", "messages size: " + messages.size());
                if(messages.size() > 0){
                    Intent localIntent = new Intent(BROADCAST_ACTION).putStringArrayListExtra(EXTENDED_DATA_STATUS, result);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}