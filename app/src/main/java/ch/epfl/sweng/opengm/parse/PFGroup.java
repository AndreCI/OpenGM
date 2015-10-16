package ch.epfl.sweng.opengm.parse;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.Map;

import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_ROLES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_USERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;

import static ch.epfl.sweng.opengm.parse.PFUtils.objectToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.objectToString;

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

    @Override
    protected void updateToServer() throws PFException {

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

    public enum Role {

        FOUNDER(0), ADMINISTRATOR(1), MODERATOR(2);

        private final int idx;

        Role(int i) {
            idx = i;
        }

        public static Role getRole(int i) {
            return Role.values()[i];
        }
    }

    public static class Builder extends PFEntity.Builder {

        private final Map<String, Role> mRoles;

        public Builder(String id) {
            super(id);
            this.mRoles = new HashMap<>();
        }

        public boolean addUserToGroup(String u, Role r) {
            if (contains(u)) {
                return true;
            }
            mRoles.put(u, r);
            return false;
        }

        @Override
        public void retrieveFromServer() throws PFException {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_GROUP);
            query.whereEqualTo(OBJECT_ID, mId);
            try {
                ParseObject object = query.getFirst();
                if (object != null) {
                    Object[] users = objectToArray(object.get(GROUP_TABLE_USERS));
                    Object[] roles = objectToArray(object.get(GROUP_TABLE_ROLES));
                    try {
                        for (int i = 0; i < Math.min(users.length, roles.length); i++) {
                            addUserToGroup(objectToString(users[i]), Role.getRole(Integer.parseInt(objectToString(roles[i]))));
                        }
                    } catch (NumberFormatException e) {
                        throw new PFException("Parse query for id " + mId + " failed");
                    }
                } else {
                    throw new PFException("Parse query for id " + mId + " failed");
                }
            } catch (ParseException e) {
                throw new PFException("Parse query for id " + mId + " failed");
            }
        }

        public PFGroup build() {
            return new PFGroup(mRoles, mId);
        }

        private boolean contains(String s) {
            for (String key : mRoles.keySet()) {
                if (key.equals(s)) {
                    return true;
                }
            }
            return false;
        }
    }
}
