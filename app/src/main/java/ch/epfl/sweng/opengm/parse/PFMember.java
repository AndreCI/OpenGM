package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;

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
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants._USER_TABLE_EMAIL;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

/**
 * This class represents a member of group : basically this is a user with a username and roles
 * but we do not download its list of groups (otherwise we may end up with downloading all the groups)
 * just keep it so we may still be able to add it to the group or remove it.
 */
public final class PFMember extends PFEntity {

    private final List<String> mRoles;

    // Only needed when you add or remove someone from a group
    private final List<String> mGroups;

    private final String mUsername;
    private final String mFirstName;
    private final String mLastName;
    private final String mEmail;
    private final String mPhoneNumber;
    private final String mAboutUser;
    private final Bitmap mPicture;

    private String mNickname;

    private PFMember(String id, String username, String firstName, String lastName, String nickname, String email, String phoneNumber, String about, Bitmap bitmap, List<String> roles, List<String> groups) {
        super(id, USER_TABLE_NAME);
        this.mUsername = username;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mNickname = nickname;
        this.mEmail = email;
        this.mPhoneNumber = phoneNumber;
        this.mAboutUser = about;
        this.mPicture = bitmap;
        this.mRoles = new ArrayList<>(roles);
        this.mGroups = new ArrayList<>(groups);
    }

    @Override
    protected void updateToServer(String entry) throws PFException {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_TABLE_NAME);
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
     * Getter for the email of the member
     *
     * @return the email associated with this member
     */
    public String getEmail() {
        return mEmail;
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
                updateToServer("");
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
                updateToServer("");
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


    public static PFMember fetchExistingMember(String id, String nickName, String[] roles) throws PFException {
        if (id == null) {
            throw new PFException();
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

                ParseQuery<ParseObject> mailQuery = ParseQuery.getQuery(PFConstants._USER_TABLE_NAME);
                mailQuery.whereEqualTo(PFConstants.USER_ENTRY_USERID, id);

                ParseObject mailObject = query.getFirst();

                String email = (mailObject == null) ? "" : mailObject.getString(_USER_TABLE_EMAIL);

                Bitmap[] picture = {null};
                retrieveFileFromServer(object, USER_ENTRY_PICTURE, picture);
                String[] groupsArray = convertFromJSONArray(object.getJSONArray(USER_ENTRY_GROUPS));
                List<String> groups = new ArrayList<>(Arrays.asList(groupsArray));
                return new PFMember(id, username, firstName, lastName, nickName == null ? username : nickName, email, phoneNumber, description, picture[0], Arrays.asList(roles), groups);
            } else {
                throw new PFException("Parse query for id " + id + " failed");
            }
        } catch (ParseException e) {
            throw new PFException("Parse query for id " + id + " failed");
        }
    }

    /**
     * Fetches an existing user from the server and returns the object as a PFUser
     *
     * @param id The id of the user we are looking for
     * @return The user that corresponds to the given id
     * @throws PFException If something wrong happened with the server
     */
    public static PFMember fetchExistingMember(String id) throws PFException {
        return fetchExistingMember(id, null, new String[0]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public String getName() {
        return getLastname() + " - " + getFirstname();
    }
}
