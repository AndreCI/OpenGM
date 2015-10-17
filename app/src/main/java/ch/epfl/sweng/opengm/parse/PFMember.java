package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.opengm.identification.ImageOverview;

import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_ABOUT;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_FIRST_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_GROUPS;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_LAST_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_PHONE_NUMBER;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_TABLE_USERNAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.objectToString;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

public class PFMember {

    private final String mId;
    private final List<String> mRoles;

    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private String mNickname;
    private String mPhoneNumber;
    private String mAboutUser;
    private Bitmap mPicture;

    public PFMember(String id, String username, String firstname, String lastname, String surname, String phoneNumber, String about, Bitmap bitmap, List<String> roles) {
        this.mId = id;
        this.mUsername = username;
        this.mFirstName = firstname;
        this.mLastName = lastname;
        this.mNickname = surname;
        this.mPhoneNumber = phoneNumber;
        this.mAboutUser = about;
        this.mPicture = bitmap;
        this.mRoles = new ArrayList<>(roles);
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


    public static final class Builder implements ImageOverview {

        private final String mId;
        private final List<String> mRoles;

        private String mUsername;
        private String mFirstName;
        private String mLastName;
        private String mNickname;
        private String mPhoneNumber;
        private String mAboutUser;
        private Bitmap mPicture;

        public Builder(String id, String nickname, String[] roles) {
            this.mId = id;
            this.mNickname = nickname;
            this.mRoles = new ArrayList<>(Arrays.asList(roles));
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
                    setUsername(object.getString(USER_TABLE_USERNAME));
                    setFirstName(object.getString(USER_TABLE_FIRST_NAME));
                    setLastName(object.getString(USER_TABLE_LAST_NAME));
                    setPhoneNumber(object.getString(USER_TABLE_PHONE_NUMBER));
                    setAbout(object.getString(USER_TABLE_ABOUT));
                    retrieveFileFromServer(object, USER_TABLE_PICTURE, this);
                } else {
                    throw new PFException("Parse query for id " + userId + " failed");
                }
            } catch (ParseException e) {
                throw new PFException("Parse query for id " + userId + " failed");
            }
        }

        public PFMember build() throws PFException {
            retrieveFromServer();
            return new PFMember(mId, mUsername, mFirstName, mLastName, mNickname, mPhoneNumber, mAboutUser, mPicture, mRoles);
        }
    }
}
