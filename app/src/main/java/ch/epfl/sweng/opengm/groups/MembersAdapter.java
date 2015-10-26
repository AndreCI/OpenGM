package ch.epfl.sweng.opengm.groups;

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
import ch.epfl.sweng.opengm.parse.PFMember;

/**
 * Created by heinz on 10/23/15.
 */
public class MembersAdapter extends ArrayAdapter<PFMember> {

    private Context context;
    private int ressource;
    private List<PFMember> objects;
    private boolean selectMode;

    public MembersAdapter(Context context, int resource, List<PFMember> objects, boolean selectMode) {
        super(context, resource, objects);
        this.context = context;
        this.ressource = resource;
        this.objects = objects;
        this.selectMode = selectMode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MemberHolder holder;

        if (row == null) {
            row = ((Activity)context).getLayoutInflater().inflate(ressource, parent, false);

            holder = new MemberHolder();
            holder.icon = (ImageView)row.findViewById(R.id.member_img);
            holder.username = (TextView)row.findViewById(R.id.member_name);
            holder.checkbox = (CheckBox)row.findViewById(R.id.member_checkbox);

            row.setTag(holder);
        } else {
            holder = (MemberHolder) row.getTag();
        }

        PFMember user = objects.get(position);
        if (user.getPicture() != null)
            holder.icon.setImageBitmap(user.getPicture());
        holder.username.setText(user.getUsername());
        if (selectMode)
            holder.checkbox.setVisibility(View.VISIBLE);
        else
            holder.checkbox.setVisibility(View.GONE);

        return row;
    }

    static class MemberHolder {
        ImageView icon;
        TextView username;
        CheckBox checkbox;
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
    }
}
