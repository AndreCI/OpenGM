package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.opengm.identification.ImageOverview;
import ch.epfl.sweng.opengm.utils.Alert;

import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_ABOUT;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_FIRSTNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_GROUPS;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_LASTNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PHONENUMBER;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_USERNAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkNullArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.listToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.objectToString;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

public class PFUser extends PFEntity {

    private final static String PARSE_TABLE_USER = PFConstants.USER_TABLE_NAME;

    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;
    private String mAboutUser;
    private Bitmap mPicture;
    private List<PFGroup> mGroups;

    private PFUser(String userId, String username, String firstname, String lastname, String phoneNumber, String aboutUser, Bitmap picture, List<String> groups) {
        super(userId, PARSE_TABLE_USER);
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
        for (String groupId : groups) {
            PFGroup.Builder group = new PFGroup.Builder(this, groupId, false);
            try {
                mGroups.add(group.build());
            } catch (PFException e) {
                // TODO : what to do?
            }
        }
        this.mPicture = picture;
    }

    @Override
    public void updateToServer(final String entry) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_USER);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        switch (entry) {
                            case USER_ENTRY_USERNAME:
                                object.put(USER_ENTRY_USERNAME, mUsername);
                                break;
                            case USER_ENTRY_FIRSTNAME:
                                object.put(USER_ENTRY_FIRSTNAME, mFirstName);
                                break;
                            case USER_ENTRY_LASTNAME:
                                object.put(USER_ENTRY_LASTNAME, mLastName);
                                break;
                            case USER_ENTRY_PHONENUMBER:
                                object.put(USER_ENTRY_PHONENUMBER, mPhoneNumber);
                                break;
                            case USER_ENTRY_ABOUT:
                                object.put(USER_ENTRY_ABOUT, mAboutUser);
                                break;
                            case USER_ENTRY_PICTURE:
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] image = stream.toByteArray();
                                ParseFile file = new ParseFile(String.format("user%s.png", getId()), image);
                                file.saveInBackground();
                                object.put(USER_ENTRY_PICTURE, mPicture);
                                break;
                            case USER_ENTRY_GROUPS:
                                object.put(USER_ENTRY_GROUPS, listToArray(mGroups));
                                break;
                            default:
                                return;
                        }
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    // FIXME: done() method canno't throw exceptions, but we want THIS method
                                    // FIXME: updateToServer() to throw a PFException --> That we can catch e.g in the setters.
                                    // throw new ParseException("No object for the selected id.");
                                }
                            }
                        });
                    } else {
                        // throw new ParseException("No object for the selected id.");
                    }
                } else {
                    // throw new ParseException("Error while sending the request to the server");
                }
            }
        });
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

    public void addToAGroup(String groupId) {
        if (belongToGroup(groupId)) {
            Alert.displayAlert("User already belongs to this group.");
        } else {
            try {
                PFGroup group = new PFGroup.Builder(this, groupId, false).build();
                group.addUser(getId());
                mGroups.add(group);
                try {
                    updateToServer(USER_ENTRY_GROUPS);
                } catch (PFException e) {
                    Alert.displayAlert("Error while updating the user's groups to the server.");
                }
            } catch (PFException e) {
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    public void removeFromGroup(String groupId) {
        if (!belongToGroup(groupId)) {
            Alert.displayAlert("User does not belong the group.");
        } else {
            PFGroup group = mGroups.get(getGroupIdx(groupId));
            group.removeUser(getId());
            mGroups.remove(group);
            try {
                updateToServer(USER_ENTRY_GROUPS);
            } catch (PFException e) {
                mGroups.add(group);
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    public void setUsername(String username) {
        if (checkArguments(username, "User name") && !username.equals(mUsername)) {
            String oldUsername = mUsername;
            this.mUsername = username;
            try {
                updateToServer(USER_ENTRY_USERNAME);
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
                updateToServer(USER_ENTRY_FIRSTNAME);
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
                updateToServer(USER_ENTRY_LASTNAME);
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
                updateToServer(USER_ENTRY_PHONENUMBER);
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
                updateToServer(USER_ENTRY_ABOUT);
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
                updateToServer(USER_ENTRY_PICTURE);
            } catch (PFException e) {
                this.mPicture = oldPicture;
                Alert.displayAlert("Error while updating the picture to the server.");
            }
        }
    }

    private int getGroupIdx(String groupId) {
        for (int i = 0; i < mGroups.size(); i++) {
            if (mGroups.get(i).getId().equals(groupId)) {
                return i;
            }
        }
        return -1;
    }

    private boolean belongToGroup(String groupId) {
        for (PFGroup g : mGroups) {
            if (g.getId().equals(groupId)) {
                return true;
            }
        }
        return false;
    }

    public static final class Builder extends PFEntity.Builder implements ImageOverview {

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

        public void setImage(Bitmap image) {
            this.mPicture = image;
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
            query.whereEqualTo(PFConstants.USER_ENTRY_USERID, userId);
            try {
                ParseObject object = query.getFirst();
                if (object != null) {
                    setUsername(object.getString(USER_ENTRY_USERNAME));
                    setFirstName(object.getString(USER_ENTRY_FIRSTNAME));
                    setLastName(object.getString(USER_ENTRY_LASTNAME));
                    setPhoneNumber(object.getString(USER_ENTRY_PHONENUMBER));
                    setAbout(object.getString(USER_ENTRY_ABOUT));
                    retrieveFileFromServer(object, USER_ENTRY_PICTURE, this);
                    String[] groups = convertFromJSONArray(object.getJSONArray(USER_ENTRY_GROUPS));
                    for (String groupId : groups) {
                        addToAGroup(objectToString(groupId));
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