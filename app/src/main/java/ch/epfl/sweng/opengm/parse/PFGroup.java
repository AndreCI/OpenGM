package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.opengm.identification.ImageOverview;
import ch.epfl.sweng.opengm.utils.Alert;

import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_EVENTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ISPRIVATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ROLES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_SURNAMES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_USERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkNullArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.listToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

public class PFGroup extends PFEntity {

    private final static String PARSE_TABLE_GROUP = PFConstants.GROUP_TABLE_NAME;

    private final PFUser mCurrentUser;

    private final HashMap<String, GroupMember> mMembers;
    private final List<PFEvent> mEvents;

    private int nOfUsers;
    private String mName;
    private String mDescription;
    private boolean mIsPrivate;
    private Bitmap mPicture;

    private PFGroup(String groupId, PFUser user, String name, List<String> users, List<String> surnames, List<String[]> roles, List<String> events, boolean isPrivate, String description, Bitmap picture) {
        super(groupId, PARSE_TABLE_GROUP);
        if ((users == null) || (surnames == null) || (roles == null) || (events == null)) {
            throw new IllegalArgumentException("One of the array  is null");
        }
        if ((users.size() != surnames.size()) || (users.size() != roles.size())) {
            throw new IllegalArgumentException("Arrays' size don't match for group " + groupId + " " + users.size() + " " + surnames.size() + " " + roles.size());
        }
        mCurrentUser = user;

        nOfUsers = users.size();
        mMembers = new HashMap<>();

        for (int i = 0; i < users.size(); i++) {
            try {
                String userId = users.get(i);
                String nickname = surnames.get(i);
                String[] role = roles.get(i);
                GroupMember member = new GroupMember.Builder(userId, nickname, role).build();
                mMembers.put(userId, member);
            } catch (PFException e) {
                // TODO : what to do?
            }
        }
        mEvents = new ArrayList<>();
        for (String eventId : events) {
            mEvents.add(new PFEvent.Builder(eventId).build());
        }
        mName = name;
        mIsPrivate = isPrivate;
        mDescription = description;
        mPicture = picture;
    }

    @Override
    protected void updateToServer(final String entry) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(GROUP_TABLE_NAME);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        switch (entry) {
                            case GROUP_ENTRY_NAME:
                                object.put(GROUP_ENTRY_NAME, mName);
                                break;
                            case GROUP_ENTRY_USERS:
                                JSONArray usersArray = new JSONArray();
                                JSONArray surnamesArray = new JSONArray();
                                JSONArray rolesArray = new JSONArray();
                                for (GroupMember member : mMembers.values()) {
                                    usersArray.put(member.getId());
                                    surnamesArray.put(member.getSurname());
                                    List<String> roles = member.getRoles();
                                    JSONArray rolesForUser = new JSONArray();
                                    for (int i = 0; i < roles.size(); i++) {
                                        rolesForUser.put(roles.get(i));
                                    }
                                    rolesArray.put(rolesForUser);
                                }
                                object.put(GROUP_ENTRY_USERS, usersArray);
                                object.put(GROUP_ENTRY_SURNAMES, surnamesArray);
                                object.put(GROUP_ENTRY_ROLES, rolesArray);
                                break;
                            case GROUP_ENTRY_EVENTS:
                                object.put(GROUP_ENTRY_EVENTS, listToArray(mEvents));
                                break;
                            case GROUP_ENTRY_DESCRIPTION:
                                object.put(GROUP_ENTRY_DESCRIPTION, mDescription);
                                break;
                            case GROUP_ENTRY_ISPRIVATE:
                                object.put(GROUP_ENTRY_ISPRIVATE, mIsPrivate);
                                break;
                            case GROUP_ENTRY_PICTURE:
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] image = stream.toByteArray();
                                ParseFile file = new ParseFile(String.format("group%s.png", getId()), image);
                                file.saveInBackground();
                                object.put(GROUP_ENTRY_PICTURE, mPicture);
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

    public String getName() {
        return mName;
    }


    public List<GroupMember> getMembers() {
        return new ArrayList<>(mMembers.values());
    }

    public String getSurnameForUser(String userId) {
        GroupMember member = mMembers.get(userId);
        if (member != null) {
            return member.getSurname();
        }
        return null;
    }

    public List<String> getRolesForUser(String userId) {
        GroupMember member = mMembers.get(userId);
        if (member != null) {
            return member.getRoles();
        }
        return null;
    }

    public List<String> getRoles() {
        Set<String> roles = new HashSet<>();
        for (GroupMember member : mMembers.values()) {
            roles.addAll(member.getRoles());
        }
        return new ArrayList<>(roles);
    }


    public void addUser(String userId) {
        if (mMembers.containsKey(userId)) {
            Alert.displayAlert("User already belongs to this group.");
        } else {
            try {
                GroupMember member = new GroupMember.Builder(userId).build();
                member.addToGroup(getId());
                mMembers.put(userId, member);
                updateToServer(GROUP_ENTRY_USERS);
                nOfUsers++;
            } catch (PFException e) {
                mMembers.remove(userId);
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    public void removeUser(String userId) {
        if (!mMembers.containsKey(userId)) {
            Alert.displayAlert("User does not belong to this group.");
        } else {
            GroupMember oldMember = mMembers.remove(userId);
            oldMember.removeFromGroup(getId());
            try {
                updateToServer(GROUP_ENTRY_USERS);
                nOfUsers--;
            } catch (PFException e) {
                mMembers.put(userId, oldMember);
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    public void addRoleToUser(String role, String memberId) {
        if (checkNullArguments(role, "Role for user")) {
            if (!mMembers.containsKey(memberId)) {
                Alert.displayAlert("User does not belong to this group.");
            } else {
                GroupMember member = mMembers.get(memberId);
                member.addRole(role);
                try {
                    updateToServer(GROUP_ENTRY_USERS);
                } catch (PFException e) {
                    member.removeRole(role);
                    Alert.displayAlert("Error while updating the user's groups to the server.");
                }
            }
        }
    }

    public void removeRoleToUser(String role, String memberId) {
        if (checkNullArguments(role, "Role for user")) {
            if (!mMembers.containsKey(memberId)) {
                Alert.displayAlert("User does not belong to this group.");
            } else {
                GroupMember member = mMembers.get(memberId);
                member.removeRole(role);
                try {
                    updateToServer(GROUP_ENTRY_USERS);
                } catch (PFException e) {
                    member.addRole(role);
                    Alert.displayAlert("Error while updating the user's groups to the server.");
                }
            }
        }
    }

    public void setSurnameForUser(String surname, String memberId) {
        if (checkNullArguments(surname, "Surname for user")) {
            if (!mMembers.containsKey(memberId)) {
                Alert.displayAlert("User does not belong to this group.");
            } else {
                GroupMember member = mMembers.get(memberId);
                String oldSurname = member.getSurname();
                member.setmNickname(surname);
                try {
                    updateToServer(GROUP_ENTRY_USERS);
                } catch (PFException e) {
                    member.setmNickname(oldSurname);
                    Alert.displayAlert("Error while updating the user's groups to the server.");
                }
            }
        }
    }

    public void setName(String name) {
        if (checkArguments(name, "Group's name")) {
            String oldTitle = mName;
            this.mName = name;
            try {
                updateToServer(GROUP_TABLE_NAME);
            } catch (PFException e) {
                this.mName = oldTitle;
                Alert.displayAlert("Error while updating the group's title to the server.");
            }
        }
    }

    public void setDescription(String description) {
        if (checkNullArguments(description, "Group's description")) {
            String oldDescription = mDescription;
            this.mDescription = description;
            try {
                updateToServer(GROUP_ENTRY_DESCRIPTION);
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
                updateToServer(GROUP_ENTRY_ISPRIVATE);
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
                updateToServer(GROUP_ENTRY_PICTURE);
            } catch (PFException e) {
                this.mPicture = oldPicture;
                Alert.displayAlert("Error while updating the picture to the server.");
            }
        }
    }

    public static class Builder extends PFEntity.Builder implements ImageOverview {

        private final List<String> mUsers;
        private final List<String> mSurnames;
        private final List<String[]> mRoles;
        private final List<String> mEvents;
        private final PFUser mCurrentUser;

        private String mName;
        private String mDescription;
        private boolean mIsPrivate;
        private Bitmap mPicture;

        public Builder(PFUser user, String nameOrId, boolean newGroup) {
            super(null);
            mUsers = new ArrayList<>();
            mSurnames = new ArrayList<>();
            mRoles = new ArrayList<>();
            mEvents = new ArrayList<>();
            mCurrentUser = user;
            if (newGroup) {

                if (nameOrId == null || nameOrId.isEmpty()) {
                    throw new IllegalArgumentException("Group title should not be empty");
                }
                mUsers.add(user.getId());
                mSurnames.add(user.getUsername());
                mRoles.add(new String[1]);
                mName = nameOrId;
                mIsPrivate = false;
                mDescription = "";
                mPicture = null;
            } else {
                setId(nameOrId);
            }
        }

        private void setName(String name) {
            this.mName = name;
        }

        private void setUsers(String[] users) {
            this.mUsers.addAll(Arrays.asList(users));
        }

        private void setSurnames(String[] surnames) {
            this.mSurnames.addAll(Arrays.asList(surnames));
        }

        private void setRoles(JSONArray rolesArray) {
            if (rolesArray != null) {
                for (int i = 0; i < rolesArray.length(); i++) {
                    try {
                        String[] roles = convertFromJSONArray((JSONArray) rolesArray.get(i));
                        mRoles.add(roles);
                    } catch (JSONException | ClassCastException e) {
                        // TODO : if object not found or cast failed ?
                        e.printStackTrace();
                    }
                }
            }
        }

        private void setEvents(String[] events) {
            if (events != null) {
                this.mEvents.addAll(Arrays.asList(events));
            }
        }

        private void setPrivacy(boolean b) {
            this.mIsPrivate = b;
        }

        private void setDescription(String description) {
            this.mDescription = description;
        }

        @Override
        public void setImage(Bitmap image) {
            this.mPicture = image;
        }

        @Override
        public void retrieveFromServer() throws PFException {
            if (mId != null) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_GROUP);
                query.whereEqualTo(OBJECT_ID, mId);
                try {
                    ParseObject object = query.getFirst();
                    if (object != null) {
                        setName(object.getString(GROUP_ENTRY_NAME));
                        setPrivacy(object.getBoolean(GROUP_ENTRY_ISPRIVATE));

                        String[] users = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_USERS));
                        setUsers(users);

                        String[] surnames = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_SURNAMES));
                        setSurnames(surnames);

                        setRoles(object.getJSONArray(GROUP_ENTRY_ROLES));

                        String[] events = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_EVENTS));
                        setEvents(events);

                        setDescription(object.getString(GROUP_ENTRY_DESCRIPTION));

                        retrieveFileFromServer(object, GROUP_ENTRY_PICTURE, this);

                    } else {
                        throw new PFException("Query failed");
                    }
                } catch (ParseException e) {
                    throw new PFException("Query failed");
                }
            } else {
                final ParseObject object = new ParseObject(GROUP_TABLE_NAME);
                object.put(GROUP_ENTRY_USERS, mUsers.toArray());
                object.put(GROUP_ENTRY_SURNAMES, mSurnames.toArray());
                object.put(GROUP_ENTRY_ROLES, mRoles.toArray());
                object.put(GROUP_ENTRY_EVENTS, mEvents.toArray());
                object.put(GROUP_ENTRY_NAME, mName);
                object.put(GROUP_ENTRY_DESCRIPTION, mDescription);
                object.put(GROUP_ENTRY_ISPRIVATE, mIsPrivate);
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
            return new PFGroup(mId, mCurrentUser, mName, mUsers, mSurnames, mRoles, mEvents, mIsPrivate, mDescription, mPicture);
        }

    }
}