package ch.epfl.sweng.opengm.identification.contacts;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.userProfile.MemberProfileActivity;

import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class PhoneContactsActivity extends AppCompatActivity {

    private final static String SMS_TYPE = "vnd.android-dir/mms-sms";
    private final static String SMS_NUMBER = "address";
    private final static String SMS_BODY = "sms_body";

    private ContactAdapter mAdapter;
    private ListView list;

    private final List<Contact> mContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contacts);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Phone contacts");

        list = (ListView) findViewById(R.id.contacts_list);

        mAdapter = new ContactAdapter(this, mContacts, false);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Contact cc = mAdapter.getObjects().get(position);
                if (cc.isIsUsingTheApp()) {
                    // Already uses the app
                    Intent i = new Intent(PhoneContactsActivity.this, MemberProfileActivity.class);
                    i.putExtra(MemberProfileActivity.MEMBER_KEY, cc.getMember());
                    startActivity(i);
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(PhoneContactsActivity.this).create();
                    alertDialog.setTitle(getString(R.string.invite_contact));
                    alertDialog.setMessage(String.format(getString(R.string.cost_contact), cc.getName()));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                    smsIntent.setType(SMS_TYPE);
                                    smsIntent.putExtra(SMS_NUMBER, cc.getPhoneNumber());
                                    smsIntent.putExtra(SMS_BODY, getString(R.string.body_contact));
                                    startActivity(smsIntent);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
        fillContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =
                (SearchView) menu.findItem(R.id.contact_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @SuppressWarnings("SameReturnValue")
    private boolean showResult(String query) {
        Collections.sort(mAdapter.getObjects(), sortList(query));
        List<Contact> displayedCc = new ArrayList<>();
        for (Contact cc : mAdapter.getObjects()) {
            if (query.isEmpty() || stripAccents(cc.toString()).contains(stripAccents(query))) {
                displayedCc.add(cc);
            }
        }
        list.setAdapter(new ContactAdapter(this, displayedCc, false));
        return true;
    }

    private Comparator<Contact> sortList(final String s) {
        return new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
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


    private void fillContacts() {

        mContacts.clear();

        List<String> phones = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.
                                    getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).
                                    replaceAll(" ", "");
                            if (!phones.contains(phoneNo)) {
                                phones.add(phoneNo);
                                Contact c = new Contact(name, phoneNo, false);
                                mContacts.add(c);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    pCur.close();
                }
            }
            cur.close();
        }
        Collections.sort(mContacts);
        mAdapter.notifyDataSetChanged();
    }

}
