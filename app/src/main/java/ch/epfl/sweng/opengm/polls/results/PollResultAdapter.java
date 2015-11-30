package ch.epfl.sweng.opengm.polls.results;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFPoll;

public class PollResultAdapter extends ArrayAdapter<PFPoll.Answer> {

    private final Context context;
    private final int resource;
    private final HashMap<PFPoll.Answer, Integer> colors;
    private final List<PFPoll.Answer> objects;


    public PollResultAdapter(Context context, List<PFPoll.Answer> objects, HashMap<PFPoll.Answer, Integer> colors) {
        super(context, R.layout.item_result_poll, objects);
        this.context = context;
        this.resource = R.layout.item_result_poll;
        this.objects = objects;
        this.colors = colors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultHolder holder;

        if (row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(resource, parent, false);
            holder = new ResultHolder(row);
            row.setTag(holder);
        } else {
            holder = (ResultHolder) row.getTag();
        }

        PFPoll.Answer answer = objects.get(position);
        holder.color.setBackgroundColor(colors.get(answer));
        holder.answer.setText(answer.getAnswer());
        holder.votes.setText(String.format("%d",answer.getVotes()));
        return row;
    }


    private static class ResultHolder {
        private final View color;
        private final TextView answer;
        private final TextView votes;

        private ResultHolder(View row) {
            color = row.findViewById(R.id.result_poll_color);
            answer = (TextView) row.findViewById(R.id.result_poll_answer);
            votes = (TextView) row.findViewById(R.id.result_poll_votes);
        }
    }

}
