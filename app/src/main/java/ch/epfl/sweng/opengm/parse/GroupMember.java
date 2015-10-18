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

import ch.epfl.sweng.opengm.identification.ImageOverview;

import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_ABOUT;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_FIRSTNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_GROUPS;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_LASTNAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PHONENUMBER;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_USERNAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

public class GroupMember {

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

    public String getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getSurname() {
        return mNickname;
    }

    public String getFirstname() {
        return mFirstName;
    }

    public String getLastname() {
        return mLastName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getAbout() {
        return mAboutUser;
    }

    public List<String> getRoles() {
        return Collections.unmodifiableList(mRoles);
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    public void setmNickname(String nickname) {
        this.mNickname = nickname;
    }

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

    public void addRole(String role) {
        if (!mRoles.contains(role)) {
            mRoles.add(role);
        }
    }

    public void removeRole(String role) {
        if (mRoles.contains(role)) {
            mRoles.remove(role);
        } else {
            // TODO : what to do?
        }
    }

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

    public static final class Builder implements ImageOverview {

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

        public Builder(String id) {
            this(id, "", new String[0]);
        }

        public Builder(String id, String nickname, String[] roles) {
            this.mId = id;
            this.mNickname = nickname;
            this.mRoles = new ArrayList<>(Arrays.asList(roles));
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

        public GroupMember build() throws PFException {
            retrieveFromServer();
            if (mNickname == null || mNickname.isEmpty()) {
                mNickname = mUsername;
            }
            return new GroupMember(mId, mUsername, mFirstName, mLastName, mNickname, mPhoneNumber, mAboutUser, mPicture, mRoles, mGroups);
        }
    }
}
