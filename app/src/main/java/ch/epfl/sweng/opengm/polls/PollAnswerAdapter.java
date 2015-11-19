package ch.epfl.sweng.opengm.polls;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;

public class PollAnswerAdapter extends ArrayAdapter<String> {


    private final Context context;
    private final int ressource;
    private final List<String> objects;


    public PollAnswerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.ressource = resource;
        this.objects = objects;
    }

    public List<String> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AnswerHolder holder;

        if (row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(ressource, parent, false);
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
