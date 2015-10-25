package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.os.Parcel;

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

import ch.epfl.sweng.opengm.utils.Alert;

import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_EVENTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ISPRIVATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ROLES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_NICKNAMES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_USERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkNullArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.listToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

/**
 * This class represents a group which extends PFEntity since it
 * is linked to a table on the server
 */
public final class PFGroup extends PFEntity {

    private final static String PARSE_TABLE_GROUP = PFConstants.GROUP_TABLE_NAME;

    private HashMap<String, PFMember> mMembers;

    private List<PFEvent> mEvents;

    private String mName;
    private String mDescription;
    private boolean mIsPrivate;
    private Bitmap mPicture;

    private PFGroup(String groupId, String name, List<String> users, List<String> surnames, List<String[]> roles, List<String> events, boolean isPrivate, String description, Bitmap picture) {
        super(groupId, PARSE_TABLE_GROUP);
        if ((users == null) || (surnames == null) || (roles == null) || (events == null)) {
            throw new IllegalArgumentException("One of the array  is null");
        }
        if ((users.size() != surnames.size()) || (users.size() != roles.size())) {
            throw new IllegalArgumentException("Arrays' size don't match for group " + groupId + " " + users.size() + " " + surnames.size() + " " + roles.size());
        }
        mMembers = new HashMap<>();

        for (int i = 0; i < users.size(); i++) {
            try {
                String userId = users.get(i);
                String nickname = surnames.get(i);
                String[] role = roles.get(i);
                mMembers.put(userId, PFMember.fetchExistingMember(userId, nickname, role));
            } catch (PFException e) {
                // TODO : what to do?
            }
        }
        mEvents = new ArrayList<>();
        for (String eventId : events) {
            mEvents.add(new PFEvent());
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
                                for (PFMember member : mMembers.values()) {
                                    usersArray.put(member.getId());
                                    surnamesArray.put(member.getNickname());
                                    List<String> roles = member.getRoles();
                                    JSONArray rolesForUser = new JSONArray();
                                    for (int i = 0; i < roles.size(); i++) {
                                        rolesForUser.put(roles.get(i));
                                    }
                                    rolesArray.put(rolesForUser);
                                }
                                object.put(GROUP_ENTRY_USERS, usersArray);
                                object.put(GROUP_ENTRY_NICKNAMES, surnamesArray);
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

    /**
     * Getter for the name of the group
     *
     * @return the name of the group
     */
    public String getName() {
        return mName;
    }

    /**
     * Getter for the list of members in the group
     *
     * @return A list of members in the group
     */
    public List<PFMember> getMembers() {
        return new ArrayList<>(mMembers.values());
    }

    /**
     * Getter for the nickname of a particular member in the group
     *
     * @return the nickname for this user or null if this user does
     * not belong to the group
     */
    public String getNicknameForUser(String userId) {
        PFMember member = mMembers.get(userId);
        if (member != null) {
            return member.getNickname();
        }
        return null;
    }

    /**
     * Getter for the roles of a particular member in the group
     *
     * @return A list of roles for this user or null if this user does
     * not belong to the group
     */
    public List<String> getRolesForUser(String userId) {
        PFMember member = mMembers.get(userId);
        if (member != null) {
            return member.getRoles();
        }
        return null;
    }

    /**
     * Getter for the roles in the group
     *
     * @return A list containing all the different roles in the group
     */
    public List<String> getRoles() {
        Set<String> roles = new HashSet<>();
        for (PFMember member : mMembers.values()) {
            roles.addAll(member.getRoles());
        }
        return new ArrayList<>(roles);
    }

    public List<PFEvent> getEvents() {
        return mEvents;
    }

    /**
     * Add a particular user to a group by adding its id
     *
     * @param userId The string that corresponds to the id of the
     *               user we would like to add to the group
     */
    public void addUser(String userId) {
        if (mMembers.containsKey(userId)) {
            Alert.displayAlert("User already belongs to this group.");
        } else {
            try {
                PFMember member = PFMember.fetchExistingMember(userId);
                member.addToGroup(getId());
                mMembers.put(userId, member);
                updateToServer(GROUP_ENTRY_USERS);
            } catch (PFException e) {
                mMembers.remove(userId);
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    /**
     * Remove a particular user to a group by removing its id
     *
     * @param userId The string that corresponds to the id of the
     *               user we would like to remove from the group
     */
    public void removeUser(String userId) {
        if (!mMembers.containsKey(userId)) {
            Alert.displayAlert("User does not belong to this group.");
        } else {
            PFMember oldMember = mMembers.remove(userId);
            oldMember.removeFromGroup(getId());
            try {
                updateToServer(GROUP_ENTRY_USERS);
            } catch (PFException e) {
                mMembers.put(userId, oldMember);
                Alert.displayAlert("Error while updating the user's groups to the server.");
            }
        }
    }

    /**
     * Add a role to a particular user in the group
     *
     * @param role     The new role we would like to add to the user
     * @param memberId The id of the user that will have a new role
     */
    public void addRoleToUser(String role, String memberId) {
        if (checkNullArguments(role, "Role for user")) {
            if (!mMembers.containsKey(memberId)) {
                Alert.displayAlert("User does not belong to this group.");
            } else {
                PFMember member = mMembers.get(memberId);
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

    /**
     * Remove a role to a particular user in the group
     *
     * @param role     The role we would like to remove to the user
     * @param memberId The id of the user that will have a role removed
     */
    public void removeRoleToUser(String role, String memberId) {
        if (checkNullArguments(role, "Role for user")) {
            if (!mMembers.containsKey(memberId)) {
                Alert.displayAlert("User does not belong to this group.");
            } else {
                PFMember member = mMembers.get(memberId);
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

    /**
     * Change the nickname of a particular user in the group
     *
     * @param nickname The new nickname we would like to attribute to the user
     * @param memberId The id of the user that will see its nickname change
     */
    public void setNicknameForUser(String nickname, String memberId) {
        if (checkNullArguments(nickname, "Surname for user")) {
            if (!mMembers.containsKey(memberId)) {
                Alert.displayAlert("User does not belong to this group.");
            } else {
                PFMember member = mMembers.get(memberId);
                String oldSurname = member.getNickname();
                member.setNickname(nickname);
                try {
                    updateToServer(GROUP_ENTRY_USERS);
                } catch (PFException e) {
                    member.setNickname(oldSurname);
                    Alert.displayAlert("Error while updating the user's groups to the server.");
                }
            }
        }
    }

    /**
     * Setter for the name of the group
     *
     * @param name The new name of the group
     */
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

    /**
     * Setter for the description of the group
     *
     * @param description The new description of the group
     */
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

    /**
     * Setter for the privacy of the group
     *
     * @param isPrivate The new privacy of the group
     */
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

    /**
     * Setter for the group's picture of the group
     *
     * @param picture The new picture of the group
     */
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

    /**
     * Fetches an existing group from the server and returns the object as a PFGroup
     *
     * @param id The id of the group we are looking for
     * @return The group that corresponds to the given id
     * @throws PFException If something wrong happened with the server
     */
    public static PFGroup fetchExistingGroup(String id) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_GROUP);
        query.whereEqualTo(OBJECT_ID, id);
        try {
            ParseObject object = query.getFirst();
            if (object != null) {
                String name = object.getString(GROUP_ENTRY_NAME);
                boolean privacy = object.getBoolean(GROUP_ENTRY_ISPRIVATE);

                String[] usersArray = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_USERS));
                List<String> users = new ArrayList<>();
                users.addAll(Arrays.asList(usersArray));

                String[] nicknamesArray = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_NICKNAMES));
                List<String> nickNames = new ArrayList<>();
                nickNames.addAll(Arrays.asList(nicknamesArray));

                List<String[]> roles = new ArrayList<>();
                JSONArray rolesArray = object.getJSONArray(GROUP_ENTRY_ROLES);
                for (int i = 0; i < rolesArray.length(); i++) {
                    try {
                        String[] currentRoles = convertFromJSONArray((JSONArray) rolesArray.get(i));
                        roles.add(currentRoles);
                    } catch (JSONException | ClassCastException e) {
                        // TODO : if object not found or cast failed ?
                    }
                }

                String[] eventsArray = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_EVENTS));
                List<String> events = new ArrayList<>();
                events.addAll(Arrays.asList(eventsArray));

                String description = object.getString(GROUP_ENTRY_DESCRIPTION);

                Bitmap[] picture = {null};
                retrieveFileFromServer(object, GROUP_ENTRY_PICTURE, picture);
                return new PFGroup(id, name, users, nickNames, roles, events, privacy, description, picture[0]);
            } else {
                throw new PFException("Query failed for id " + id);
            }
        } catch (ParseException e) {
            throw new PFException("Query failed for id " + id);
        }
    }

    /**
     * Create a new Group in the Group table
     *
     * @param user        The user who is creating the group (automatically added)
     * @param name        The name of the group
     * @param description The description of the group
     * @param picture     The profile picture associated with this group
     * @return The new group that contains all the given parameters
     * @throws PFException If something wrong happened with the server
     */
    public static PFGroup createNewGroup(PFUser user, String name, String description, Bitmap picture) throws PFException {

        JSONArray users = new JSONArray();
        users.put(user.getId());
        List<String> usersList = new ArrayList<>();
        usersList.add(user.getId());

        JSONArray nicknames = new JSONArray();
        nicknames.put(user.getUsername());
        List<String> nickNamesList = new ArrayList<>();
        nickNamesList.add(user.getUsername());

        JSONArray roles = new JSONArray();
        roles.put(new JSONArray());
        List<String[]> rolesList = new ArrayList<>();
        rolesList.add(new String[0]);

        JSONArray events = new JSONArray();

        String about = (description == null) ? "" : description;

        ParseObject object = new ParseObject(GROUP_TABLE_NAME);
        object.put(GROUP_ENTRY_USERS, users);
        object.put(GROUP_ENTRY_NICKNAMES, nicknames);
        object.put(GROUP_ENTRY_ROLES, roles);
        object.put(GROUP_ENTRY_EVENTS, events);
        object.put(GROUP_ENTRY_NAME, name);
        object.put(GROUP_ENTRY_DESCRIPTION, about);
        object.put(GROUP_ENTRY_ISPRIVATE, false);
        if (picture != null) {
            object.put(GROUP_ENTRY_PICTURE, picture);
        }

        try {
            object.save();
            String id = object.getObjectId();
            return new PFGroup(id, name, usersList, nickNamesList, rolesList, new ArrayList<String>(), false, about, picture);
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
}