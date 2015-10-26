package ch.epfl.sweng.opengm.events;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;

/**
 * Created by virgile on 26/10/2015.
 */
public class CheckParticipantAdapter extends ArrayAdapter<CheckParticipant> {
    private Context context;
    private int layoutResourceId;
    private List<CheckParticipant> checkParticipantList;

    public List<CheckParticipant> getCheckParticipantList() {
        return checkParticipantList;
    }

    public CheckParticipantAdapter(Context context, int layoutResourceId, List<CheckParticipant> checkParticipantList) {
        super(context, layoutResourceId, checkParticipantList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.checkParticipantList = checkParticipantList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CheckParticipantHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CheckParticipantHolder();
            holder.checkBox = (CheckBox)row.findViewById(R.id.ParticipantCheckBox);
            holder.participantName = (TextView)row.findViewById(R.id.ParticipantName);

            row.setTag(holder);
        } else {
            holder = (CheckParticipantHolder) row.getTag();
        }

        CheckParticipant checkParticipant = checkParticipantList.get(position);
        holder.participantName.setText(checkParticipant.getName());
        holder.checkBox.setChecked(checkParticipant.getCheck());

        return row;
    }

    private static class CheckParticipantHolder {
        CheckBox checkBox;
        TextView participantName;
    }
}
