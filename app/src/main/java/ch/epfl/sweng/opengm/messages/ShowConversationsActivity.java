package ch.epfl.sweng.opengm.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFConversation;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowConversationsActivity extends AppCompatActivity {
    private PFGroup currentGroup;
    private List<PFConversation> conversations;
    public static final int NEW_CONVERSATION_REQUEST_CODE = 1;
    //TODO: model idea : group have a list of ids corresponding to text files in another parse table, 1 file per conv + 1 with all convInfo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversations);
        Intent intent = getIntent();
        int index = intent.getIntExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE, -1);
        currentGroup = OpenGMApplication.getCurrentUser().getGroups().get(index);
        conversations = new ArrayList<>();
        generateConversationList();
        //TODO: in background start fetching the file on the serv and then read it and compare the lists

        //TODO : check on serv which file is the most recent in background, use local one and then update if necessary

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewConversation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowConversationsActivity.this, CreateNewConversationActivity.class);
                intent.putExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE, currentGroup.getId());
                startActivityForResult(intent, NEW_CONVERSATION_REQUEST_CODE);
            }
        });

        ListView listView = (ListView) findViewById(R.id.conversation_list);
        listView.setAdapter(new CustomAdapter(this, R.layout.conversation_info, conversations));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.conversation_title);
                PFConversation conversationInformation = (PFConversation) textView.getTag();
                Intent intent = new Intent(ShowConversationsActivity.this, ShowMessagesActivity.class);
                intent.putExtra(Utils.FILE_INFO_INTENT_MESSAGE, conversationInformation);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_CONVERSATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String conversationName = data.getStringExtra(Utils.CONVERSATION_INFO_INTENT_MESSAGE);
                new CreateNewConvResult().execute(conversationName);
            } else {
                Log.v("ShowConversations", "activity result bad code");
            }
        }
    }

    private void generateConversationList() {
        new ReadIndex().execute(currentGroup);
    }

    private class CustomAdapter extends ArrayAdapter<PFConversation> {
        private List<PFConversation> conversations;

        public CustomAdapter(Context context, int resource, List<PFConversation> conversations) {
            super(context, resource, conversations);
            this.conversations = new ArrayList<>();
            this.conversations.addAll(conversations);
        }

        private class ViewHolder {
            TextView textView;
            RadioButton radioButton;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.conversation_info, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.conversation_title);
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.conversation_updated);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Log.v("showConversationsAct", String.valueOf(conversations.size()) + ':' + position);
            if(position < conversations.size()) {
                PFConversation conversationInformation = conversations.get(position);
                holder.textView.setText(conversationInformation.getConversationName());
                holder.radioButton.setChecked(false);
                holder.textView.setTag(conversationInformation);
            }
            return convertView;
        }
    }

    class ReadIndex extends AsyncTask<PFGroup, Void, Void> {

        @Override
        protected Void doInBackground(PFGroup... params) {
            for(String s : params[0].getConversationInformations()) {
                try {
                    conversations.add(PFConversation.fetchExistingConversation(s));
                } catch (PFException e) {
                    Log.v("ShowConv read index", "coudln't add conversation " + s);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView listView = (ListView) findViewById(R.id.conversation_list);
            listView.setAdapter(new CustomAdapter(ShowConversationsActivity.this, R.layout.conversation_info, conversations));
        }
    }

    class CreateNewConvResult extends AsyncTask<String, Void, PFConversation> {

        @Override
        protected PFConversation doInBackground(String... params) {

            PFConversation conversation = null;
            try {
                conversation = PFConversation.createNewConversation(params[0], currentGroup.getId(), ShowConversationsActivity.this);
                conversation.writeConversationInformation();
            } catch (FileNotFoundException e) {
                Log.e("CreateNewConv", "couldn't createFile", e);
            } catch (ParseException|PFException e) {
                Log.e("CreateNewConv", "error with parse");
            }
            return conversation;
        }

        @Override
        protected void onPostExecute(PFConversation conversation) {
            if(conversation != null) {
                conversations.add(conversation);
                ListView listView = (ListView) findViewById(R.id.conversation_list);
                listView.setAdapter(new CustomAdapter(ShowConversationsActivity.this, R.layout.conversation_info, conversations));
                new UpdateIndex().execute();
                Log.v("CreateNewConversation", conversation.getConversationName());
            } else {
                Log.e("ShowConv res","coudln't create conversation");
            }
        }
    }

    class UpdateIndex extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> ids = new ArrayList<>();
            for(PFConversation conversation : conversations) {
                ids.add(conversation.getId());
            }
            currentGroup.setConversationInformations(ids);
            return null;
        }
    }
}
