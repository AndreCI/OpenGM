package ch.epfl.sweng.opengm.messages;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFMessage;
import ch.epfl.sweng.opengm.userProfile.MemberProfileActivity;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentGroup;
import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;
import static ch.epfl.sweng.opengm.messages.ShowConversationsActivity.IDS_FOR_CONV;
import static ch.epfl.sweng.opengm.messages.ShowConversationsActivity.MESSAGES_FOR_CONV;
import static ch.epfl.sweng.opengm.messages.ShowConversationsActivity.refresh;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFMessage.TABLE_ENTRY_BODY;
import static ch.epfl.sweng.opengm.parse.PFMessage.TABLE_ENTRY_NAME;
import static ch.epfl.sweng.opengm.parse.PFMessage.TABLE_ENTRY_SENDER;
import static ch.epfl.sweng.opengm.parse.PFMessage.TABLE_NAME;

public class ShowMessagesActivity extends AppCompatActivity {
    private static String conversation;
    private ListView messageList;
    private List<ChatMessage> messages;
    private List<String> messagesIds;
    private EditText textBar;
    private MessageAdapter adapter;
    private NotificationManager manager;

    private boolean isRunning;
    private Date lastMsgDate;

    private static Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);
        Intent intent = getIntent();
        conversation = intent.getStringExtra(Utils.FILE_INFO_INTENT_MESSAGE);

        messages = MESSAGES_FOR_CONV.get(conversation);
        messagesIds = IDS_FOR_CONV.get(conversation);

        setTitle(conversation);

        handler = new Handler();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //new DisplayMessages().execute(conversation);
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

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        loadConversationList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    private void sendMessage() {
        if (textBar.length() == 0)
            return;

        String s = textBar.getText().toString();
        try {
            PFMessage message = PFMessage.writeMessage(conversation, getCurrentGroup().getId(), getCurrentUser().getId(), s);
            ChatMessage chatMessage = new ChatMessage(message.getId(), getCurrentUser().getId(), new Date(), message.getBody());
            messages.add(chatMessage);
            messagesIds.add(message.getId());
            messageList.smoothScrollToPosition(messages.size() - 1);
            adapter.notifyDataSetChanged();
            textBar.setText("");
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error while sending your message ...", Toast.LENGTH_LONG).show();
        }
    }


    private void loadConversationList() {
        ParseQuery<ParseObject> q = ParseQuery.getQuery(TABLE_NAME);
        // load only newly received message..
        if (lastMsgDate != null)
            q.whereGreaterThan("createdAt", lastMsgDate);
        q.whereEqualTo(TABLE_ENTRY_NAME, conversation);
        q.whereNotContainedIn(OBJECT_ID, messagesIds);
        q.orderByDescending("createdAt");
        q.setLimit(30);
        q.findInBackground(new FindCallback<ParseObject>() {
                               @Override
                               public void done(List<ParseObject> li, ParseException e) {
                                   boolean onlySendByMe = true;
                                   if (e == null && li != null && li.size() > 0) {
                                       for (int i = li.size() - 1; i >= 0; i--) {
                                           ParseObject object = li.get(i);

                                           ChatMessage message = new ChatMessage(object.getObjectId(),
                                                   object.getString(TABLE_ENTRY_SENDER),
                                                   object.getCreatedAt(),
                                                   object.getString(TABLE_ENTRY_BODY));
                                           if (!message.getSenderId().equals(getCurrentUser().getId())) {
                                               onlySendByMe = false;
                                           }
                                           messages.add(message);
                                           messagesIds.add(object.getObjectId());
                                           if (lastMsgDate == null
                                                   || lastMsgDate.before(message.getSendDate()))
                                               lastMsgDate = message.getSendDate();
                                       }
                                       Collections.sort(messages);
                                       adapter.notifyDataSetChanged();
                                       messageList.smoothScrollToPosition(messages.size() - 1);
                                       if (!onlySendByMe)
                                           displayNotification();
                                   }
                                   handler.postDelayed(new Runnable() {
                                       @Override
                                       public void run() {
                                           if (isRunning)
                                               loadConversationList();
                                       }
                                   }, 300);
                               }
                           }

        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        refresh = false;
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

    public void clickOnSendButton(View view) {
        sendMessage();
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

}