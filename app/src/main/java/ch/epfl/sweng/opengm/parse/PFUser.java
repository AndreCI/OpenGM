package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_FIRST_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_GROUPS;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_LAST_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_PHONE_NUMBER;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_USERNAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.objectToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.objectToString;

public class PFUser extends PFEntity {

    private final static String PARSE_TABLE_USER = PFConstants.USER_TABLE_NAME;

    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;
    private List<PFGroup> mGroups;

    private PFUser(String id, String username, String firstname, String lastname, String phoneNumber, List<String> groups) {
        super(id, PARSE_TABLE_USER);
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is null or empty");
        }
        this.mUsername = username;
        if (firstname == null || firstname.isEmpty()) {
            throw new IllegalArgumentException("Firstname is null or empty");
        }
        this.mFirstName = firstname;
        if (lastname == null || lastname.isEmpty()) {
            throw new IllegalArgumentException("Lastname is null or empty");
        }
        this.mLastName = lastname;
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone number is null or empty");
        }
        this.mPhoneNumber = phoneNumber;
        if (groups == null || groups.isEmpty()) {
            throw new IllegalArgumentException("List of groups is null or empty");
        }
        this.mGroups = new ArrayList<>();
    }

    public PFUser(String id, String username, String firstname, String lastname, String phoneNumber) {
        this(id, username, firstname, lastname, phoneNumber, new ArrayList<String>());
    }

    @Override
    public void updateToServer() throws PFException {

    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public List<PFUser> getmGroups() {
        return Collections.emptyList();
    }

    public boolean addToAGroup(String group) {
        return true;
    }

    public boolean removeFromGroup(String group) {
        // TODO update Parse db
        return mGroups.remove(group);
    }


    public static final class Builder extends PFEntity.Builder {

        private String mUsername;
        private String mFirstName;
        private String mLastName;
        private String mPhoneNumber;
        private String mAboutUser;
        private Bitmap mPicture;
        private final List<String> mGroups;

        public Builder() {
            this(null);
        }

        public Builder(String id) {
            super(id);
            this.mGroups = new ArrayList<>();
        }

        private void setUsername(String username) {
            this.mUsername = username;
        }

        private void setFirstName(String firstName) {
            this.mFirstName = firstName;
        }

        private void setLastName(String lastName) {
            this.mLastName = lastName;
        }

        private void setPhoneNumber(String phoneNumber) {
            this.mPhoneNumber = phoneNumber;
        }

        private void addToAGroup(String group) {
            // TODO update Parse db
            mGroups.add(group);
        }

        @Override
        public void retrieveFromServer() throws PFException {
            final String userId = (mId == null ? ParseUser.getCurrentUser().getObjectId() : mId);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
            query.whereEqualTo(PFConstants.USER_TABLE_USER_ID, userId);
            try {
                ParseObject object = query.getFirst();
                if (object != null) {
                    setUsername(objectToString(object.get(USER_TABLE_USERNAME)));
                    setFirstName(objectToString(object.get(USER_TABLE_FIRST_NAME)));
                    setLastName(objectToString(object.get(USER_TABLE_LAST_NAME)));
                    setPhoneNumber(objectToString(object.get(USER_TABLE_PHONE_NUMBER)));

                    ParseFile fileObject = (ParseFile) object
                            .get(USER_TABLE_PICTURE);
                    fileObject.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            mPicture = (e == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length));
                        }
                    });
                    Object[] groups = objectToArray(object.get(USER_TABLE_GROUPS));
                    for (Object o : groups) {
                        addToAGroup(objectToString(o));
                    }
                } else {
                    throw new PFException("Parse query for id " + userId + " failed");
                }
            } catch (ParseException e) {
                throw new PFException("Parse query for id " + userId + " failed");
            }
        }

        public PFUser build() {
            return new PFUser(mId, mUsername, mFirstName, mLastName, mPhoneNumber, mGroups);
        }

    }
}