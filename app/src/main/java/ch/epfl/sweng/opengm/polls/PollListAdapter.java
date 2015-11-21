package ch.epfl.sweng.opengm.polls;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFPoll;

public class PollListAdapter extends ArrayAdapter<PFPoll> {

    private final Context context;
    private final int resource;
    private final List<PFPoll> objects;


    public PollListAdapter(Context context, List<PFPoll> objects) {
        super(context, R.layout.item_poll, objects);
        this.context = context;
        this.resource = R.layout.item_poll;
        this.objects = objects;
    }

    public List<PFPoll> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PollHolder holder;

        if (row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(resource, parent, false);
            holder = new PollHolder(row);
            row.setTag(holder);
        } else {
            holder = (PollHolder) row.getTag();
        }

        PFPoll poll = objects.get(position);

        holder.name.setText(poll.getName());
        holder.info.setText(poll.getDescription());
        holder.date.setText(poll.getDeadline().toString());

        return row;
    }


    private static class PollHolder {
        private final TextView name;
        private final TextView info;
        private final TextView date;

        private PollHolder(View row) {
            name = (TextView) row.findViewById(R.id.poll_name);
            info = (TextView) row.findViewById(R.id.poll_description);
            date = (TextView) row.findViewById(R.id.poll_deadline);
        }

    }

}
