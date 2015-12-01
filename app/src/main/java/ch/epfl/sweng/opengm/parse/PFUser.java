package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.os.Parcel;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_ABOUT;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_FIRSTNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_GROUPS;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_LASTNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PHONENUMBER;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_USERID;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_USERNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants._USER_TABLE_EMAIL;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;

/**
 * This class represents a user which extends PFEntity since it
 * is linked to a table on the server
 */
public final class PFUser extends PFEntity {

    private final static String PARSE_TABLE_USER = PFConstants.USER_TABLE_NAME;

    private List<PFGroup> mGroups;

    private List<String> mGroupsId;

    private String mEmail;
    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;
    private String mAboutUser;
    private Bitmap mPicture;

    public PFUser(Parcel in){
        super(in, PARSE_TABLE_USER);
    }

    private PFUser(String userId, Date date, String phoneNumber, String email, String username,
                   String firstName, String lastName, String aboutUser, Bitmap picture,
                   List<String> groups) throws PFException {

        super(userId, PARSE_TABLE_USER, date);
        this.mEmail = email;
        this.mUsername = username;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mPhoneNumber = phoneNumber;
        this.mAboutUser = aboutUser;
        this.mGroups = new ArrayList<>();
        this.mGroupsId = new ArrayList<>(groups);

        this.mPicture = picture;
    }


    public List<String> getGroupsIds() {
        return mGroupsId;
    }

    public PFGroup fetchGroupWithId(String groupId) throws PFException {
        PFGroup newGroup = PFGroup.fetchExistingGroup(groupId);
        mGroups.add(newGroup);
        return newGroup;
    }

    @Override
    public void reload() throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_USER);
        query.whereEqualTo(USER_ENTRY_USERID, getId());
        try {
            ParseObject object = query.getFirst();
            if (hasBeenModified(object)) {
                setLastModified(object);
                mUsername = object.getString(USER_ENTRY_USERNAME);
                mFirstName = object.getString(USER_ENTRY_FIRSTNAME);
                mLastName = object.getString(USER_ENTRY_LASTNAME);
                mPhoneNumber = object.getString(USER_ENTRY_PHONENUMBER);
                mAboutUser = object.getString(USER_ENTRY_ABOUT);

                ParseQuery<ParseUser> mailQuery = ParseUser.getQuery();

                try {
                    ParseObject mailObject = mailQuery.get(getId());
                    mEmail = mailObject.getString(_USER_TABLE_EMAIL);
                } catch (ParseException pe) {
                    // Do nothing
                }

                String[] groupsArray = convertFromJSONArray(object.getJSONArray(USER_ENTRY_GROUPS));
                List<String> groups = new ArrayList<>(Arrays.asList(groupsArray));

                for (String groupId : groups) {
                    if (!mGroupsId.contains(groupId)){
                        try {
                            mGroups.add(PFGroup.fetchExistingGroup(groupId));
                            mGroupsId.add(groupId);
                        } catch (PFException e) {
                            throw new PFException("Error while retrieving the existing group with id " + groupId);
                        }
                    }

                }
            }
        } catch (ParseException e) {
            throw new PFException(e);
        }
    }

    @Override
    public void updateToServer(final String entry) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_USER);
        query.whereEqualTo(USER_ENTRY_USERID, getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
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
                                JSONArray array = new JSONArray();
                                for(String groupId :mGroupsId){
                                    array.put(groupId);
                                }
                                object.put(USER_ENTRY_GROUPS, array);
                                break;
                            default:
                                return;
                        }
                        object.saveInBackground();
                    }
                }
            }
        });
    }

    /**
     * Getter for the email of the user
     *
     * @return the email of the user
     */
    public String getEmail() {
        return mEmail;
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
     * @param group The group whose user wil be added
     * @throws PFException If something went wrong while updating on the server
     */
    public void addToAGroup(PFGroup group) throws PFException {
        if (!belongToGroup(group.getId())) {
            mGroups.add(group);
            mGroupsId.add(group.getId());
            try {
                updateToServer(USER_ENTRY_GROUPS);
            } catch (PFException e) {
                mGroups.remove(group);
                mGroupsId.remove(group.getId());
                throw new PFException();
            }
        }
    }

    /**
     * Remove the current user to a group given its id
     *
     * @param groupId The id of the group whose user wil be removed
     * @throws PFException If something went wrong while updating on the server
     */
    public void removeFromGroup(String groupId) throws PFException {
        if (belongToGroup(groupId)) {
            PFGroup group = mGroups.get(getGroupIdx(groupId));
            group.removeUser(getId());
            mGroups.remove(group);
            mGroupsId.remove(group.getId());
            try {
                updateToServer(USER_ENTRY_GROUPS);
            } catch (PFException e) {
                group.addUserWithId(getId());
                mGroups.add(group);
                mGroupsId.add(group.getId());
                throw new PFException();
            }
        }
    }


    /**
     * Setter for the email of the current user
     *
     * @param email The new email of the current user
     * @throws PFException If something went wrong while updating on the server
     */
    public void setEmail(String email) throws PFException {
        if (checkArguments(email) && !email.equals(mEmail)) {
            try {
                ParseUser.getCurrentUser().setEmail(email);
                ParseUser.getCurrentUser().save();
                this.mEmail = email;
            } catch (ParseException e) {
                throw new PFException();
            }
        }
    }

    /**
     * Setter for the username of the current user
     *
     * @param username The new username of the current user
     * @throws PFException If something went wrong while updating on the server
     */
    public void setUsername(String username) throws PFException {
        if (checkArguments(username) && !username.equals(mUsername)) {
            String oldUsername = mUsername;
            this.mUsername = username;
            try {
                updateToServer(USER_ENTRY_USERNAME);
            } catch (PFException e) {
                this.mUsername = oldUsername;
                throw new PFException();
            }
        }
    }

    /**
     * Setter for the first name of the current user
     *
     * @param firstName The new first name of the current user
     * @throws PFException If something went wrong while updating on the server
     */
    public void setFirstName(String firstName) throws PFException {
        if (checkArguments(firstName) && !firstName.equals(mFirstName)) {
            String oldFirstName = mFirstName;
            this.mFirstName = firstName;
            try {
                updateToServer(USER_ENTRY_FIRSTNAME);
            } catch (PFException e) {
                this.mFirstName = oldFirstName;
                throw new PFException();
            }
        }
    }

    /**
     * Setter for the last name of the current user
     *
     * @param lastName The new last name of the current user
     * @throws PFException If something went wrong while updating on the server
     */
    public void setLastName(String lastName) throws PFException {
        if (checkArguments(lastName) && !lastName.equals(mLastName)) {
            String oldLastName = mFirstName;
            this.mLastName = lastName;
            try {
                updateToServer(USER_ENTRY_LASTNAME);
            } catch (PFException e) {
                this.mLastName = oldLastName;
                throw new PFException();
            }
        }
    }

    /**
     * Setter for the phone number of the current user
     *
     * @param phoneNumber The new phone number of the current user
     * @throws PFException If something went wrong while updating on the server
     */
    public void setPhoneNumber(String phoneNumber) throws PFException {
        if (checkArguments(phoneNumber) && !phoneNumber.equals(mPhoneNumber)) {
            String oldPhoneNumber = mPhoneNumber;
            this.mPhoneNumber = phoneNumber;
            try {
                updateToServer(USER_ENTRY_PHONENUMBER);
            } catch (PFException e) {
                this.mPhoneNumber = oldPhoneNumber;
                throw new PFException();
            }
        }
    }

    /**
     * Setter for the description of the current user
     *
     * @param aboutUser The new description of the current user
     * @throws PFException If something went wrong while updating on the server
     */
    public void setAboutUser(String aboutUser) throws PFException {
        if (checkArguments(aboutUser) && !aboutUser.equals(mAboutUser)) {
            String oldAboutUser = mAboutUser;
            this.mAboutUser = aboutUser;
            try {
                updateToServer(USER_ENTRY_ABOUT);
            } catch (PFException e) {
                this.mAboutUser = oldAboutUser;
                throw new PFException();
            }
        }
    }

    /**
     * Setter for the profile picture of the current user
     *
     * @param picture The new picture of the current user
     * @throws PFException If something went wrong while updating on the server
     */
    public void setPicture(Bitmap picture) throws PFException {
        if (!mPicture.equals(picture)) {
            Bitmap oldPicture = mPicture;
            this.mPicture = picture;
            try {
                updateToServer(USER_ENTRY_PICTURE);
            } catch (PFException e) {
                this.mPicture = oldPicture;
                throw new PFException();
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
        return mGroupsId.contains(groupId);
    }

    /**
     * Fetches an existing user from the server and returns the object as a PFUser
     *
     * @param id The id of the user we are looking for
     * @return The user that corresponds to the given id
     * @throws PFException If something wrong happened with the server
     */
    public static PFUser fetchExistingUser(String id) throws PFException {
        if (id == null) {
            throw new PFException("Id is null");
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
        query.whereEqualTo(PFConstants.USER_ENTRY_USERID, id);
        try {
            ParseObject object = query.getFirst();
            if (object != null) {
                String username = object.getString(USER_ENTRY_USERNAME);
                String firstName = object.getString(USER_ENTRY_FIRSTNAME);
                String lastName = object.getString(USER_ENTRY_LASTNAME);
                String phoneNumber = object.getString(USER_ENTRY_PHONENUMBER);
                String description = object.getString(USER_ENTRY_ABOUT);

                ParseObject mailObject = null;

                ParseQuery<ParseUser> mailQuery = ParseUser.getQuery();

                try {
                    mailObject = mailQuery.get(id);
                } catch (ParseException pe) {
                    // Do nothing
                }

                String email = (mailObject == null) ? "" : mailObject.getString(_USER_TABLE_EMAIL);

                String[] groupsArray = convertFromJSONArray(object.getJSONArray(USER_ENTRY_GROUPS));
                List<String> groups = (groupsArray == null ?
                        new ArrayList<String>() : new ArrayList<>(Arrays.asList(groupsArray)));
                return new PFUser(id, object.getUpdatedAt(), phoneNumber, email, username,
                        firstName, lastName, description, null, groups);
            } else {
                throw new PFException("Parse query for id " + id + " failed");
            }
        } catch (ParseException e) {
            throw new PFException("Parse query for id " + id + " failed");
        }
    }

    /**
     * Create a new user in the User table (should only be used after sign up)
     *
     * @param id        The given id of our user
     * @param username  The username of the user
     * @param firstName The first name of the user
     * @param lastName  The last name of the user
     * @return The new user that contains all the given parameters
     * @throws PFException If something wrong happened with the server
     */
    public static PFUser createNewUser(String id, String email, String phoneNumber,
                                       String username, String firstName, String lastName)
            throws PFException {
        ParseObject parseObject = new ParseObject(USER_TABLE_NAME);
        parseObject.put(USER_ENTRY_USERID, id);
        parseObject.put(USER_ENTRY_USERNAME, username);
        parseObject.put(USER_ENTRY_FIRSTNAME, firstName);
        parseObject.put(USER_ENTRY_LASTNAME, lastName);
        parseObject.put(USER_ENTRY_GROUPS, new JSONArray());
        parseObject.put(USER_ENTRY_PHONENUMBER, phoneNumber);
        parseObject.put(USER_ENTRY_ABOUT, "");
        try {
            parseObject.save();
            return new PFUser(id, parseObject.getUpdatedAt(), phoneNumber, email, username,
                    firstName, lastName, "", null, new ArrayList<String>());
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static final Creator<PFUser> CREATOR = new Creator<PFUser>() {
        @Override
        public PFUser createFromParcel(Parcel in) {
            return new PFUser(in);
        }

        @Override
        public PFUser[] newArray(int size) {
            return new PFUser[size];
        }
    };

}