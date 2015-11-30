package ch.epfl.sweng.opengm.polls.participants;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;

class ParticipantAdapter extends ArrayAdapter<Participant> {

    private final Context context;
    private final int resource;
    private final List<Participant> objects;


    public ParticipantAdapter(Context context, List<Participant> objects) {
        super(context, R.layout.item_poll_participant, objects);
        this.context = context;
        this.resource = R.layout.item_poll_participant;
        this.objects = objects;
    }

    public List<Participant> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ParticipantHolder holder;

        if (row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(resource, parent, false);
            holder = new ParticipantHolder(row);
            row.setTag(holder);
        } else {
            holder = (ParticipantHolder) row.getTag();
        }

        Participant participant = objects.get(position);
        holder.box.setTag(participant);
        holder.prefix.setText(participant.getPrefix());
        holder.name.setText(participant.getName());
        holder.info.setText(participant.getInfo());
        holder.image.setBackgroundResource(participant.isGroup() ? R.drawable.ic_action_group : R.drawable.ic_person);

        return row;
    }


    private static class ParticipantHolder {
        private final CheckBox box;
        private final TextView prefix;
        private final TextView name;
        private final TextView info;
        private final ImageView image;

        private ParticipantHolder(View row) {
            box = (CheckBox) row.findViewById(R.id.participant_box_poll);
            prefix = (TextView) row.findViewById(R.id.participant_prefix_poll);
            name = (TextView) row.findViewById(R.id.participant_name_poll);
            info = (TextView) row.findViewById(R.id.participant_info_poll);
            image = (ImageView) row.findViewById(R.id.participant_image_poll);
        }

    }

}
