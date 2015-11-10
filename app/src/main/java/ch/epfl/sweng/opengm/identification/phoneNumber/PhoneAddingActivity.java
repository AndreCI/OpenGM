package ch.epfl.sweng.opengm.identification.phoneNumber;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.opengm.R;

import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class PhoneAddingActivity extends AppCompatActivity {

    private CountryCodeAdapter mAdapter;
    private ListView list;

    private final List<CountryCode> countryCodes = new ArrayList<>();

    private final HashMap<String, CountryCode> codeForCountries = new HashMap<>();

    private TextView mEditCountry;
    private TextView mEditCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_adding);

        mEditCountry = (TextView) findViewById(R.id.country_name);
        mEditCode = (TextView) findViewById(R.id.country_code);

        mEditCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CountryCode cc = codeForCountries.get(mEditCode.getText().toString());
                if (cc == null) {
                    mEditCountry.setText(R.string.invalid_phone_number);
                } else {
                    mEditCountry.setText(cc.getCountry());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    s.append("+");
                }
            }
        });

        String[] array = getResources().getStringArray(R.array.countryCodes);

        for (String s : array) {
            CountryCode cc = new CountryCode(s);
            countryCodes.add(cc);
            codeForCountries.put(cc.getCode(), cc);
        }
    }

    public void showCodeList(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select your country");

        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_choose_country_code, null);

        list = (ListView) view.findViewById(R.id.listView_coutrycodes);

        mAdapter = new CountryCodeAdapter(view.getContext(), R.layout.item_countrycode, countryCodes);
        list.setAdapter(mAdapter);


        final SearchView sv = (SearchView) view.findViewById(R.id.filterCodeCountry);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return showResult(query);
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                return showResult(newText);
            }
        });
        Collections.sort(mAdapter.getObjects());


        builder.setView(view);
        final AlertDialog dialog = builder.create();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryCode cc = mAdapter.getObjects().get(position);
                mEditCountry.setText(cc.getCountry());
                mEditCode.setText(cc.getCode());
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private boolean showResult(String query) {
        Collections.sort(mAdapter.getObjects(), sortList(query));
        List<CountryCode> displayedCc = new ArrayList<>();
        for (CountryCode cc : mAdapter.getObjects()) {
            if (query.isEmpty() || stripAccents(cc.toString()).contains(stripAccents(query))) {
                displayedCc.add(cc);
            }
        }
        mAdapter = new CountryCodeAdapter(this, R.layout.item_countrycode, displayedCc);
        list.setAdapter(mAdapter);
        return true;
    }

    private Comparator<CountryCode> sortList(final String s) {
        return new Comparator<CountryCode>() {
            @Override
            public int compare(CountryCode lhs, CountryCode rhs) {
                String c1 = lhs.toString();
                String c2 = rhs.toString();
                if (c1.contains(s) && c2.contains(s)) {
                    return c1.compareTo(c2);
                } else if (c1.contains(s) && !c2.contains(s)) {
                    return -1;
                } else if (!c1.contains(s) && c2.contains(s)) {
                    return 1;
                } else {
                    return c1.compareTo(c2);
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
