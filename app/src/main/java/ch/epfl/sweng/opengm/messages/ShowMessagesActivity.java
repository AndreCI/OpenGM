package ch.epfl.sweng.opengm.messages;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFMessage;
import ch.epfl.sweng.opengm.userProfile.MemberProfileActivity;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentGroup;
import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;

public class ShowMessagesActivity extends AppCompatActivity {
    private static String BROADCAST_ACTION = "ch.epfl.sweng.opengm.broadcast_action";
    private static final String EXTENDED_DATA_STATUS = "ch.epfl.sweng.opengm.status";
    private static String conversation;
    private ListView messageList;
    private static final List<ChatMessage> messages = new ArrayList<>();
    private EditText textBar;
    private MessageAdapter adapter;
    private Intent mServiceIntent;
    private NotificationManager manager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);
        Intent intent = getIntent();
        conversation = intent.getStringExtra(Utils.FILE_INFO_INTENT_MESSAGE);

        setTitle(conversation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        new DisplayMessages().execute(conversation, "0");
        messageList = (ListView) findViewById(R.id.message_list);

        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMessage ma = adapter.getItem(position);
                startActivity(new Intent(ShowMessagesActivity.this, MemberProfileActivity.class).
                        putExtra(MemberProfileActivity.MEMBER_KEY, ma.getSenderId()));
            }
        });

        adapter = new MessageAdapter(this, R.id.message_list, messages);
        messageList.setAdapter(adapter);
        textBar = (EditText) findViewById(R.id.message_text_bar);

        mServiceIntent = new Intent(this, RefreshMessages.class);
        startService(mServiceIntent);

        ResponseReceiver mResponseReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver, new IntentFilter(BROADCAST_ACTION));

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ShowConversationsActivity.refresh = false;
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

        }
    }

    class SendMessage extends AsyncTask<String, Void, PFMessage> {

        @Override
        protected PFMessage doInBackground(String... params) {
            String message = params[0];
            try {
                return PFMessage.writeMessage(conversation, getCurrentGroup().getId(), getCurrentUser().getId(), message);
            } catch (IOException | ParseException | PFException e) {
                Toast.makeText(getBaseContext(), "Error, your message was not sent", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(PFMessage pfMessage) {
            ChatMessage chatMessage = new ChatMessage(pfMessage.getId(), getCurrentUser().getId(), new Date(), pfMessage.getBody());
            messages.add(chatMessage);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    messageList.smoothScrollToPosition(messages.size() - 1);
                }
            });
        }
    }

    class DisplayMessages extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            for (PFMessage message : Utils.getMessagesForConversationName(params[0], messages)) {
                messages.add(new ChatMessage(message.getId(), message.getSenderId(), message.getLastModified(), message.getBody()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Collections.sort(messages);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void displayNotification() {

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(ShowMessagesActivity.this)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle("New message in " + conversation)
                        .setContentText(messages.get(messages.size() - 1).getMessage());

        Intent notificationIntent = new Intent(this, ShowMessagesActivity.class);
        notificationIntent.putExtra(Utils.FILE_INFO_INTENT_MESSAGE, conversation);
        notificationIntent.putExtra(Utils.NOTIF_INTENT_MESSAGE, false);

        adapter.notifyDataSetChanged();
        PendingIntent contentIntent = PendingIntent.getActivity(ShowMessagesActivity.this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setLights(0xFF00AAAC, 500, 500);
        long[] pattern = {500, 500};
        builder.setVibrate(pattern);
        builder.setStyle(new NotificationCompat.InboxStyle());

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;

        manager.notify(1, notification);
    }

    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> messagesFragmented = intent.getStringArrayListExtra(EXTENDED_DATA_STATUS);
            if (messagesFragmented.size() > 0) {
                boolean newMessageAdded = false;
                int MESSAGE_FIELD = 4;
                ArrayList<ChatMessage> newMessages = new ArrayList<>();
                for (int i = 0; messages.size() > 0 && messagesFragmented.size() > 0 &&
                        messagesFragmented.size() % MESSAGE_FIELD == 0 &&
                        i < messagesFragmented.size() - 3; i += MESSAGE_FIELD) {

                    if (new Date(Long.parseLong(messagesFragmented.get(i + 2)))
                            .after(messages.get(messages.size() - 1).getSendDate()) &&
                            !messagesFragmented.get(i).equals(OpenGMApplication.getCurrentUser().getId())) {
                        newMessageAdded = true;
                        newMessages.add(new ChatMessage(messagesFragmented.get(i), messagesFragmented.get(i + 1), new Date(Long.parseLong(messagesFragmented.get(i + 2))), messagesFragmented.get(i + 3)));
                    }
                }
                if (newMessageAdded) {
                    messages.addAll(newMessages);
                    Collections.sort(messages);
                    messageList.smoothScrollToPosition(messages.size() - 1);
                    adapter.notifyDataSetChanged();
                    displayNotification();
                }
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
            while (ShowConversationsActivity.refresh) {
                List<PFMessage> newMessages = Utils.getMessagesForConversationName(ShowConversationsActivity.conversationToRefresh, messages);
                ArrayList<String> result = new ArrayList<>();
                for (PFMessage message : newMessages) {
                    result.add(message.getId());
                    result.add(message.getSenderId());
                    result.add(Long.toString(message.getLastModified().getTime()));
                    result.add(message.getBody());
                }
                if (newMessages.size() > 0) {
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