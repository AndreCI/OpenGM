package ch.epfl.sweng.opengm.identification.contacts;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

import static ch.epfl.sweng.opengm.utils.Utils.stripAccents;

public class AppContactsActivity extends AppCompatActivity {

    private ContactAdapter mAdapter;
    private ListView list;

    private final HashMap<String, PFMember> mMembers = new HashMap<>();
    private final HashMap<String, List<PFGroup>> mGroups = new HashMap<>();

    private final List<Contact> mContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_contacts);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        list = (ListView) findViewById(R.id.app_contacts_list);

        fillContacts();

        mAdapter = new ContactAdapter(this, R.layout.item_contact, mContacts, true);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Contact cc = mAdapter.getObjects().get(position);
            }
        });


    }

    public void showPhoneContact(View v) {
        startActivity(new Intent(this, PhoneContactsActivity.class));
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

    private boolean showResult(String query) {
        Collections.sort(mAdapter.getObjects(), sortList(query));
        List<Contact> displayedCc = new ArrayList<>();
        for (Contact cc : mAdapter.getObjects()) {
            if (query.isEmpty() || stripAccents(cc.toString()).contains(stripAccents(query))) {
                displayedCc.add(cc);
            }
        }
        list.setAdapter(new ContactAdapter(this, R.layout.item_contact, displayedCc, true));
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

        PFUser currentUser = OpenGMApplication.getCurrentUser();

        for (PFGroup group : currentUser.getGroups()) {
            for (Map.Entry<String, PFMember> member : group.getMembers().entrySet()) {
                mMembers.put(member.getKey(), member.getValue());
                if (!mGroups.containsKey(member.getKey())) {
                    mGroups.put(member.getKey(), new ArrayList<PFGroup>());
                }
                mGroups.get(member.getKey()).add(group);
            }
        }

        for (PFMember member : mMembers.values()) {
            mContacts.add(new Contact(member));
        }

        Collections.sort(mContacts);
    }

}
