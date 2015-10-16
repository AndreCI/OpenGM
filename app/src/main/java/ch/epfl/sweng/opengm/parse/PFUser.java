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

import ch.epfl.sweng.opengm.utils.Alert;

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
            PFGroup.Builder group = new PFGroup.Builder(s);
            mGroups.add(group.build());
        }
        this.mPicture = picture;
    }

    @Override
    public void updateToServer() throws PFException {

    }

    public String getUsername() {
        return mUsername;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public List<PFGroup> getGroups() {
        return Collections.unmodifiableList(mGroups);
    }

    public String getAboutUser() {
        return mAboutUser;
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    public void addToAGroup(String s) {
        if (belongToGroup(s)) {
            Alert.displayAlert("User already belongs to this group.");
        } else {
            // TODO update groups data
            PFGroup.Builder group = new PFGroup.Builder(s);
            mGroups.add(group.build());
            try {
                updateToServer();
            } catch (PFException e) {
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    public void removeFromGroup(String group) {
        if (!belongToGroup(group)) {
            Alert.displayAlert("User does not belong the group.");
        } else {
            // TODO update groups data
            PFGroup oldGroup = mGroups.get(getGroupIdx(group));
            mGroups.remove(getGroupIdx(group));
            try {
                updateToServer();
            } catch (PFException e) {
                mGroups.add(oldGroup);
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    public void setUsername(String username) {
        if (checkArguments(username, "User name") && !username.equals(mUsername)) {
            String oldUsername = mUsername;
            this.mUsername = username;
            try {
                updateToServer();
            } catch (PFException e) {
                this.mUsername = oldUsername;
                Alert.displayAlert("Error while updating the username to the server.");
            }
        }
    }

    public void setFirstName(String firstname) {
        if (checkArguments(firstname, "First name") && !firstname.equals(mFirstName)) {
            String oldFirstname = mFirstName;
            this.mFirstName = firstname;
            try {
                updateToServer();
            } catch (PFException e) {
                this.mFirstName = oldFirstname;
                Alert.displayAlert("Error while updating the first name to the server.");
            }
        }
    }

    public void setLastName(String lastname) {
        if (checkArguments(lastname, "Last name") && !lastname.equals(mLastName)) {
            String oldLastname = mFirstName;
            this.mLastName = lastname;
            try {
                updateToServer();
            } catch (PFException e) {
                this.mLastName = oldLastname;
                Alert.displayAlert("Error while updating the last name to the server.");
            }
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        if (checkArguments(phoneNumber, "Phone number") && !phoneNumber.equals(mPhoneNumber)) {
            String oldPhoneNumber = mPhoneNumber;
            this.mPhoneNumber = phoneNumber;
            try {
                updateToServer();
            } catch (PFException e) {
                this.mPhoneNumber = oldPhoneNumber;
                Alert.displayAlert("Error while updating the phone number to the server.");
            }
        }
    }

    public void setAboutUser(String aboutUser) {
        if (checkArguments(aboutUser, "User's description") && !aboutUser.equals(mAboutUser)) {
            String oldAboutUser = mAboutUser;
            this.mAboutUser = aboutUser;
            try {
                updateToServer();
            } catch (PFException e) {
                this.mAboutUser = oldAboutUser;
                Alert.displayAlert("Error while updating the phone number to the server.");
            }
        }
    }

    public void setPicture(Bitmap picture) {
        if (!mPicture.equals(picture)) {
            Bitmap oldPicture = mPicture;
            this.mPicture = picture;
            try {
                updateToServer();
            } catch (PFException e) {
                this.mPicture = oldPicture;
                Alert.displayAlert("Error while updating the picture to the server.");
            }
        }
    }

    private int getGroupIdx(String id) {
        for (int i = 0; i < mGroups.size(); i++) {
            if (mGroups.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private boolean belongToGroup(String id) {
        for (PFGroup g : mGroups) {
            if (g.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkArguments(String arg, String name) {
        if (arg == null || arg.isEmpty()) {
            Alert.displayAlert(name + " is null or empty.");
            return false;
        }
        return true;
    }

    private boolean checkNullArguments(String arg, String name) {
        if (arg == null) {
            Alert.displayAlert(name + " is null.");
            return false;
        }
        return true;
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