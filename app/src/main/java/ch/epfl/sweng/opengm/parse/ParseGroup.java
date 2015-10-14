package ch.epfl.sweng.opengm.parse;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.Map;

import static ch.epfl.sweng.opengm.parse.ParseConstants.GROUP_TABLE_ROLES;
import static ch.epfl.sweng.opengm.parse.ParseConstants.GROUP_TABLE_USERS;
import static ch.epfl.sweng.opengm.parse.ParseConstants.OBJECT_ID;

import static ch.epfl.sweng.opengm.parse.ParseUtils.objectToArray;
import static ch.epfl.sweng.opengm.parse.ParseUtils.objectToString;

public class ParseGroup extends ParseEntity {

    private final static String PARSE_TABLE_GROUP = ParseConstants.GROUP_TABLE_NAME;

    private final int nOfUsers;
    private final Map<User, Role> mRoles;

    public ParseGroup(String id, Map<User, Role> roles) {
        super(id, PARSE_TABLE_GROUP);
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Group is null or empty");
        }
        nOfUsers = roles.size();
        mRoles = new HashMap<>(roles);
    }

    public ParseGroup(Map<String, Role> roles, String id) {
        super(id, PARSE_TABLE_GROUP);
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Group is null or empty");
        }
        nOfUsers = roles.size();
        mRoles = userAndRoles(roles);
    }

    public ParseGroup(String id, String[] users, int[] roles) {
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

    public boolean addUserToGroup(String u) {
        if (containUser(u)) {
            return true;
        }
        // TODO update the array of the group and also the groups array of this user
        return false;
    }

    public boolean addUserToGroup(User u) {
        return addUserToGroup(u.getId());
    }

    public boolean deleteUserToGroup(String u) throws IllegalArgumentException {
        if (!containUser(u)) {
            throw new IllegalArgumentException("The user with id " + u + " doesn't belong to this group");
        }
        // TODO update the array of the group and also the groups array of this user
        return true;
    }

    public boolean deleteUserToGroup(User u) throws IllegalArgumentException {
        return deleteUserToGroup(u.getId());
    }

    private boolean containUser(String id) {
        for (User s : mRoles.keySet()) {
            if (s.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private Map<User, Role> userAndRoles(String[] users, int[] r) {
        Map<String, Role> roles = new HashMap<>();
        for (int i = 0; i < nOfUsers; i++) {
            roles.put(users[i], Role.getRole(r[i]));
        }
        return userAndRoles(roles);
    }

    private Map<User, Role> userAndRoles(Map<String, Role> r) {
        Map<User, Role> roles = new HashMap<>();
        for (Map.Entry<String, Role> entry : r.entrySet()) {
            User.Builder usr = new User.Builder(entry.getKey());
            try {
                usr.retrieveFromParse();
            } catch (ServerException e) {
                // TODO what to do with the error?
                e.printStackTrace();
            }
            roles.put(usr.build(), entry.getValue());
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

    public static class Builder extends ParseEntity.Builder {

        private Map<String, Role> mRoles;

        public Builder(String id) {
            super(id, PARSE_TABLE_GROUP);
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
        public void retrieveFromParse() throws ServerException {
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
                        throw new ServerException("Parse query for id " + mId + " failed");
                    }
                } else {
                    throw new ServerException("Parse query for id " + mId + " failed");
                }
            } catch (ParseException e) {
                throw new ServerException("Parse query for id " + mId + " failed");
            }
        }

        public ParseGroup build() {
            return new ParseGroup(mRoles, mId);
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
