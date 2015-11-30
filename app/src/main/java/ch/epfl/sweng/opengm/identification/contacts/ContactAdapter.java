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

class ContactAdapter extends ArrayAdapter<Contact> {

    private final Context context;
    private final int resource;
    private final List<Contact> objects;
    private final boolean appContact;


    public ContactAdapter(Context context, List<Contact> objects, boolean appContact) {
        super(context, R.layout.item_contact, objects);
        this.context = context;
        this.resource = R.layout.item_contact;
        this.objects = objects;
        this.appContact = appContact;
    }

    public List<Contact> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContactHolder holder;

        if (row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(resource, parent, false);
            holder = new ContactHolder(row);
            row.setTag(holder);
        } else {
            holder = (ContactHolder) row.getTag();
        }

        Contact contact = objects.get(position);
        holder.name.setText(contact.getName());
        holder.number.setText(contact.getPhoneNumber());
        if (appContact) {
            holder.isUsingTheApp.setBackgroundResource(R.mipmap.ic_arrow_forward_white_24dp);
        } else {
            holder.isUsingTheApp.setText(contact.isIsUsingTheApp() ? "Y" : "N");
        }
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
