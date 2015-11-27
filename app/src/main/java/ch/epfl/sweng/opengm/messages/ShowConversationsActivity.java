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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowConversationsActivity extends AppCompatActivity {
    private PFGroup currentGroup;
    private List<ConversationInformation> conversationInformations;
    private final String CONV_INDEX_FORMAT = "conversationIndex_%s.txt";
    private String conversationIndexName;
    public static final int NEW_CONVERSATION_REQUEST_CODE = 1;
    //TODO: model idea : group have a list of ids corresponding to text files in another parse table, 1 file per conv + 1 with all convInfo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversations);
        Intent intent = getIntent();
        currentGroup = intent.getParcelableExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE);
        conversationIndexName = String.format(CONV_INDEX_FORMAT, currentGroup.getId());
        conversationInformations = new ArrayList<>();
        generateConversationList();
        //TODO: in background start fetching the file on the serv and then read it and compare the lists

        //TODO : check on serv which file is the most recent in background, use local one and then update if necessary

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewConversation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowConversationsActivity.this, CreateNewConversationActivity.class);
                intent.putExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE, currentGroup);
                startActivityForResult(intent, NEW_CONVERSATION_REQUEST_CODE);
            }
        });

        ListView listView = (ListView) findViewById(R.id.conversation_list);
        listView.setAdapter(new CustomAdapter(this, R.layout.conversation_info, conversationInformations));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.conversation_title);
                ConversationInformation conversationInformation = (ConversationInformation) textView.getTag();
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
                ConversationInformation conversationInformation = data.getParcelableExtra(Utils.CONVERSATION_INFO_INTENT_MESSAGE);
                conversationInformations.add(conversationInformation);
                new UpdateIndexFile().execute(conversationIndexName, conversationInformation.getConversationName(), currentGroup.getId());
                ListView listView = (ListView) findViewById(R.id.conversation_list);
                listView.setAdapter(new CustomAdapter(this, R.layout.conversation_info, conversationInformations));
                Log.v("ShowConversations", "activity result good code");
            } else {
                Log.v("ShowConversations", "activity result bad code");
            }
        }
    }

    private void generateConversationList() {
        File file = new File(getFilesDir(), conversationIndexName);
        new ReadIndexFile().execute(file);
    }

    private class CustomAdapter extends ArrayAdapter<ConversationInformation> {
        private List<ConversationInformation> conversations;

        public CustomAdapter(Context context, int resource, List<ConversationInformation> conversations) {
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

            ConversationInformation conversationInformation = conversations.get(position);
            holder.textView.setText(conversationInformation.getConversationName());
            holder.radioButton.setChecked(false);
            holder.textView.setTag(conversationInformation);

            return convertView;
        }
    }

    class ReadIndexFile extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... params) {
            try {
                conversationInformations = Utils.readIndexFile(params[0].getAbsolutePath());
            } catch (IOException e) {
                Log.e("ShowConv readIndF", "IOException with file: " + params[0].getPath());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView listView = (ListView) findViewById(R.id.conversation_list);
            listView.setAdapter(new CustomAdapter(ShowConversationsActivity.this, R.layout.conversation_info, conversationInformations));
        }

    }

    class UpdateIndexFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Utils.writeConversationInformationLocal(params[0], new ConversationInformation(params[1], params[2]), ShowConversationsActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListView listView = (ListView) findViewById(R.id.conversation_list);
            listView.setAdapter(new CustomAdapter(ShowConversationsActivity.this, R.layout.conversation_info, conversationInformations));
        }


    }

}
