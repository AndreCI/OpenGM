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
import ch.epfl.sweng.opengm.events.Utils;
import ch.epfl.sweng.opengm.parse.PFGroup;

/**
 * Created by virgile on 18/11/2015.
 */
public class ShowMessages extends AppCompatActivity {
    private CustomAdapter adapter;
    private PFGroup currentGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);
        Intent intent = getIntent();
        currentGroup = intent.getParcelableExtra(Utils.GROUP_INTENT_MESSAGE);

        List<ConversationAdapter> conversations = new ArrayList<>();
        /* TODO: implement this
        for(String id : currentGroup.conversations) {
            conversations.add(PFConversation.fetchExistingConversation(id));
        }
         */

        adapter = new CustomAdapter(this, R.layout.conversation_info, conversations);

        ListView listView = (ListView) findViewById(R.id.conversation_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.conversation_title);
                //TODO : start activity of the conversation, use tag of textView
            }
        });

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
            if(convertView == null) {
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
            holder.radioButton.setChecked(conversationAdapter.getNewMessage());
            holder.textView.setTag(conversationAdapter);

            return convertView;
        }
    }

}
