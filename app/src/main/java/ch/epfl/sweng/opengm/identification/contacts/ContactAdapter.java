package ch.epfl.sweng.opengm.identification.contacts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private int ressource;
    private List<Contact> objects;


    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        this.context = context;
        this.ressource = resource;
        this.objects = objects;
    }

    public List<Contact> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContactHolder holder;

        if (row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(ressource, parent, false);
            holder = new ContactHolder(row);
            row.setTag(holder);
        } else {
            holder = (ContactHolder) row.getTag();
        }

        Contact contact = objects.get(position);
        holder.name.setText(contact.getName());
        holder.number.setText(contact.getPhoneNumber());
        holder.isUsingTheApp.setText(contact.isIsUsingTheApp() ? "Y" : "N");
        return row;
    }


    private static class ContactHolder {
        private final TextView name;
        private final TextView number;
        private final Button isUsingTheApp;

        private ContactHolder(View row) {
            name = (TextView) row.findViewById(R.id.contact_name);
            number = (TextView) row.findViewById(R.id.contact_number);
            isUsingTheApp = (Button) row.findViewById(R.id.contact_useTheApp);
        }

    }
}
