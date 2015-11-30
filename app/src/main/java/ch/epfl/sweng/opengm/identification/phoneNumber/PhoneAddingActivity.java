package ch.epfl.sweng.opengm.identification.phoneNumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
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
import ch.epfl.sweng.opengm.identification.InputUtils;
import ch.epfl.sweng.opengm.identification.RegisterActivity;

import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;
import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class PhoneAddingActivity extends AppCompatActivity {

    private CountryCodeAdapter mAdapter;
    private ListView list;

    private final List<CountryCode> countryCodes = new ArrayList<>();

    private final HashMap<String, CountryCode> codeForCountries = new HashMap<>();

    private TextView mEditCountry;
    private TextView mEditCode;
    private TextView mEditNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_adding);

        // ************************************************************************** //
        // FIXME: How to do a pointer on activity ?
        /* int incomingActivityKey = getIntent().getIntExtra(INCOMING_ACTIVITY_KEY);
        AppCompatActivity incomingActivity = RegisterActivity;
        switch (incomingActivityKey) {
            case 1:
                incomingActivity = RegisterActivity;
                break;
            case 2:
                incomingActivity = EditUserProfileActivity;
                break;
            default:
                incomingActivity = RegisterActivity;
        } */
        // ************************************************************************** //


        mEditCountry = (TextView) findViewById(R.id.country_name);
        mEditCode = (TextView) findViewById(R.id.country_code);
        mEditNumber = (TextView) findViewById(R.id.input_phoneNumber);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        onTapOutsideBehaviour(findViewById(R.id.phone_adding_outmostLayout), this);

        mEditCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditCountry.setText(getCountryForCode(mEditCode.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    s.append("+");
                } else if (!s.toString().startsWith("+")) {
                    s.insert(0, "+");
                }
            }
        });

        String[] array = getResources().getStringArray(R.array.countryCodes);

        for (String s : array) {
            CountryCode cc = new CountryCode(s);
            countryCodes.add(cc);
            codeForCountries.put(cc.getCode(), cc);
        }

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String isoCode = tm.getSimCountryIso();

        CountryCode cc = getPrefixForIso(isoCode);

        mEditCode.setText(cc == null ? "+0" : cc.getCode());
        mEditCountry.setText(cc == null ? getString(R.string.invalid_phone_number) : cc.getCountry());

    }

    private CountryCode getPrefixForIso(String iso) {
        for (CountryCode c : codeForCountries.values()) {
            if (c.containsIso(iso)) {
                return c;
            }
        }
        return null;
    }

    private String getCountryForCode(String code) {
        CountryCode cc = codeForCountries.get(code);
        return cc == null ? getString(R.string.invalid_phone_number) : cc.getCountry();
    }

    public void showCodeList(@SuppressWarnings("UnusedParameters") View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.title_phone_number);

        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_choose_country_code, null);

        list = (ListView) view.findViewById(R.id.listView_coutrycodes);

        mAdapter = new CountryCodeAdapter(view.getContext(), countryCodes);
        list.setAdapter(mAdapter);


        final SearchView sv = (SearchView) view.findViewById(R.id.filterCodeCountry);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return showResult(view, query);
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                return showResult(view, newText);
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

    @SuppressWarnings("SameReturnValue")
    private boolean showResult(View view, String query) {
        Collections.sort(mAdapter.getObjects(), sortList(query));
        List<CountryCode> displayedCc = new ArrayList<>();
        for (CountryCode cc : mAdapter.getObjects()) {
            if (query.isEmpty() || stripAccents(cc.toString()).contains(stripAccents(query))) {
                displayedCc.add(cc);
            }
        }
        list.setAdapter(new CountryCodeAdapter(view.getContext(), displayedCc));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phone_number, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_phone_number_help:
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(getString(R.string.help_phone_number));
                alertDialog.setMessage(getString(R.string.information_phone_number));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            case R.id.action_phone_number_validate:
                // Intent
                if (InputUtils.isPhoneNumberValid(mEditNumber.getText().toString()) && !mEditCountry.getText().equals(getString(R.string.invalid_phone_number))) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(RegisterActivity.PHONE_KEY, mEditCode.getText() + " " + mEditNumber.getText());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    mEditNumber.setError(getString(R.string.invalid_phone_input));
                    mEditNumber.requestFocus();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
