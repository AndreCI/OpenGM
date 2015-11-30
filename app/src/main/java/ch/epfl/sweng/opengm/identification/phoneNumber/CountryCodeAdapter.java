package ch.epfl.sweng.opengm.identification.phoneNumber;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.opengm.R;

class CountryCodeAdapter extends ArrayAdapter<CountryCode> {

    private final Context context;
    private final int resource;
    private final List<CountryCode> objects;


    public CountryCodeAdapter(Context context, List<CountryCode> objects) {
        super(context, R.layout.item_countrycode, objects);
        this.context = context;
        this.resource = R.layout.item_countrycode;
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
            row = ((Activity) context).getLayoutInflater().inflate(resource, parent, false);
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
