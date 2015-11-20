package ch.epfl.sweng.opengm.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

import static ch.epfl.sweng.opengm.events.Utils.stringToDate;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowConversationsActivity extends AppCompatActivity {
    private CustomAdapter adapter;
    private PFGroup currentGroup;
    private List<ConversationInformation> conversationInformations;
    private final String CONV_INDEX_FORMAT = "conversationIndex_%s.txt";
    private Date serveurLastUpdate;
    public static final int NEW_CONVERSATION_REQUEST_CODE = 1;
    //TODO: model idea : group have a list of ids corresponding to text files in another parse table, 1 file per conv + 1 with all convInfo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversations);
        Intent intent = getIntent();
        currentGroup = intent.getParcelableExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE);
        generateConversationList();
        //TODO: in background start fetching the file on the serv and then read it and compare the lists
        List<ConversationAdapter> conversations = getConversations();

        //TODO : check on serv which file is the most recent in background, use local one and then update if necessary

        adapter = new CustomAdapter(this, R.layout.conversation_info, conversations);

        ListView listView = (ListView) findViewById(R.id.conversation_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.conversation_title);
                String title = ((ConversationAdapter) textView.getTag()).getTitle();
                Intent intent = new Intent(ShowConversationsActivity.this, ShowMessagesActivity.class);
                startActivity(intent);
                //TODO : start activity of the conversation, use tag of textView and send path of txt file with Utils.FILE_PATH_INTENT_MESSAGE
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNewConversation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowConversationsActivity.this, CreateNewConversationActivity.class);
                intent.putExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE, currentGroup);
                startActivityForResult(intent, NEW_CONVERSATION_REQUEST_CODE);
                //TODO: add new Conversation
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_CONVERSATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                conversationInformations.add((ConversationInformation) data.getParcelableExtra(Utils.CONVERSATION_INFO_INTENT_MESSAGE));
            } else {

            }
        }
    }

    private void generateConversationList() {
        String indexName = String.format(CONV_INDEX_FORMAT, currentGroup.getId());
        File file = new File(getFilesDir(), indexName);
        conversationInformations = new ArrayList<>(readConversationInformationsFromFile(file));
        //TODO: conversationInformations.addAll(currentGroup.getConversationsInformations()); or use file
    }

    private List<ConversationInformation> readConversationInformationsFromFile(File file) {
        List<ConversationInformation> list = new ArrayList<>();
        if(file.exists()) {
            try {
                InputStream inputStream = openFileInput(file.getAbsolutePath());
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line = bufferedReader.readLine();
                    getIndexInformations(line);
                    //get info about file like last updated time
                    line = bufferedReader.readLine();
                    while (line != null) {
                        try {
                            list.add(ConversationInformation.createFromString(line));
                        } catch (IllegalArgumentException e) {
                            //don't add it
                        }
                    }
                    inputStream.close();
                } else {
                    Log.v("show conv", "couldn't read file");
                }
            } catch (FileNotFoundException e) {
                Log.e("show conv", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("show conv", "Can not read file: " + e.toString());
            }
        }
        return list;
    }

    private void getIndexInformations(String line) {
        serveurLastUpdate = stringToDate(line);
    }

    private List<ConversationAdapter> getConversations() {
        List<ConversationAdapter> result = new ArrayList<>();
        for (ConversationInformation inf : conversationInformations) {
            result.add(new ConversationAdapter(inf));
        }
        return result;
    }

    private class CustomAdapter extends ArrayAdapter<ConversationAdapter> {
        private List<ConversationAdapter> conversations;

        public CustomAdapter(Context context, int resource, List<ConversationAdapter> conversations) {
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

            ConversationAdapter conversationAdapter = conversations.get(position);
            holder.textView.setText(conversationAdapter.getTitle());
            holder.radioButton.setChecked(false);
            holder.textView.setTag(conversationAdapter);

            return convertView;
        }
    }

}
