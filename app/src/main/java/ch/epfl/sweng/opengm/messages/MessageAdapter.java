package ch.epfl.sweng.opengm.messages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFMember;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentGroup;
import static ch.epfl.sweng.opengm.messages.Utils.getDateFromTimestamp;

public class MessageAdapter extends ArrayAdapter<ChatMessage> {

    public MessageAdapter(Context context, int resource, List<ChatMessage> messages) {
        super(context, resource, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = getItem(position);
        ViewHolder holder;

        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessage.wasSent()) {
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

        PFMember member = getCurrentGroup().getMember(chatMessage.getSenderId());
        if (member != null) {
            if (member.getPicture() != null) {
                holder.image.setBackground(null);
                holder.image.setImageBitmap(member.getPicture());
            }
            if (chatMessage.wasSent()) {
                holder.sender.setText(String.format("(%s %s) %s",
                        member.getFirstName(), member.getLastName(), member.getNickname()));
            } else {
                holder.sender.setText(String.format("%s (%s %s)",
                        member.getNickname(), member.getFirstName(), member.getLastName()));
            }
        }
        holder.message.setText(chatMessage.getMessage());
        holder.date.setText(getDateFromTimestamp(chatMessage.getSendDate().getTime()));
        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView date;
        TextView sender;
        TextView message;
    }

}
