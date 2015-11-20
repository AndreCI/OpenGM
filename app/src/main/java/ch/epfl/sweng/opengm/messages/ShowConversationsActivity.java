package ch.epfl.sweng.opengm.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowConversationsActivity extends AppCompatActivity {
    private CustomAdapter adapter;
    private PFGroup currentGroup;
    private List<ConversationInformation> conversationInformations;

    //TODO: model idea : group have a list of ids corresponding to text files in another parse table

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_conversations);
        Intent intent = getIntent();
        currentGroup = intent.getParcelableExtra(ch.epfl.sweng.opengm.events.Utils.GROUP_INTENT_MESSAGE);
        conversationInformations = new ArrayList<>();

        //TODO: conversationInformations.addAll(currentGroup.getConversationsInformations());


        List<ConversationAdapter> conversations = getConversations();

        //TODO : check on serv which file is the most recent

        adapter = new CustomAdapter(this, R.layout.conversation_info, conversations);

        ListView listView = (ListView) findViewById(R.id.conversation_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.conversation_title);
                String title = ((ConversationAdapter) textView.getTag()).getTitle();
                Intent intent; //.....
                //TODO : start activity of the conversation,0 use tag of textView and send path of txt file with Utils.FILE_PATH_INTENT_MESSAGE
            }
        });
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
