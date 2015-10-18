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

/**
 * This class represents a user which extends PFEntity since it
 * is linked to a table on the server
 */
public final class PFUser extends PFEntity {

    private final static String PARSE_TABLE_USER = PFConstants.USER_TABLE_NAME;

    private final List<PFGroup> mGroups;

    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;
    private String mAboutUser;
    private Bitmap mPicture;

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

    /**
     * Getter for the username of the user
     *
     * @return the username of the user
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Getter for the first name of the user
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Getter for the last name of the user
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Getter for the phone number of the user
     *
     * @return the phone number of the user
     */
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    /**
     * Getter for the list of groups of the user
     *
     * @return the list of groups of the user
     */
    public List<PFGroup> getGroups() {
        return Collections.unmodifiableList(mGroups);
    }

    /**
     * Getter for the description of the user
     *
     * @return the description of the user
     */
    public String getAboutUser() {
        return mAboutUser;
    }

    /**
     * Getter for the picture of the user
     *
     * @return the picture of the user
     */
    public Bitmap getPicture() {
        return mPicture;
    }

    /**
     * Add the current user to a group given its id
     *
     * @param groupId The id of the group whose user wil be added
     */
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

    /**
     * Remove the current user to a group given its id
     *
     * @param groupId The id of the group whose user wil be removed
     */
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

    /**
     * Setter for the username of the current user
     *
     * @param username The new username of the current user
     */
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

    /**
     * Setter for the first name of the current user
     *
     * @param firstname The new first name of the current user
     */
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

    /**
     * Setter for the last name of the current user
     *
     * @param lastname The new last name of the current user
     */
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

    /**
     * Setter for the phone number of the current user
     *
     * @param phoneNumber The new phone number of the current user
     */
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

    /**
     * Setter for the description of the current user
     *
     * @param aboutUser The new description of the current user
     */
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

    /**
     * Setter for the profile picture of the current user
     *
     * @param picture The new picture of the current user
     */
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

    /**
     * Returns the id of the group given as parameter in the list of groups
     *
     * @param groupId The id of the group we would like to know the index
     * @return The index of ths group in the list or -1 if the group is not in the list
     */
    private int getGroupIdx(String groupId) {
        for (int i = 0; i < mGroups.size(); i++) {
            if (mGroups.get(i).getId().equals(groupId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns true if the user belongs to the group
     *
     * @param groupId The id of the group we would like to know the index
     * @return True if the user belongs to this group, false otherwise
     */
    private boolean belongToGroup(String groupId) {
        for (PFGroup g : mGroups) {
            if (g.getId().equals(groupId)) {
                return true;
            }
        }
        return false;
    }

    public static final class Builder extends PFEntity.Builder implements PFImageInterface {

        private String mUsername;
        private String mFirstName;
        private String mLastName;
        private String mPhoneNumber;
        private String mAboutUser;
        private Bitmap mPicture;
        private final List<String> mGroups;

        /**
         * The only constructor for building a user
         *
         * @param id The id of the user we would like to retrieve information
         */
        public Builder(String id) {
            super(id);
            this.mGroups = new ArrayList<>();
        }

        /**
         * Setter for the username of the user we are building
         *
         * @param username The new username of the current user
         */
        private void setUsername(String username) {
            this.mUsername = username;
        }

        /**
         * Setter for the first name of the user we are building
         *
         * @param firstName The new first name of the current user
         */
        private void setFirstName(String firstName) {
            this.mFirstName = firstName;
        }

        /**
         * Setter for the last name of the user we are building
         *
         * @param lastName The new last name of the current user
         */
        private void setLastName(String lastName) {
            this.mLastName = lastName;
        }

        /**
         * Setter for the phone number of the user we are building
         *
         * @param phoneNumber The new phone number of the current user
         */
        private void setPhoneNumber(String phoneNumber) {
            this.mPhoneNumber = phoneNumber;
        }

        /**
         * Setter for the description of the user we are building
         *
         * @param about The new description of the current user
         */
        private void setAbout(String about) {
            this.mAboutUser = about;
        }

        /**
         * Setter for the profile picture of the user we are building
         *
         * @param image The new picture of the current user
         */
        public void setImage(Bitmap image) {
            this.mPicture = image;
        }

        /**
         * Add the user we are building to a group given its id
         *
         * @param group The id of the group whose user wil be added
         */
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

        /**
         * Builds a new User with all its attributes.
         *
         * @return a new user corresponding to the object we were building
         * @throws PFException If something went wrong while retrieving information online
         */
        public PFUser build() throws PFException {
            retrieveFromServer();
            return new PFUser(mId, mUsername, mFirstName, mLastName, mPhoneNumber, mAboutUser, mPicture, mGroups);
        }

    }
}