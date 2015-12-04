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

public class RolesAdapter extends ArrayAdapter<String>{

    private Context context;
    private int ressource;
    private List<String> roles;
    boolean checkingModifiesActivity;

    public RolesAdapter(Context context, int resource, List<String> objects, boolean checkingModifiesActivity) {
        super(context, resource, objects);
        this.context = context;
        this.ressource = resource;
        this.roles = objects;
        this.checkingModifiesActivity = checkingModifiesActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        final RoleHolder holder;

        if(row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(ressource, parent, false);

            holder = new RoleHolder();
            holder.checkBox = (CheckBox)row.findViewById(R.id.role_checkbox);
            holder.textView = (TextView)row.findViewById(R.id.role_name);

            row.setTag(holder);
        } else {
            holder = (RoleHolder) row.getTag();
        }

        String role = roles.get(position);
        holder.textView.setText(role);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                if(checkingModifiesActivity){
                    ((ManageRolesActivity) context).updateOptions(!holder.checkBox.isChecked());
                }
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkingModifiesActivity) {
                    ((ManageRolesActivity) context).updateOptions(((CheckBox) v).isChecked());
                }
            }
        });

        return row;
    }

    static class RoleHolder {
        CheckBox checkBox;
        TextView textView;
    }
}
