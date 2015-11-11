package ch.epfl.sweng.opengm.identification.contacts;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import ch.epfl.sweng.opengm.parse.PFConstants;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFMember;

public class Contact implements Comparable<Contact> {

    private final String mName;
    private final String mPhoneNumber;
    private boolean mIsUsingTheApp;
    private PFMember mMember;

    public Contact(String name, String number) {
        mPhoneNumber = number;
        mName = name;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
        query.whereEqualTo(PFConstants.USER_ENTRY_PHONENUMBER, number);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null && parseObject != null) {
                    mIsUsingTheApp = true;
                    try {
                        mMember = PFMember.fetchExistingMember(parseObject.getString(PFConstants.USER_ENTRY_USERID));
                    } catch (PFException e1) {
                        e1.printStackTrace();
                        mIsUsingTheApp = false;
                    }
                } else {
                    mIsUsingTheApp = false;
                }
            }
        });

    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public boolean isIsUsingTheApp() {
        return mIsUsingTheApp;
    }

    public PFMember getMember() {
        return mMember;
    }

    @Override
    public int compareTo(Contact another) {
        return mName.compareTo(another.mName);
    }

    @Override
    public String toString() {
        return mName + " : " + mPhoneNumber + " " + isIsUsingTheApp();
    }
}
