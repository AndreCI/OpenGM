package ch.epfl.sweng.opengm.messages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

public class ShowConversationsActivity extends AppCompatActivity {
    public static boolean refresh = false;
    public static String conversationToRefresh = null;
    private PFGroup currentGroup;
    private List<String> conversations;
    public static final HashMap<String, List<String>> IDS_FOR_CONV = new HashMap<>();
    public static final HashMap<String, List<ChatMessage>> MESSAGES_FOR_CONV = new HashMap<>();
    private CustomAdapter adapter;

    private AlertDialog addConv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversations);
        currentGroup = OpenGMApplication.getCurrentGroup();
        conversations = new ArrayList<>();

        setTitle("Conversations of " + currentGroup.getName());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_add_conversation, null);
        final EditText edit = (EditText) dialogLayout.findViewById(R.id.dialog_add_conv_name);
        builder.setView(dialogLayout)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(edit.getText());
                        if (!name.isEmpty()) {
                            if (!conversations.contains(name)) {
                                conversations.add(name);
                                Collections.sort(conversations);
                                adapter.notifyDataSetChanged();
                                currentGroup.addConversation(name);
                                IDS_FOR_CONV.put(name, new ArrayList<String>());
                                MESSAGES_FOR_CONV.put(name, new ArrayList<ChatMessage>());
                            } else {
                                Toast.makeText(getBaseContext(), "Conversation name already exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Conversation name cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                        edit.getText().clear();
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        addConv = builder.create();

        generateConversationList();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewConversation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addConv.show();
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
                Intent intent = new Intent(ShowConversationsActivity.this, ShowMessagesActivity.class);
                intent.putExtra(Utils.FILE_INFO_INTENT_MESSAGE, conversationName);
                intent.putExtra(Utils.NOTIF_INTENT_MESSAGE, true);
                refresh = true;
                conversationToRefresh = conversationName;
                startActivity(intent);
            }
        });

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

    private void generateConversationList() {
        new ReadIndex().execute(currentGroup);
    }

    private class CustomAdapter extends ArrayAdapter<String> {

        public CustomAdapter(Context context, int resource) {
            super(context, resource, conversations);
        }

        private class ViewHolder {
            TextView textView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.conversation_info, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.conversation_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < conversations.size()) {
                String conversation = conversations.get(position);
                holder.textView.setText(conversation);
            }
            return convertView;
        }
    }

    class ReadIndex extends AsyncTask<PFGroup, Void, List<String>> {

        @Override
        protected List<String> doInBackground(PFGroup... params) {
            List<String> result = new ArrayList<>();
            for (String s : params[0].getConversationInformations()) {
                if (s != null && !s.isEmpty()) {
                    result.add(s);
                    IDS_FOR_CONV.put(s, new ArrayList<String>());
                    MESSAGES_FOR_CONV.put(s, new ArrayList<ChatMessage>());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            conversations.clear();
            conversations.addAll(result);
            Collections.sort(conversations);
            adapter.notifyDataSetChanged();
        }
    }

}
