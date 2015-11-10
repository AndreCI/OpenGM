package ch.epfl.sweng.opengm.identification.phoneNumber;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.opengm.R;

public class PhoneAddingActivity extends AppCompatActivity {

    private CountryCodeAdapter mAdapter;
    private ListView list;

    private final List<CountryCode> countryCodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_adding);

        list = (ListView) findViewById(R.id.listView_coutrycodes);

        String[] array = getResources().getStringArray(R.array.countryCodes);

        for (String s : array) {
            countryCodes.add(new CountryCode(s));
            Log.d("CODE", new CountryCode(s).toString());
        }

        mAdapter = new CountryCodeAdapter(this, R.layout.item_countrycode, countryCodes);
        Log.d("LIST", list + "");
        list.setAdapter(mAdapter);
        Log.d("LIST", list + "");
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
