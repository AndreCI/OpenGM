package ch.epfl.sweng.opengm.groups;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;

public class PermissionsAdapter extends ArrayAdapter<PFGroup.Permission>{
    private Context context;
    private int resource;
    private Map<PFGroup.Permission, Boolean> checkedPermissions = new HashMap<>();
    private List<PFGroup.Permission> permissions;

    public PermissionsAdapter(Context context, int resource, int textViewResourceId, List<PFGroup.Permission> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.resource = resource;
        for(PFGroup.Permission permission : objects){
            checkedPermissions.put(permission, false);
        }
        this.permissions = objects;
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

        holder.textView.setText(permissions.get(position).getValue());
        holder.checkBox.setChecked(checkedPermissions.get(permissions.get(position)));

        return row;
    }

    static class PermissionHolder {
        CheckBox checkBox;
        TextView textView;
    }
}
