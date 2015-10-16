package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_FIRST_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_GROUPS;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_LAST_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_PHONE_NUMBER;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_USERNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_ABOUT;
import static ch.epfl.sweng.opengm.parse.PFUtils.objectToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.objectToString;

public class PFUser extends PFEntity {

    private final static String PARSE_TABLE_USER = PFConstants.USER_TABLE_NAME;

    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;
    private String mAboutUser;
    private Bitmap mPicture;
    private List<PFGroup> mGroups;


    private PFUser(String id, String username, String firstname, String lastname, String phoneNumber, String aboutUser, List<PFGroup> groups, Bitmap picture) {
        super(id, PARSE_TABLE_USER);
        checkArguments(username, "User name");
        this.mUsername = username;
        checkArguments(firstname, "First name");
        this.mFirstName = firstname;
        checkArguments(lastname, "Last name");
        this.mLastName = lastname;
        checkNullArguments(phoneNumber, "Phone number");
        this.mPhoneNumber = phoneNumber;
        checkNullArguments(aboutUser, "User's description");
        this.mAboutUser = aboutUser;
        checkNullArguments(aboutUser, "User's groups");
        this.mGroups = new ArrayList<>(groups);
        this.mPicture = picture;
    }


    private PFUser(String id, String username, String firstname, String lastname, String phoneNumber, String aboutUser, Bitmap picture, List<String> groups) {
        super(id, PARSE_TABLE_USER);
        checkArguments(username, "User name");
        this.mUsername = username;
        checkArguments(firstname, "First name");
        this.mFirstName = firstname;
        checkArguments(lastname, "Last name");
        this.mLastName = lastname;
        checkNullArguments(phoneNumber, "Phone number");
        this.mPhoneNumber = phoneNumber;
        checkNullArguments(aboutUser, "User's description");
        this.mAboutUser = aboutUser;
        checkNullArguments(aboutUser, "User's groups");
        this.mGroups = new ArrayList<>();
        for (String s : groups) {
            addToAGroup(s);
        }
        this.mPicture = picture;
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

    public String getmAboutUser() {
        return mAboutUser;
    }

    public Bitmap getmPicture() {
        return mPicture;
    }

    public boolean addToAGroup(String s) {
        if (belongToGroup(s)) {
            return false;
        }
        PFGroup.Builder group = new PFGroup.Builder(s);
        return mGroups.add(group.build());
    }

    public boolean removeFromGroup(String group) {
        if (!belongToGroup(group)) {
            throw new IllegalArgumentException("User does not belong the group.")
        }
        return mGroups.remove(group);
    }

    public void setUsername(String username) {
        checkArguments(username, "User name");
        this.mUsername = username;
    }

    public void setFirstName(String firstname) {
        checkArguments(firstname, "First name");
        this.mFirstName = firstname;
    }

    public void setLastName(String lastname) {
        checkArguments(lastname, "Last name");
        this.mLastName = lastname;
    }

    public void setPhoneNumber(String phoneNumber) {
        checkNullArguments(phoneNumber, "Phone number");
        this.mPhoneNumber = phoneNumber;
    }

    public void setmAboutUser(String aboutUser) {
        checkNullArguments(aboutUser, "User's description");
        this.mAboutUser = aboutUser;
    }

    public void setmPicture(Bitmap mPicture) {
        this.mPicture = mPicture;
    }

    private boolean belongToGroup(String id) {
        for (PFGroup g : mGroups) {
            if (g.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void checkArguments(String arg, String name) throws IllegalArgumentException {
        if (arg == null || arg.isEmpty()) {
            throw new IllegalArgumentException(name + " is null or empty.");
        }
    }

    private void checkNullArguments(String arg, String name) throws IllegalArgumentException {
        if (arg == null) {
            throw new IllegalArgumentException(name + " is null.");
        }
    }

    public static final class Builder extends PFEntity.Builder {

        private String mUsername;
        private String mFirstName;
        private String mLastName;
        private String mPhoneNumber;
        private String mAboutUser;
        private Bitmap mPicture;
        private final List<String> mGroups;

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

        private void setAbout(String about) {
            this.mAboutUser = about;
        }

        private void addToAGroup(String group) {
            mGroups.add(group);
        }

        @Override
        protected void retrieveFromServer() throws PFException {
            if (mId == null) {
                throw new PFException();
            }
            final String userId = mId;
            ParseQuery<ParseObject> query = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
            query.whereEqualTo(PFConstants.USER_TABLE_USER_ID, userId);
            try {
                ParseObject object = query.getFirst();
                if (object != null) {
                    setUsername(objectToString(object.get(USER_TABLE_USERNAME)));
                    setFirstName(objectToString(object.get(USER_TABLE_FIRST_NAME)));
                    setLastName(objectToString(object.get(USER_TABLE_LAST_NAME)));
                    setPhoneNumber(objectToString(object.get(USER_TABLE_PHONE_NUMBER)));
                    setAbout(objectToString(object.get(USER_TABLE_ABOUT)));
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

        public PFUser build() throws PFException {
            retrieveFromServer();
            return new PFUser(mId, mUsername, mFirstName, mLastName, mPhoneNumber, mAboutUser, mPicture, mGroups);
        }

    }
}