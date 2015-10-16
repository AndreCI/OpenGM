package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_EVENTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_ISPRIVATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_ROLES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_SURNAMES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_USERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;

import static ch.epfl.sweng.opengm.parse.PFUtils.objectToArray;

public class PFGroup extends PFEntity {

    private final static String PARSE_TABLE_GROUP = PFConstants.GROUP_TABLE_NAME;

    private final int nOfUsers;
    private final Map<PFUser, Role> mRoles;

    public PFGroup(String id, Map<PFUser, Role> roles) {
        super(id, PARSE_TABLE_GROUP);
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Group is null or empty");
        }
        nOfUsers = roles.size();
        mRoles = new HashMap<>(roles);
    }

    private PFGroup(Map<String, Role> roles, String id) {
        super(id, PARSE_TABLE_GROUP);
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Group is null or empty");
        }
        nOfUsers = roles.size();
        mRoles = userAndRoles(roles);
    }

    public PFGroup(String id, String[] users, int[] roles) {
        super(id, PARSE_TABLE_GROUP);
        if (users == null || roles == null) {
            throw new IllegalArgumentException("Arrays arr null");
        }
        if (users.length != roles.length) {
            throw new IllegalArgumentException("Arrays size don't match");
        }
        nOfUsers = users.length;
        if (nOfUsers < 1) {
            throw new IllegalArgumentException("Arrays are empty");
        }
        mRoles = userAndRoles(users, roles);
    }

    public PFGroup(String mId, List<String> mUsers, List<String> mSurnames, List<String> mRoles, List<String> mEvents, boolean isPrivate, String mDescription, Bitmap mPicture) {
        super();
    }

    @Override
    protected void updateToServer(int idx) throws PFException {

    }

    private boolean addUserToGroup(String u) {
        if (containUser(u)) {
            return true;
        }
        // TODO update the array of the group and also the groups array of this user
        return false;
    }

    public boolean addUserToGroup(PFUser u) {
        return addUserToGroup(u.getId());
    }

    private boolean deleteUserToGroup(String u) throws IllegalArgumentException {
        if (!containUser(u)) {
            throw new IllegalArgumentException("The user with id " + u + " doesn't belong to this group");
        }
        // TODO update the array of the group and also the groups array of this user
        return true;
    }

    public boolean deleteUserToGroup(PFUser u) throws IllegalArgumentException {
        return deleteUserToGroup(u.getId());
    }

    private boolean containUser(String id) {
        for (PFUser s : mRoles.keySet()) {
            if (s.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private Map<PFUser, Role> userAndRoles(String[] users, int[] r) {
        Map<String, Role> roles = new HashMap<>();
        for (int i = 0; i < nOfUsers; i++) {
            roles.put(users[i], Role.getRole(r[i]));
        }
        return userAndRoles(roles);
    }

    private Map<PFUser, Role> userAndRoles(Map<String, Role> r) {
        Map<PFUser, Role> roles = new HashMap<>();
        for (Map.Entry<String, Role> entry : r.entrySet()) {
            PFUser.Builder usr = new PFUser.Builder(entry.getKey());
            try {
                usr.retrieveFromServer();
            } catch (PFException e) {
                // TODO what to do with the error?
                e.printStackTrace();
            }
            //roles.put(usr.build(), entry.getValue());
        }
        return new HashMap<>(roles);
    }

    public static class Builder extends PFEntity.Builder {

        private final List<String> mUsers;
        private final List<String> mSurnames;
        private final List<String> mRoles;
        private final List<String> mEvents;

        private String mDescription;
        private boolean isPrivate;
        private Bitmap mPicture;

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
                mRoles.add((String) obj);
            }
        }

        private void setEvents(Object[] o) {
            for (Object obj : o) {
                mEvents.add((String) obj);
            }
        }

        private void setPrivacy(boolean b) {
            this.isPrivate = b;
        }

        private void setDescription(String s) {
            this.mDescription = s;
        }

        @Override
        public void retrieveFromServer() throws PFException {
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
        }

        public PFGroup build() {
            return new PFGroup(mId, mUsers, mSurnames, mRoles, mEvents, isPrivate, mDescription, mPicture);
        }
    }
}