package ch.epfl.sweng.opengm.parse;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.sweng.opengm.parse.ParseConstants.USER_TABLE_FIRST_NAME;
import static ch.epfl.sweng.opengm.parse.ParseConstants.USER_TABLE_GROUPS;
import static ch.epfl.sweng.opengm.parse.ParseConstants.USER_TABLE_LAST_NAME;
import static ch.epfl.sweng.opengm.parse.ParseConstants.USER_TABLE_PHONE_NUMBER;
import static ch.epfl.sweng.opengm.parse.ParseConstants.USER_TABLE_USERNAME;
import static ch.epfl.sweng.opengm.parse.ParseUtils.objectToArray;
import static ch.epfl.sweng.opengm.parse.ParseUtils.objectToString;

public class User extends ParseEntity {

    private final static String PARSE_TABLE_USER = ParseConstants.USER_TABLE_NAME;

    private final String mUsername;
    private final String mFirstName;
    private final String mLastName;
    private final String mPhoneNumber;
    private final List<String> mGroups;

    private User(String id, String username, String firstname, String lastname, String phoneNumber, List<String> groups) {
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
        this.mGroups = new ArrayList<>(groups);
    }

    public User(String id, String username, String firstname, String lastname, String phoneNumber) {
        this(id, username, firstname, lastname, phoneNumber, new ArrayList<String>());
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

    public List<String> getmGroups() {
        return Collections.unmodifiableList(mGroups);
    }

    public boolean addToAGroup(String group) {
        return mGroups.add(group);
    }

    public boolean removeFromGroup(String group) {
        // TODO update Parse db
        return mGroups.remove(group);
    }

    public static final class Builder extends ParseEntity.Builder {

        private String mUsername;
        private String mFirstName;
        private String mLastName;
        private String mPhoneNumber;
        private final List<String> mGroups;

        public Builder() {
            this(null);
        }

        public Builder(String id) {
            super(id, PARSE_TABLE_USER);
            this.mGroups = new ArrayList<>();
        }

        public boolean setUsername(String username) {
            if (mUsername.equals(username)) {
                return true;
            }
            this.mUsername = username;
            return false;
        }

        public boolean setFirstName(String firstName) {
            if (mFirstName.equals(firstName)) {
                return true;
            }
            this.mFirstName = firstName;
            return false;
        }

        public boolean setLastName(String lastName) {
            if (mLastName.equals(lastName)) {
                return true;
            }
            this.mLastName = lastName;
            return false;
        }

        public boolean setPhoneNumber(String phoneNumber) {
            if (mPhoneNumber.equals(phoneNumber)) {
                return true;
            }
            this.mPhoneNumber = phoneNumber;
            return false;
        }

        public boolean addToAGroup(String group) {
            // TODO update Parse db
            return mGroups.add(group);
        }

        public boolean removeFromGroup(String group) {
            // TODO update Parse db
            return mGroups.remove(group);
        }

        @Override
        public void retrieveFromParse() throws ServerException {
            final String userId = (mId == null ? ParseUser.getCurrentUser().getObjectId() : mId);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.USER_TABLE_NAME);
            query.whereEqualTo(ParseConstants.USER_TABLE_USER_ID, userId);
            try {
                ParseObject object = query.getFirst();
                if (object != null) {
                    setUsername(objectToString(object.get(USER_TABLE_USERNAME)));
                    setFirstName(objectToString(object.get(USER_TABLE_FIRST_NAME)));
                    setLastName(objectToString(object.get(USER_TABLE_LAST_NAME)));
                    setPhoneNumber(objectToString(object.get(USER_TABLE_PHONE_NUMBER)));
                    Object[] groups = objectToArray(object.get(USER_TABLE_GROUPS));
                    for (Object o : groups) {
                        addToAGroup(objectToString(o));
                    }
                } else {
                    throw new ServerException("Parse query for id " + userId + " failed");
                }
            } catch (ParseException e) {
                throw new ServerException("Parse query for id " + userId + " failed");
            }
        }

        public User build() {
            return new User(mId, mUsername, mFirstName, mLastName, mPhoneNumber, mGroups);
        }

    }
}