package ch.epfl.sweng.opengm.messages;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFMessage;
import ch.epfl.sweng.opengm.userProfile.MemberProfileActivity;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentGroup;
import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;
import static ch.epfl.sweng.opengm.messages.Utils.getDateFromTimestamp;

public class ShowMessagesActivity extends AppCompatActivity {
    private static String INTENT_CONVERSATION_NAME = "ch.epfl.sweng.opengm.intent_conv_name";
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

        setTitle(conversation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Log.v("ShowMessages", conversation);
        new DisplayMessages().execute(conversation);
        messageList = (ListView) findViewById(R.id.message_list);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageAdapter ma = adapter.getItem(position);
                startActivity(new Intent(ShowMessagesActivity.this, MemberProfileActivity.class).
                        putExtra(MemberProfileActivity.MEMBER_KEY, ma.getSenderId()));
            }
        });

        adapter = new CustomAdapter(this, R.id.message_list);
        messageList.setAdapter(adapter);
        textBar = (EditText) findViewById(R.id.message_text_bar);

        mServiceIntent = new Intent(this, RefreshMessages.class);
        mServiceIntent.putExtra(INTENT_CONVERSATION_NAME, conversation);
        startService(mServiceIntent);

        ResponseReceiver mResponseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver, new IntentFilter(BROADCAST_ACTION));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return true;
        }
    }

    private void sendMessage(String message) {
        new SendMessage().execute(message);
    }

    public void clickOnSendButton(View view) {
        String message = textBar.getText().toString();
        if (!message.isEmpty()) {
            textBar.setText("");
            sendMessage(message);
            MessageAdapter messageAdapter = new MessageAdapter(getCurrentUser().getId(),
                    Long.toString(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis()), message);
            messages.add(messageAdapter);
            adapter.notifyDataSetChanged();
            messageList.smoothScrollToPosition(messages.size() - 1);
        }
    }

    class SendMessage extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String message = params[0];
            try {
                PFMessage.writeMessage(conversation, getCurrentGroup().getId(), getCurrentUser().getId(), message);
                return true;
            } catch (IOException | ParseException | PFException e) {
                Toast.makeText(getBaseContext(), "Error, your message was not sent", Toast.LENGTH_LONG).show();
                return false;
            }
        }

    }

    class DisplayMessages extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            for (PFMessage message : Utils.getMessagesForConversationName(params[0])) {
                messages.add(new MessageAdapter(message.getSenderId(), message.getTimestamp().toString(), message.getBody()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            return null;
        }

    }

    private class CustomAdapter extends ArrayAdapter<MessageAdapter> {

        public CustomAdapter(Context context, int resource) {
            super(context, resource, messages);

        }

        private class ViewHolder {
            ImageView image;
            TextView date;
            TextView sender;
            TextView message;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageAdapter messageAdapter = messages.get(position);
            boolean isSender = messageAdapter.getSenderId().equals(getCurrentUser().getId());

            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (isSender) {
                    convertView = vi.inflate(R.layout.chat_item_sent, null);
                } else {
                    convertView = vi.inflate(R.layout.chat_item_rcv, null);
                }
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.message_sender_image);
                holder.sender = (TextView) convertView.findViewById(R.id.message_sender_name);
                holder.message = (TextView) convertView.findViewById(R.id.message_body);
                holder.date = (TextView) convertView.findViewById(R.id.message_sending_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PFMember member = getCurrentGroup().getMember(messageAdapter.getSenderId());
            if (member != null) {
                if (member.getPicture() != null) {
                    holder.image.setBackground(null);
                    holder.image.setImageBitmap(member.getPicture());
                }
                if (isSender) {
                    holder.sender.setText(String.format("(%s %s) %s", member.getFirstName(), member.getLastName(), member.getNickname()));
                } else {
                    holder.sender.setText(String.format("%s (%s %s)", member.getNickname(), member.getFirstName(), member.getLastName()));
                }
            }
            holder.message.setText(messageAdapter.getMessage());
            holder.date.setText(getDateFromTimestamp(messageAdapter.getSendDate()));
            return convertView;
        }
    }

    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> messagesFragmented = intent.getStringArrayListExtra(EXTENDED_DATA_STATUS);
            if (messagesFragmented.size() / 3 > messages.size()) {
                ArrayList<MessageAdapter> messages = new ArrayList<>();
                for (int i = 0; i < messagesFragmented.size() - 2; i += 3) {
                    Log.v("ResponseReceiver", messagesFragmented.get(i) + " - " + messagesFragmented.get(i + 1) + " - " + messagesFragmented.get(i + 2));
                    messages.add(new MessageAdapter(messagesFragmented.get(i), messagesFragmented.get(i + 1), messagesFragmented.get(i + 2)));
                }
                ShowMessagesActivity.this.messages.clear();
                ShowMessagesActivity.this.messages.addAll(messages);
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
            while (true) {
                List<PFMessage> messages = Utils.getMessagesForConversationName(intent.getStringExtra(INTENT_CONVERSATION_NAME));
                ArrayList<String> result = new ArrayList<>();
                for (PFMessage message : messages) {
                    result.add(message.getSenderId());
                    result.add(message.getTimestamp().toString());
                    result.add(message.getBody());
                }
                Log.v("RefreshMessages", "messages size: " + messages.size());
                Intent localIntent = new Intent(BROADCAST_ACTION).putStringArrayListExtra(EXTENDED_DATA_STATUS, result);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        }
    }

}