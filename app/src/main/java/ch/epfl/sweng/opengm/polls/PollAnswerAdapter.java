package ch.epfl.sweng.opengm.polls;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;

class PollAnswerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int resource;
    private final List<String> objects;


    public PollAnswerAdapter(Context context, List<String> objects) {
        super(context, R.layout.item_answer_poll, objects);
        this.context = context;
        this.resource = R.layout.item_answer_poll;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AnswerHolder holder;

        if (row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(resource, parent, false);
            holder = new AnswerHolder(row);
            row.setTag(holder);
        } else {
            holder = (AnswerHolder) row.getTag();
        }

        String answer = objects.get(position);
        holder.answer.setText(answer);
        return row;
    }

    private static class AnswerHolder {
        private final TextView answer;

        private AnswerHolder(View row) {
            answer = (TextView) row.findViewById(R.id.answer_textView);
        }

    }

}
