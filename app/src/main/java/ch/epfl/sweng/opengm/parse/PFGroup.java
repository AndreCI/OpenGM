package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
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

import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_EVENTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_ISPRIVATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_ROLES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_SURNAMES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_TITLE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_USERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkNullArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.listToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.objectToArray;

public class PFGroup extends PFEntity {

    private final static String PARSE_TABLE_GROUP = PFConstants.GROUP_TABLE_NAME;

    private final static int IDX_NAME = 0;
    private final static int IDX_USERS = 1;
    private final static int IDX_SURNAMES = 2;
    private final static int IDX_ROLES = 3;
    private final static int IDX_EVENTS = 4;
    private final static int IDX_DESCRIPTION = 5;
    private final static int IDX_PRIVACY = 6;
    private final static int IDX_PICTURE = 7;


    private final List<PFUser> mUsers;
    private final List<String> mSurnames;
    private final List<String[]> mRoles;
    private final List<PFEvent> mEvents;

    private String mName;
    private String mDescription;
    private boolean mIsPrivate;
    private Bitmap mPicture;


    private PFGroup(String mId, String name, List<String> users, List<String> surnames, List<String[]> roles, List<String> events, boolean isPrivate, String description, Bitmap picture) {
        super(mId, PARSE_TABLE_GROUP);
        if (users == null || surnames == null || roles == null || events == null ||
                users.size() < 0 || surnames.size() < 0 || roles.size() < 0 || events.size() < 0) {
            throw new IllegalArgumentException("One of the array has a negative size or is null");
        }
        mUsers = new ArrayList<>();
        mSurnames = new ArrayList<>();
        mRoles = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            try {
                PFUser user = new PFUser.Builder(users.get(i)).build();
                mUsers.add(user);
                mSurnames.add(surnames.get(i));
                mRoles.add(roles.get(i));
            } catch (PFException e) {
                // TODO : what to do?
            }
        }
        mEvents = new ArrayList<>();
        for (String s : events) {
            mEvents.add(new PFEvent.Builder(s).build());
        }
        mName = name;
        mIsPrivate = isPrivate;
        mDescription = description;
        mPicture = picture;
    }

    @Override
    protected void updateToServer(final int idx) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(GROUP_TABLE_NAME);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        switch (idx) {
                            case IDX_NAME:
                                object.put(GROUP_TABLE_TITLE, mName);
                                break;
                            case IDX_USERS:
                                object.put(GROUP_TABLE_USERS, listToArray(mUsers));
                                break;
                            case IDX_SURNAMES:
                                object.put(GROUP_TABLE_SURNAMES, mSurnames.toArray());
                                break;
                            case IDX_ROLES:
                                object.put(GROUP_TABLE_ROLES, mRoles.toArray());
                                break;
                            case IDX_EVENTS:
                                object.put(GROUP_TABLE_EVENTS, listToArray(mEvents));
                                break;
                            case IDX_DESCRIPTION:
                                object.put(GROUP_TABLE_DESCRIPTION, mDescription);
                                break;
                            case IDX_PRIVACY:
                                object.put(GROUP_TABLE_ISPRIVATE, mIsPrivate);
                                break;
                            case IDX_PICTURE:
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] image = stream.toByteArray();
                                ParseFile file = new ParseFile(String.format("group%s.png", getId()), image);
                                file.saveInBackground();
                                object.put(GROUP_TABLE_PICTURE, mPicture);
                                break;
                            default:
                                return;
                        }
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
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

    public String getmName() {
        return mName;
    }

    public List<PFUser> getUsers() {
        return Collections.unmodifiableList(mUsers);
    }

    public String getSurnameForUser(String user) {
        int userIdx;
        if ((userIdx = containsUser(user)) != -1) {
            return mSurnames.get(userIdx);
        }
        return null;
    }

    public String[] getRolesForUser(String user) {
        int userIdx;
        if ((userIdx = containsUser(user)) != -1) {
            return mRoles.get(userIdx);
        }
        return null;
    }

    public List<String> getRoles() {
        List<String> roles = new ArrayList<>();
        for (String[] array : mRoles) {
            for (String role : array) {
                roles.add(role);
            }
        }
        return roles;
    }

    public void addUser(String user) {
        try {
            PFUser usr = new PFUser.Builder(user).build();
            addUser(usr);
        } catch (PFException e) {
            Alert.displayAlert("Error while adding the user to the server.");
        }
    }

    public void addUser(PFUser user) {
        if (belongsToGroup(user.getId())) {
            Alert.displayAlert("User already belongs to this group.");
        } else {
            mUsers.add(user);
            user.addToAGroup(getId());
            mSurnames.add(user.getUsername());
            mRoles.add(new String[1]);
            try {
                updateToServer(IDX_USERS);
            } catch (PFException e) {
                int size = mUsers.size() - 1;
                mUsers.remove(size);
                mSurnames.remove(size);
                mRoles.remove(size);
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    public void removeUser(String user) {
        try {
            PFUser usr = new PFUser.Builder(user).build();
            removeUser(usr);
        } catch (PFException e) {
            Alert.displayAlert("Error while removing the user to the server.");
        }
    }

    public void removeUser(PFUser user) {
        if (!belongsToGroup(user.getId())) {
            Alert.displayAlert("User does not belong to this group.");
        } else {
            int idx = mUsers.indexOf(user);
            mUsers.remove(idx);
            String oldSurname = mSurnames.remove(idx);
            String[] oldRoles = mRoles.remove(idx);
            user.removeFromGroup(getId());
            try {
                updateToServer(IDX_USERS);
            } catch (PFException e) {
                mUsers.add(user);
                mSurnames.add(oldSurname);
                mRoles.add(oldRoles);
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }

    }

    public void setName(String name) {
        if (checkArguments(name, "Group's name")) {
            String oldTitle = mName;
            this.mName = name;
            try {
                updateToServer(IDX_NAME);
            } catch (PFException e) {
                this.mName = oldTitle;
                Alert.displayAlert("Error while updating the group's title to the server.");
            }

        }

    }

    public void setDescription(String description) {
        if (checkNullArguments(description, "Group's description")) {
            String oldDescription = description;
            this.mDescription = description;
            try {
                updateToServer(IDX_DESCRIPTION);
            } catch (PFException e) {
                this.mDescription = oldDescription;
                Alert.displayAlert("Error while updating the description to the server.");
            }

        }
    }

    public void setPrivacy(boolean isPrivate) {
        if (mIsPrivate != isPrivate) {
            this.mIsPrivate = !mIsPrivate;
            try {
                updateToServer(IDX_PRIVACY);
            } catch (PFException e) {
                this.mIsPrivate = !mIsPrivate;
                Alert.displayAlert("Error while changing the privacy to the server.");
            }
        }
    }

    public void setPicture(Bitmap picture) {
        if (!mPicture.equals(picture)) {
            Bitmap oldPicture = mPicture;
            this.mPicture = picture;
            try {
                updateToServer(IDX_PICTURE);
            } catch (PFException e) {
                this.mPicture = oldPicture;
                Alert.displayAlert("Error while updating the picture to the server.");
            }
        }
    }

    private boolean belongsToGroup(String user) {
        return containsUser(user) != -1;
    }

    private int containsUser(String user) {
        for (int i = 0; i < mUsers.size(); i++) {
            if (user.equals(mUsers.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    public static class Builder extends PFEntity.Builder {

        private final List<String> mUsers;
        private final List<String> mSurnames;
        private final List<String[]> mRoles;
        private final List<String> mEvents;

        private String mName;
        private String mDescription;
        private boolean mIsPrivate;
        private Bitmap mPicture;

        public Builder(PFUser user, String name) {
            super(null);
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Group title should not be empty");
            }

            mUsers = new ArrayList<>();
            mSurnames = new ArrayList<>();
            mRoles = new ArrayList<>();

            mUsers.add(user.getId());
            mSurnames.add(user.getUsername());
            mRoles.add(new String[1]);
            mEvents = new ArrayList<>();
            mName = name;
            mIsPrivate = false;
            mDescription = "";
            mPicture = null;
        }

        public Builder(String id) {
            super(id);
            mUsers = new ArrayList<>();
            mSurnames = new ArrayList<>();
            mRoles = new ArrayList<>();
            mEvents = new ArrayList<>();
        }

        private void setUsers(Object[] o) {
            for (Object obj : o) {
                mUsers.add((String) obj);
            }
        }

        private void setSurnames(Object[] o) {
            for (Object obj : o) {
                mSurnames.add((String) obj);
            }
        }

        private void setRoles(Object[] o) {
            for (Object obj : o) {
                try {
                    String[] roles = objectArrayToStringArray(objectToArray(obj));
                    mRoles.add(roles);
                } catch (PFException e) {
                    // TODO : what to do?
                }
            }
        }

        private void setEvents(Object[] o) {
            for (Object obj : o) {
                mEvents.add((String) obj);
            }
        }

        private void setPrivacy(boolean b) {
            this.mIsPrivate = b;
        }

        private void setDescription(String s) {
            this.mDescription = s;
        }

        @Override
        public void retrieveFromServer() throws PFException {
            if (mId != null) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_GROUP);
                query.whereEqualTo(OBJECT_ID, mId);
                try {
                    ParseObject object = query.getFirst();
                    if (object != null) {
                        setUsers(objectToArray(object.get(GROUP_TABLE_USERS)));
                        setSurnames(objectToArray(object.get(GROUP_TABLE_SURNAMES)));
                        setRoles(objectToArray(object.get(GROUP_TABLE_ROLES)));
                        setEvents(objectToArray(object.get(GROUP_TABLE_EVENTS)));
                        setPrivacy(object.getBoolean(GROUP_TABLE_ISPRIVATE));
                        setDescription(object.getString(GROUP_TABLE_DESCRIPTION));
                        ParseFile fileObject = (ParseFile) object
                                .get(GROUP_TABLE_PICTURE);
                        fileObject.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                mPicture = (e == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length));
                            }
                        });
                    } else {
                        throw new PFException("Query failed");
                    }
                } catch (ParseException e) {
                    throw new PFException("Query failed");
                }
            } else {
                final ParseObject object = new ParseObject(GROUP_TABLE_NAME);
                object.put(GROUP_TABLE_USERS, mUsers.toArray());
                object.put(GROUP_TABLE_SURNAMES, mSurnames.toArray());
                object.put(GROUP_TABLE_ROLES, mRoles.toArray());
                object.put(GROUP_TABLE_EVENTS, mEvents.toArray());
                object.put(GROUP_TABLE_TITLE, mName);
                object.put(GROUP_TABLE_DESCRIPTION, mDescription);
                object.put(GROUP_TABLE_ISPRIVATE, mIsPrivate);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            setId(object.getObjectId());
                        } else {
                            // throw new PFException("Query failed");
                        }
                    }
                });
            }
        }

        public PFGroup build() throws PFException {
            retrieveFromServer();
            return new PFGroup(mId, mName, mUsers, mSurnames, mRoles, mEvents, mIsPrivate, mDescription, mPicture);
        }

        private String[] objectArrayToStringArray(Object[] o) {
            String[] out = new String[o.length];
            for (int i = 0; i < out.length; i++) {
                out[i] = (String) o[i];
            }
            return out;
        }

    }
}