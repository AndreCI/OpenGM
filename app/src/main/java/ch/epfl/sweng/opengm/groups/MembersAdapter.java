package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFUser;

/**
 * Created by heinz on 10/23/15.
 */
public class MembersAdapter extends ArrayAdapter<PFUser> {

    private Context context;
    private int ressource;
    private List<PFUser> objects;

    public MembersAdapter(Context context, int resource, List<PFUser> objects) {
        super(context, resource, objects);
        this.context = context;
        this.ressource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = ((Activity)context).getLayoutInflater().inflate(ressource, parent, false);

            row.setTag(0, row.findViewById(R.id.member_img));
            row.setTag(1, row.findViewById(R.id.member_name));
        }

        PFUser user = objects.get(position);
        ((ImageView)row.getTag(0)).setImageBitmap(user.getPicture());
        ((TextView)row.getTag(1)).setText(user.getUsername());

        return row;
    }
}
