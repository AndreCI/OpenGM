package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

public class PermissionsAdapter extends ArrayAdapter<PFGroup.Permission>{
    private Context context;
    private int resource;
    private List<PFGroup.Permission> permissions;
    private List<Boolean> checks;

    public PermissionsAdapter(Context context, int resource, List<PFGroup.Permission> objects, List<Boolean> checks) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.permissions = objects;
        this.checks = checks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        PermissionHolder holder;

        if(row == null){
            row =((Activity) context).getLayoutInflater().inflate(resource, parent, false);

            holder = new PermissionHolder();
            holder.checkBox = (CheckBox)row.findViewById(R.id.role_checkbox);
            holder.textView = (TextView)row.findViewById(R.id.role_name);

            row.setTag(holder);
        } else {
            holder = (PermissionHolder) row.getTag();
        }

        holder.textView.setText(permissions.get(position).toString());
        holder.checkBox.setChecked(checks.get(position));

        return row;
    }

    static class PermissionHolder {
        CheckBox checkBox;
        TextView textView;
    }
}
