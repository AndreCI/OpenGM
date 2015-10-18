package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_ABOUT;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_FIRSTNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_GROUPS;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_LASTNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PHONENUMBER;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_USERNAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

/**
 * This class represents a member of group : basically this is a user with a username and roles
 * but we do not download its list of groups (otherwise we may end up with downloading all the groups)
 * just keep it so we may still be able to add it to the group or remove it.
 */
public final class GroupMember {

    private final String mId;
    private final List<String> mRoles;

    // Only needed when you add or remove someone from a group
    private final List<String> mGroups;

    private final String mUsername;
    private final String mFirstName;
    private final String mLastName;
    private final String mPhoneNumber;
    private final String mAboutUser;
    private final Bitmap mPicture;

    private String mNickname;

    public GroupMember(String id, String username, String firstname, String lastname, String surname, String phoneNumber, String about, Bitmap bitmap, List<String> roles, List<String> groups) {
        this.mId = id;
        this.mUsername = username;
        this.mFirstName = firstname;
        this.mLastName = lastname;
        this.mNickname = surname;
        this.mPhoneNumber = phoneNumber;
        this.mAboutUser = about;
        this.mPicture = bitmap;
        this.mRoles = new ArrayList<>(roles);
        this.mGroups = new ArrayList<>(groups);
    }

    /**
     * Getter for the id of the member
     *
     * @return the id associated with this member
     */
    public String getId() {
        return mId;
    }

    /**
     * Getter for the username of the member
     *
     * @return the username associated with this member
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Getter for the nickname of the member in this group
     *
     * @return the nickname associated with this member
     */
    public String getNickname() {
        return mNickname;
    }

    /**
     * Getter for the first name of the member
     *
     * @return the first name associated with this member
     */
    public String getFirstname() {
        return mFirstName;
    }

    /**
     * Getter for the last name of the member
     *
     * @return the last name associated with this member
     */
    public String getLastname() {
        return mLastName;
    }

    /**
     * Getter for the phone number of the member
     *
     * @return the phone number associated with this member
     */
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    /**
     * Getter for the description of the member
     *
     * @return the description associated with this member
     */
    public String getAbout() {
        return mAboutUser;
    }

    /**
     * Getter for the roles of the member in this group
     *
     * @return the roles associated with this member
     */
    public List<String> getRoles() {
        return Collections.unmodifiableList(mRoles);
    }

    /**
     * Getter for the profile picture of the member in this group
     *
     * @return the profile picture associated with this member
     */
    public Bitmap getPicture() {
        return mPicture;
    }

    /**
     * Setter for the nickname of the member in this group
     *
     * @param nickname the new nickname of the member (updated by the caller on the server)
     */
    public void setNickname(String nickname) {
        this.mNickname = nickname;
    }

    /**
     * Setter to add the member to a new group
     *
     * @param groupId the id of the group that the member will belong to
     */
    public void addToGroup(String groupId) {
        if (!mGroups.contains(groupId)) {
            mGroups.add(groupId);
            try {
                updateToServer();
            } catch (PFException e) {
                mGroups.remove(groupId);
                // TODO : what to do?
            }
        }
    }

    /**
     * Setter to remove the member to a new group
     *
     * @param groupId the id of the group that the member will be deleted from
     */
    public void removeFromGroup(String groupId) {
        if (mGroups.contains(groupId)) {
            mGroups.remove(groupId);
            try {
                updateToServer();
            } catch (PFException e) {
                mGroups.add(groupId);
                // TODO : what to do?
            }
        } else {
            // TODO : what to do?
        }
    }

    /**
     * Setter to add a role to the member
     *
     * @param role another role  that will be associated with this member
     */
    public void addRole(String role) {
        if (!mRoles.contains(role)) {
            mRoles.add(role);
        }
    }

    /**
     * Setter to remove a role to the member
     *
     * @param role a role that will be deleted from the member's role
     */
    public void removeRole(String role) {
        if (mRoles.contains(role)) {
            mRoles.remove(role);
        } else {
            // TODO : what to do?
        }
    }

    /**
     * Private method that will update the member on the server : can only updated the list of groups
     * ths user belong to (eg. if you are a moderator of a group and you want to delete a member you
     * just need to update this entry not the other ones)
     */
    private void updateToServer() throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PFConstants.USER_TABLE_NAME);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        JSONArray array = new JSONArray();
                        for (String groupId : mGroups) {
                            array.put(groupId);
                        }
                        object.put(USER_ENTRY_GROUPS, array);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
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

    public static final class Builder implements PFImageInterface {

        private final String mId;
        private final List<String> mRoles;
        private final List<String> mGroups;

        private String mUsername;
        private String mFirstName;
        private String mLastName;
        private String mNickname;
        private String mPhoneNumber;
        private String mAboutUser;
        private Bitmap mPicture;

        /**
         * The constructor for a new member that does not have any
         * nickname or role (default are respectively the username and an empty array)
         *
         * @param id the id of the member that we will retrieve information from
         */
        public Builder(String id) {
            this(id, "", new String[0]);
        }

        /**
         * The complete constructor of a GroupMember.Builder
         *
         * @param id       the id of the member that we will retrieve information from
         * @param nickname the nickname that will be associated to this member
         * @param roles    the roles that are attributed ot this user
         */
        public Builder(String id, String nickname, String[] roles) {
            this.mId = id;
            this.mNickname = nickname;
            this.mRoles = new ArrayList<>(Arrays.asList(roles));
            this.mGroups = new ArrayList<>();
        }

        /**
         * Setter for the username of the member we are building
         *
         * @param username the new username of this member
         */
        private void setUsername(String username) {
            this.mUsername = username;
        }

        /**
         * Setter for the first name of the member we are building
         *
         * @param firstName the new first name of this member
         */
        private void setFirstName(String firstName) {
            this.mFirstName = firstName;
        }

        /**
         * Setter for the last name of the member we are building
         *
         * @param lastName the new last name of this member
         */
        private void setLastName(String lastName) {
            this.mLastName = lastName;
        }

        /**
         * Setter for the phone number of the member we are building
         *
         * @param phoneNumber the new phone number of this member
         */
        private void setPhoneNumber(String phoneNumber) {
            this.mPhoneNumber = phoneNumber;
        }

        /**
         * Setter for the description of the member we are building
         *
         * @param about the new description of this member
         */
        private void setAbout(String about) {
            this.mAboutUser = about;
        }

        /**
         * Setter for the profile picture of the member we are building
         *
         * @param picture the new profile picture of this member
         */
        public void setImage(Bitmap picture) {
            this.mPicture = picture;
        }

        /**
         * Setter for the list of groups this member belongs to
         *
         * @param groupsArray A Json array containing the ids of the groups this member belongs to.
         * @throws PFException If something when wrong while getting informations from the json array or
         *                     if the type of the information is not correct
         */
        private void setGroups(JSONArray groupsArray) throws PFException {
            if (groupsArray != null) {
                for (int i = 0; i < groupsArray.length(); i++) {
                    try {
                        mGroups.add((String) groupsArray.get(i));
                    } catch (JSONException | ClassCastException e) {
                        throw new PFException();
                    }
                }
            }
        }

        /**
         * A method that retrieves all the informations associated to this member with an id.
         *
         * @throws PFException If something went wrong while communicating with the server,
         */
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
                    setGroups(object.getJSONArray(USER_ENTRY_GROUPS));
                    retrieveFileFromServer(object, USER_ENTRY_PICTURE, this);
                } else {
                    throw new PFException("Parse query for id " + userId + " failed");
                }
            } catch (ParseException e) {
                throw new PFException("Parse query for id " + userId + " failed");
            }
        }

        /**
         * Builds a new Group member with all its attributes.
         *
         * @return a new GroupMember corresponding to the object we were building
         * @throws PFException If something went wrong while retrieving information online
         */
        public GroupMember build() throws PFException {
            retrieveFromServer();
            if (mNickname == null || mNickname.isEmpty()) {
                mNickname = mUsername;
            }
            return new GroupMember(mId, mUsername, mFirstName, mLastName, mNickname, mPhoneNumber, mAboutUser, mPicture, mRoles, mGroups);
        }
    }
}
