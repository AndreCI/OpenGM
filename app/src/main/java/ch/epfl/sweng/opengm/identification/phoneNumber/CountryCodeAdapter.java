package ch.epfl.sweng.opengm.identification.phoneNumber;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;

public class CountryCodeAdapter extends ArrayAdapter<CountryCode> {

    private Context context;
    private int ressource;
    private List<CountryCode> objects;


    public CountryCodeAdapter(Context context, int resource, List<CountryCode> objects) {
        super(context, resource, objects);
        this.context = context;
        this.ressource = resource;
        this.objects = objects;
    }

    public List<CountryCode> getObjects() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CountryCodeHolder holder;

        if (row == null) {
            row = ((Activity) context).getLayoutInflater().inflate(ressource, parent, false);
            holder = new CountryCodeHolder(row);
            row.setTag(holder);
        } else {
            holder = (CountryCodeHolder) row.getTag();
        }

        CountryCode countryCode = objects.get(position);
        holder.country.setText(countryCode.getCountry());
        holder.code.setText(countryCode.getCode());
        return row;
    }


    private static class CountryCodeHolder {
        private final TextView country;
        private final TextView code;

        private CountryCodeHolder(View row) {
            country = (TextView) row.findViewById(R.id.member_country_name);
            code = (TextView) row.findViewById(R.id.member_country_code);
        }

    }
}
