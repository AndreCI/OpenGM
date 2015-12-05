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

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowConversationsActivity extends AppCompatActivity {
    private PFGroup currentGroup;
    private List<String> conversations;
    public static final int NEW_CONVERSATION_REQUEST_CODE = 1;
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversations);
        currentGroup = OpenGMApplication.getCurrentGroup();
        conversations = new ArrayList<>();
        generateConversationList();
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
        adapter = new CustomAdapter(this, R.layout.conversation_info);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.conversation_title);
                String conversationName = textView.getText().toString();
                Log.v("ShowConversation", conversationName);
                Intent intent = new Intent(ShowConversationsActivity.this, ShowMessagesActivity.class);
                intent.putExtra(Utils.FILE_INFO_INTENT_MESSAGE, conversationName);
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

    public void updateList(List<String> list) {
        conversations.clear();
        conversations.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private class CustomAdapter extends ArrayAdapter<String> {

        public CustomAdapter(Context context, int resource) {
            super(context, resource, conversations);
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
                String conversation = conversations.get(position);
                holder.textView.setText(conversation);
                holder.radioButton.setChecked(false);
            }
            return convertView;
        }
    }

    class ReadIndex extends AsyncTask<PFGroup, Void, List<String>> {

        @Override
        protected List<String> doInBackground(PFGroup... params) {
            List<String> result = new ArrayList<>();
            for(String s : params[0].getConversationInformations()) {
                if(s != null && !s.isEmpty()) {
                        result.add(s);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            updateList(result);
        }
    }

    class CreateNewConvResult extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(String conversation) {
            if(conversation != null && !conversations.contains(conversation)) {
                List<String> oldConvs = new ArrayList<>(conversations);
                oldConvs.add(conversation);
                updateList(oldConvs);
                currentGroup.addConversation(conversation);
                Log.v("CreateNewConversation", conversation);
            } else {
                Log.e("ShowConv res","coudln't create conversation");
            }
        }
    }
}
