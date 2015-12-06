package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_CONVERSATIONS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_EVENTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ISPRIVATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_NICKNAMES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_POLLS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ROLES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ROLES_PERMISSIONS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_USERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFConstants._USER_TABLE_EMAIL;
import static ch.epfl.sweng.opengm.parse.PFConstants._USER_TABLE_USERNAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkNullArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.collectionToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.utils.Utils.unzipRoles;
import static ch.epfl.sweng.opengm.utils.Utils.zipRole;

/**
 * This class represents a group which extends PFEntity since it
 * is linked to a table on the server
 */
public final class PFGroup extends PFEntity {

    private final static String PARSE_TABLE_GROUP = GROUP_TABLE_NAME;

    private HashMap<String, PFMember> mMembers;

    private HashMap<String, PFEvent> mEvents;

    private HashMap<String, PFPoll> mPolls;

    private ArrayList<String> mConversationNames;

    private String mName;
    private String mDescription;
    private boolean mIsPrivate;
    private Bitmap mPicture;

    private Map<String, List<Permission>> mRolesPermissions;

    private final static String adminRole = "Administrator";
    private final static String userRole = "User";

    public List<String> getConversationInformations() {
        return new ArrayList<>(mConversationNames);
    }

    public enum Permission {
        ADD_MEMBER(0, "Add members"),
        REMOVE_MEMBER(1, "Remove members"),
        MANAGE_ROLES(2, "Manage roles"),
        ADD_ROLES(3, "Add roles"),
        ADD_EVENT(4, "Add events"),
        MANAGE_EVENT(5, "Manage events"),
        MANAGE_GROUP(6, "Manage groups"),
        CREATE_POLL(7, "Create Poll");

        private int value;
        private String name;
        private static Map<Integer, Permission> intToPermission = new HashMap<>();
        private static Map<String, Permission> nameToPermission = new HashMap<>();

        static {
            for (Permission p : Permission.values()) {
                intToPermission.put(p.getValue(), p);
                nameToPermission.put(p.name, p);
            }
        }

        public static Permission forInt(int value) {
            return intToPermission.get(value);
        }

        public static Permission forName(String name) {
            return nameToPermission.get(name);
        }

        Permission(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public PFGroup(Parcel in) {
        super(in, PARSE_TABLE_GROUP);
        this.mName = in.readString();
        List<String> users = in.createStringArrayList();
        List<String> nicknames = in.createStringArrayList();
        List<String> rolesZip = in.createStringArrayList();
        List<String[]> roles = unzipRoles(rolesZip);
        fillMembersMap(users, nicknames, roles);
        mEvents = new HashMap<>();

        List<String> eventKeys = in.createStringArrayList();
        List<PFEvent> events = new ArrayList<>();
        in.readTypedList(events, PFEvent.CREATOR);
        for (int i = 0; i < events.size(); ++i) {
            mEvents.put(eventKeys.get(i), events.get(i));
        }
        mIsPrivate = in.readInt() == 0; //0 is true, everything else is false
        mDescription = in.readString();
        mPicture = in.readParcelable(Bitmap.class.getClassLoader());

        List<String> rolesR = in.createStringArrayList();
        mConversationNames = in.createStringArrayList();
        List<List<Integer>> permissions = in.readArrayList(ArrayList.class.getClassLoader());
        mRolesPermissions = new HashMap<>();
        for (int i = 0; i < rolesR.size(); i++) {
            String currRole = rolesR.get(i);
            List<Permission> actualPermissions = new ArrayList<>();
            for (Integer integer : permissions.get(i)) {
                actualPermissions.add(Permission.forInt(integer));
            }
            mRolesPermissions.put(currRole, actualPermissions);
        }

        mPolls = new HashMap<>();
        List<String> pollsKeys = in.createStringArrayList();
        List<PFPoll> polls = new ArrayList<>();
        in.readTypedList(polls, PFPoll.CREATOR);
        for (int i = 0; i < polls.size(); ++i) {
            mPolls.put(pollsKeys.get(i), polls.get(i));
        }
    }


    private PFGroup(String groupId, Date date, String name, List<String> users, List<String> nicknames, List<String[]> roles, List<String> events, List<String> polls, boolean isPrivate, String description, Bitmap picture, Map<String, List<Permission>> rolesPermissions, List<String> conversationInformations) {
        super(groupId, PARSE_TABLE_GROUP, date);
        if ((users == null) || (nicknames == null) || (roles == null) || (events == null)) {
            throw new IllegalArgumentException("One of the array  is null");
        }
        if ((users.size() != nicknames.size()) || (users.size() != roles.size())) {
            throw new IllegalArgumentException("Arrays' size don't match for group "
                    + groupId + " " + users.size() + " " + nicknames.size() + " " + roles.size());
        }
        fillMembersMap(users, nicknames, roles);
        mPolls = new HashMap<>();
        for (String pollId : polls) {
            try {
                mPolls.put(pollId, PFPoll.fetchExistingPoll(pollId, this));
            } catch (PFException e) {
                // Do not add the event but do nothing
            }
        }
        mEvents = new HashMap<>();
        for (String eventId : events) {
            try {
                mEvents.put(eventId, PFEvent.fetchExistingEvent(eventId, this));
            } catch (PFException e) {
                // Do not add the event but do nothing
            }
        }
        mName = name;
        mIsPrivate = isPrivate;
        mDescription = description;
        mPicture = picture;
        mRolesPermissions = new HashMap<>(rolesPermissions);
        mConversationNames = new ArrayList<>(conversationInformations);
    }

    private void fillMembersMap(List<String> users, List<String> nicknames, List<String[]> roles) {
        mMembers = new HashMap<>();
        if (users.size() == nicknames.size() && users.size() == roles.size()) {
            for (int i = 0; i < users.size(); i++) {
                try {
                    String userId = users.get(i);
                    String nickname = nicknames.get(i);
                    String[] role = roles.get(i);
                    mMembers.put(userId, PFMember.fetchExistingMember(userId, nickname, role));
                } catch (PFException e) {
                }
            }
        } else {
            throw new IllegalArgumentException("list size different: "
                    + users.size() + '-' + nicknames.size() + '-' + roles.size());
        }
    }

    @Override
    public void reload() throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(GROUP_TABLE_NAME);
        try {
            ParseObject object = query.get(getId());
            if (hasBeenModified(object)) {
                setLastModified(object);

                mName = object.getString(GROUP_ENTRY_NAME);
                mIsPrivate = object.getBoolean(GROUP_ENTRY_ISPRIVATE);

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
                        // Cast failed, but doesn't matter
                    }
                }

                fillMembersMap(users, nickNames, roles);

                String[] eventsArray = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_EVENTS));
                String[] pollsArray = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_POLLS));

                HashSet<String> oldEvents = new HashSet<>();
                for (String eventId : mEvents.keySet()) {
                    oldEvents.add(eventId);
                }
                HashSet<String> newEvents = new HashSet<>();
                for (int i = 0; i < eventsArray.length; i++) {
                    newEvents.add(eventsArray[i]);
                }

                if (!newEvents.equals(oldEvents)) {
                    mEvents = new HashMap<>();
                    for (int i = 0; i < eventsArray.length; i++) {
                        try {
                            String eventId = eventsArray[i];
                            mEvents.put(eventId, PFEvent.fetchExistingEvent(eventId, this));
                        } catch (PFException e) {
                            // Do not add the event but to nothing
                        }
                    }
                }

                HashSet<String> oldPolls = new HashSet<>();
                for (String pollId : mPolls.keySet()) {
                    oldPolls.add(pollId);
                }
                HashSet<String> newPolls = new HashSet<>();
                for (int i = 0; i < pollsArray.length; i++) {
                    newPolls.add(pollsArray[i]);
                }

                if (!newPolls.equals(oldPolls)) {
                    mPolls = new HashMap<>();
                    for (int i = 0; i < pollsArray.length; i++) {
                        try {
                            String pollId = pollsArray[i];
                            mPolls.put(pollId, PFPoll.fetchExistingPoll(pollId, this));
                        } catch (PFException e) {
                            // Do not add the event but to nothing
                        }
                    }
                }

                mDescription = object.getString(GROUP_ENTRY_DESCRIPTION);

                // retrieve image
                ParseFile imageFile = (ParseFile) object.get(GROUP_ENTRY_PICTURE);
                if (imageFile != null) {
                    imageFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            mPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                        }
                    });
                }
                HashMap<String, List<Permission>> rolesPermissions = new HashMap<>();
                JSONArray permissionsForRoles = object.getJSONArray(GROUP_ENTRY_ROLES_PERMISSIONS);
                for (int i = 0; i < permissionsForRoles.length(); i++) {
                    try {
                        JSONArray current = (JSONArray) permissionsForRoles.get(i);
                        String role = (String) current.get(0);
                        List<Permission> permissions = new ArrayList<>();
                        for (int j = 1; j < current.length(); j++) {
                            int permission = Integer.parseInt(current.getString(j));
                            permissions.add(Permission.forInt(permission));
                        }
                        rolesPermissions.put(role, permissions);
                    } catch (JSONException e) {
                    }

                }
                mRolesPermissions = new HashMap<>(rolesPermissions);
            }
            for (PFMember member : mMembers.values()) {
                member.reload();
            }
            for (PFEvent event : mEvents.values()) {
                event.reload();
            }
            for (PFPoll poll : mPolls.values()) {
                poll.reload();
            }
        } catch (ParseException e) {
            throw new PFException();
        }

    }

    @Override
    protected void updateToServer(final String entry) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(GROUP_TABLE_NAME);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
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
                            // BE CAREFUL: no break here!
                        case GROUP_ENTRY_ROLES_PERMISSIONS:
                            JSONArray rolesPermissions = new JSONArray();
                            for (Map.Entry<String, List<Permission>> entry : mRolesPermissions.entrySet()) {
                                String role = entry.getKey();
                                JSONArray permissions = new JSONArray();
                                permissions.put(role);
                                for (Permission permission : entry.getValue()) {
                                    permissions.put(permission.getValue());
                                }
                                rolesPermissions.put(permissions);
                            }
                            object.put(GROUP_ENTRY_ROLES_PERMISSIONS, rolesPermissions);
                            break;
                        case GROUP_ENTRY_POLLS:
                            object.put(GROUP_ENTRY_POLLS, collectionToArray(
                                    new ArrayList<PFEntity>(mPolls.values())));
                            break;
                        case GROUP_ENTRY_EVENTS:
                            object.put(GROUP_ENTRY_EVENTS, collectionToArray(
                                    new ArrayList<PFEntity>(mEvents.values())));
                            break;
                        case GROUP_ENTRY_DESCRIPTION:
                            object.put(GROUP_ENTRY_DESCRIPTION, mDescription);
                            break;
                        case GROUP_ENTRY_ISPRIVATE:
                            object.put(GROUP_ENTRY_ISPRIVATE, mIsPrivate);
                            break;
                        case GROUP_ENTRY_PICTURE:
                            // convert bitmap to a bytes array to send it on the server
                            if (mPicture != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] imageData = stream.toByteArray();
                                ParseFile image = new ParseFile(String.format("group%s.png", getId()), imageData);
                                object.put(GROUP_ENTRY_PICTURE, image);
                            } else {
                                object.remove(GROUP_ENTRY_PICTURE);
                            }
                            break;
                        case GROUP_ENTRY_CONVERSATIONS:
                            object.put(GROUP_ENTRY_CONVERSATIONS, mConversationNames);
                            break;
                        default:
                            return;
                    }
                    object.saveInBackground();
                } else {
                    // throw new ParseException("No object for the selected id.");
                }
            }
        });
    }

    public List<PFMember> getMembersWithRole(String role) {
        List<PFMember> members = new ArrayList<>();
        for (PFMember member : mMembers.values()) {
            if (member.hasRole(role)) {
                members.add(member);
            }
        }
        return members;
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
     * @return A map of members in the group
     */
    public HashMap<String, PFMember> getMembers() {
        return mMembers;
    }

    /**
     * Getter for a particular member
     *
     * @param userId The user id of the member to retrieve
     * @return The member
     */
    public PFMember getMember(String userId) {
        return mMembers.get(userId);
    }

    /**
     * getter for the description of the group
     *
     * @return A string containing the description of the group
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Getter for the list of members in the group without the current user
     *
     * @return A list of members in the group without the current user
     */
    public List<PFMember> getMembersWithoutUser(String userId) {
        Map<String, PFMember> members = new HashMap<>(mMembers);
        members.remove(userId);
        return new ArrayList<>(members.values());
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


    public void deleteGroup() {
        HashMap<String, PFMember> members = new HashMap<>(mMembers);
        for (Map.Entry<String, PFMember> member : members.entrySet()) {
            removeUser(member.getKey());
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_GROUP);
        query.whereEqualTo(OBJECT_ID, getId());
        try {
            ParseObject object = query.getFirst();
            object.delete();
        } catch (ParseException e) {
            // Do nothing, that's fine
        }
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

    public Collection<PFEvent> getEvents() {
        return mEvents.values();
    }

    public Collection<PFPoll> getPolls() {
        return mPolls.values();
    }

    public HashMap<String, PFPoll> getPollsWithId() {
        return mPolls;
    }

    public boolean hasMembers() {
        return (mMembers != null && !mMembers.isEmpty());
    }

    public void addPoll(PFPoll poll) {
        if (!mPolls.containsKey(poll.getId())) {
            try {
                mPolls.put(poll.getId(), poll);
                updateToServer(GROUP_ENTRY_POLLS);
            } catch (PFException e) {
                mPolls.remove(poll.getId());
            }
        }
    }

    public void removePoll(PFPoll poll) {
        if (mPolls.containsKey(poll.getId())) {
            try {
                mPolls.remove(poll.getId());
                updateToServer(GROUP_ENTRY_POLLS);
            } catch (PFException e) {
                mPolls.put(poll.getId(), poll);
            }
        }
    }

    /**
     * Add an event to the list of events of this group
     * Update the server also
     *
     * @param event The event we want to add
     * @throws PFException if we cant add the event, it gets removed from mEvents
     */
    public void addEvent(PFEvent event) throws PFException {
        if (!mEvents.containsKey(event.getId())) {
            try {
                mEvents.put(event.getId(), event);
                updateToServer(GROUP_ENTRY_EVENTS);
            } catch (PFException e) {
                mEvents.remove(event.getId());
                throw new PFException("Can't add the event");
            }
        }
    }

    /**
     * Remove then add the event.
     * Also update the server
     *
     * @param event the event to update.
     * @throws PFException if we cant remove it or add it back
     */
    public void updateEvent(PFEvent event) throws PFException {
        event.updateAllFields();
    }

    /**
     * Remove an event to the list of events of this group
     * Also delete the event on the server.
     *
     * @param event The event we want to remove
     * @throws PFException : event is put back into the group.
     */
    public void removeEvent(PFEvent event) throws PFException {
        if (mEvents.containsKey(event.getId())) {
            try {
                mEvents.remove(event.getId());
                updateToServer(GROUP_ENTRY_EVENTS);
                // Delete also the event of the server
                event.delete();
            } catch (PFException e) {
                mEvents.put(event.getId(), event);
                throw new PFException("Can't remove event");
            }
        }
    }

    /**
     * Check if a member is already in this group
     *
     * @param userId The user id of the member
     * @return If the member belong to this group
     */
    public boolean containsMember(String userId) {
        return mMembers.containsKey(userId);
    }

    /**
     * Add a particular user to a group by adding its id
     *
     * @param userId The string that corresponds to the id of the
     *               user we would like to add to the group
     */
    public void addUserWithId(String userId) {

        if (!containsMember(userId)) {
            try {
                PFMember member = PFMember.fetchExistingMember(userId);
                member.addToGroup(getId());
                member.addRole(userRole);
                mRolesPermissions.put(userRole, new ArrayList<>(Arrays.asList(Permission.values())));
                mMembers.put(userId, member);
                updateToServer(GROUP_ENTRY_USERS);
            } catch (PFException e) {
                mMembers.remove(userId);
            }
        }
    }

    /**
     * Add a particular user to a group by adding its username
     *
     * @param username The string that corresponds to the username of the
     *                 user we would like to add to the group
     * @throws PFException If something went wrong with the server
     */
    public void addUserWithUsername(String username) throws PFException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(_USER_TABLE_USERNAME, username);
        try {
            ParseObject object = query.getFirst();
            addUserWithId(object.getObjectId());
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    /**
     * Add a particular user to a group by adding its email
     *
     * @param email The string that corresponds to the email of the
     *              user we would like to add to the group
     * @throws PFException If something went wrong with the server
     */
    public void addUserWithEmail(String email) throws PFException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(_USER_TABLE_EMAIL, email);
        try {
            ParseObject object = query.getFirst();
            addUserWithId(object.getObjectId());
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    /**
     * Remove a particular user to a group by removing its id
     *
     * @param userId The string that corresponds to the id of the
     *               user we would like to remove from the group
     */
    public void removeUser(String userId) {
        if (containsMember(userId)) {
            PFMember oldMember = mMembers.remove(userId);
            oldMember.removeFromGroup(getId());
            try {
                updateToServer(GROUP_ENTRY_USERS);
            } catch (PFException e) {
                mMembers.put(userId, oldMember);
            } finally {
                if (mMembers.isEmpty()) {
                    deleteGroup();
                }
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

        if (checkNullArguments(role)) {
            if (containsMember(memberId)) {
                PFMember member = mMembers.get(memberId);
                member.addRole(role);
                mRolesPermissions.put(role, new ArrayList<Permission>());
                try {
                    updateToServer(GROUP_ENTRY_USERS);
                } catch (PFException e) {
                    member.removeRole(role);
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

        if (checkNullArguments(role)) {
            if (containsMember(memberId)) {
                PFMember member = mMembers.get(memberId);
                member.removeRole(role);
                mRolesPermissions.remove(role);
                try {
                    updateToServer(GROUP_ENTRY_USERS);
                } catch (PFException e) {
                    member.addRole(role);
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
        if (checkNullArguments(nickname)) {
            if (containsMember(memberId)) {
                PFMember member = mMembers.get(memberId);
                String oldSurname = member.getNickname();
                member.setNickname(nickname);
                try {
                    updateToServer(GROUP_ENTRY_USERS);
                } catch (PFException e) {
                    member.setNickname(oldSurname);
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
        if (checkArguments(name)) {
            String oldTitle = mName;
            this.mName = name;
            try {
                updateToServer(GROUP_ENTRY_NAME);
            } catch (PFException e) {
                this.mName = oldTitle;
            }
        }
    }

    /**
     * Setter for the description of the group
     *
     * @param description The new description of the group
     */
    public void setDescription(String description) {
        if (checkNullArguments(description)) {
            String oldDescription = mDescription;
            this.mDescription = description;
            try {
                updateToServer(GROUP_ENTRY_DESCRIPTION);
            } catch (PFException e) {
                this.mDescription = oldDescription;
            }
        }
    }


    public void addConversation(String conversation) {
        if (!conversation.isEmpty() && !mConversationNames.contains(conversation)) {
            ArrayList<String> oldConversationInformations = mConversationNames;
            mConversationNames.add(conversation);
            try {
                updateToServer(GROUP_ENTRY_CONVERSATIONS);
            } catch (PFException e) {
                this.mConversationNames = oldConversationInformations;
            }
        }
    }

    public void setConversationInformations(List<String> conversationInformations) {
        if (conversationInformations != null) {
            ArrayList<String> oldConversationInformations = mConversationNames;
            this.mConversationNames = new ArrayList<>(conversationInformations);
            try {
                updateToServer(GROUP_ENTRY_CONVERSATIONS);
            } catch (PFException e) {
                this.mConversationNames = oldConversationInformations;
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
            }
        }
    }

    public boolean isPrivate() {
        return this.mIsPrivate;
    }

    /**
     * Setter for the group's picture of the group
     *
     * @param picture The new picture of the group
     */
    public void setPicture(Bitmap picture) {
        if ((mPicture == null && picture != null) ||
                (mPicture != null && !mPicture.equals(picture))) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 50, out);
            Bitmap oldPicture = mPicture;
            mPicture = picture;
            try {
                updateToServer(GROUP_ENTRY_PICTURE);
                oldPicture = null;
            } catch (PFException e) {
                mPicture = oldPicture;
            }
        }
    }

    public Bitmap getPicture() {
        return mPicture;
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

                HashMap<String, List<Permission>> rolesPermissions = new HashMap<>();
                JSONArray permissionsForRoles = object.getJSONArray(GROUP_ENTRY_ROLES_PERMISSIONS);
                for (int i = 0; i < permissionsForRoles.length(); i++) {
                    try {
                        JSONArray current = (JSONArray) permissionsForRoles.get(i);
                        String role = (String) current.get(0);
                        List<Permission> permissions = new ArrayList<>();
                        for (int j = 1; j < current.length(); j++) {
                            permissions.add(Permission.forInt(current.getInt(j)));
                        }
                        rolesPermissions.put(role, permissions);
                    } catch (JSONException e) {
                        // TODO : What to do?
                    }

                }

                String[] eventsArray = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_EVENTS));
                List<String> events = new ArrayList<>();
                events.addAll(Arrays.asList(eventsArray));

                String[] pollsArray = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_POLLS));
                List<String> polls = new ArrayList<>();
                polls.addAll(Arrays.asList(pollsArray));

                String[] convsArray = convertFromJSONArray(object.getJSONArray(GROUP_ENTRY_CONVERSATIONS));
                List<String> conversationInformations = new ArrayList<>();
                if (convsArray != null) {
                    conversationInformations.addAll(Arrays.asList(convsArray));
                }

                String description = object.getString(GROUP_ENTRY_DESCRIPTION);

                final PFGroup group = new PFGroup(id, object.getUpdatedAt(), name, users, nickNames,
                        roles, events, polls, privacy, description, null, rolesPermissions, conversationInformations);

                // retrieve image from server
                ParseFile imageFile = (ParseFile) object.get(GROUP_ENTRY_PICTURE);
                if (imageFile != null) {
                    imageFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                            group.setPicture(picture);
                        }
                    });
                }

                return group;
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
    public static PFGroup createNewGroup(PFUser user, String name, String description,
                                         Bitmap picture) throws PFException {

        JSONArray users = new JSONArray();
        users.put(user.getId());
        List<String> usersList = new ArrayList<>();
        usersList.add(user.getId());

        JSONArray nicknames = new JSONArray();
        nicknames.put(user.getUsername());
        List<String> nickNamesList = new ArrayList<>();
        nickNamesList.add(user.getUsername());

        JSONArray roles = new JSONArray();
        JSONArray rolesForFounder = new JSONArray();
        rolesForFounder.put(adminRole);
        roles.put(rolesForFounder);
        List<String[]> rolesList = new ArrayList<>();
        rolesList.add(new String[]{adminRole});

        JSONArray polls = new JSONArray();
        JSONArray events = new JSONArray();

        JSONArray rolesPermissions = new JSONArray();
        JSONArray permissions = new JSONArray();
        permissions.put(adminRole);
        for (Permission permission : Permission.values()) {
            permissions.put(permission.getValue());
        }
        rolesPermissions.put(permissions);
        Map<String, List<Permission>> rolesPermissionsMap = new HashMap<>();
        rolesPermissionsMap.put(adminRole, new ArrayList<>(Arrays.asList(Permission.values())));

        String about = (description == null) ? "" : description;

        ParseObject object = new ParseObject(GROUP_TABLE_NAME);
        object.put(GROUP_ENTRY_USERS, users);
        object.put(GROUP_ENTRY_NICKNAMES, nicknames);
        object.put(GROUP_ENTRY_ROLES, roles);
        object.put(GROUP_ENTRY_EVENTS, events);
        object.put(GROUP_ENTRY_POLLS, polls);
        object.put(GROUP_ENTRY_NAME, name);
        object.put(GROUP_ENTRY_DESCRIPTION, about);
        object.put(GROUP_ENTRY_ISPRIVATE, false);
        object.put(GROUP_ENTRY_ROLES_PERMISSIONS, rolesPermissions);
        if (picture != null) {
            // convert bitmap to a bytes array to send it on the server (FREEZE THE APP)
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] imageData = stream.toByteArray();
            ParseFile image = new ParseFile(name + ".png", imageData);
            object.put(GROUP_ENTRY_PICTURE, image);
        }

        try {
            object.save();
            String id = object.getObjectId();
            PFGroup newGroup = new PFGroup(id, object.getUpdatedAt(), name, usersList, nickNamesList, rolesList, new ArrayList<String>(), new ArrayList<String>(), false, about, picture, rolesPermissionsMap, new ArrayList<String>());
            user.addToAGroup(newGroup);
            return newGroup;
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
        dest.writeString(mId);
        dest.writeString(dateToString(lastModified));
        dest.writeString(mName);
        List<String> ids = new ArrayList<>();
        List<PFMember> members = new ArrayList<>();
        for (String s : mMembers.keySet()) {
            ids.add(s);
            members.add(mMembers.get(s));
        }
        dest.writeStringList(ids);
        List<String> nicknames = new ArrayList<>();
        List<String> rolesZip = new ArrayList<>();
        for (PFMember member : members) {
            nicknames.add(member.getNickname());
            rolesZip.add(zipRole(member.getRoles()));
        }
        dest.writeStringList(nicknames);
        dest.writeStringList(rolesZip);
        List<String> eventKeys = new ArrayList<>();
        List<PFEvent> events = new ArrayList<>();
        for (String s : mEvents.keySet()) {
            eventKeys.add(s);
            events.add(mEvents.get(s));
        }
        dest.writeStringList(eventKeys);
        dest.writeTypedList(events);
        if (mIsPrivate) {
            dest.writeInt(0);
        } else {
            dest.writeInt(42);

        }
        dest.writeString(mDescription);
        dest.writeParcelable(mPicture, flags);
        List<String> rolesList = new ArrayList<>();
        List<List<Integer>> permissions = new ArrayList<>();
        for (Map.Entry<String, List<Permission>> entry : mRolesPermissions.entrySet()) {
            rolesList.add(entry.getKey());
            List<Integer> current = new ArrayList<>();
            for (Permission permission : entry.getValue()) {
                current.add(permission.getValue());
            }
            permissions.add(current);
        }
        dest.writeStringList(rolesList);
        dest.writeList(permissions);
        dest.writeStringList(mConversationNames);
        List<String> pollsKeys = new ArrayList<>();
        List<PFPoll> polls = new ArrayList<>();
        for (Map.Entry<String, PFPoll> entry : mPolls.entrySet()) {
            pollsKeys.add(entry.getKey());
            polls.add(entry.getValue());
        }
        dest.writeStringList(pollsKeys);
        dest.writeTypedList(polls);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public PFGroup createFromParcel(Parcel source) {
            return new PFGroup(source);
        }

        @Override
        public PFGroup[] newArray(int size) {
            return new PFGroup[size];
        }
    };

    /**
     * Gets the permissions of a given role.
     *
     * @param role the role for which to get the permissions
     * @return the list of permissions for the role
     */
    public List<Permission> getPermissionsForRole(String role) {
        return new ArrayList<>(mRolesPermissions.get(role));
    }

    /**
     * Gets the permissions for a given user. This method gets all the permissions
     * for the roles which the user has.
     *
     * @param userId The ID of the user for which to get the roles
     * @return the list of permissions of a user
     */
    public List<Permission> getPermissionsForUser(String userId) {
        List<String> rolesForUser = getRolesForUser(userId);
        List<Permission> permissionsForUser = new ArrayList<>();

        for (String role : rolesForUser) {
            permissionsForUser.addAll(getPermissionsForRole(role));
        }

        return permissionsForUser;
    }

    /**
     * Add a permission to role. This adds the permission to all users who have
     * the role.
     *
     * @param role       the role to which add the permission
     * @param permission the permission to add to the role
     */
    public void addPermissionToRole(String role, Permission permission) {
        if (mRolesPermissions.containsKey(role)) {
            mRolesPermissions.get(role).add(permission);
        } else {
            List<Permission> newPermissions = new ArrayList<>();
            newPermissions.add(permission);
            mRolesPermissions.put(role, newPermissions);
        }
        try {
            updateToServer(GROUP_ENTRY_ROLES_PERMISSIONS);
        } catch (PFException e) {
            mRolesPermissions.remove(role);
        }
    }

    /**
     * Adds a permission to a user. This gives the permission to every role in
     * which the user is.
     *
     * @param userId     the ID of the user for which to add the permission
     * @param permission the permission to add
     */
    public void addPermissionToUser(String userId, Permission permission) {
        List<String> rolesForUser = new ArrayList<>(getRolesForUser(userId));
        for (String role : rolesForUser) {
            addPermissionToRole(role, permission);
        }
    }

    /**
     * Returns true if the specified user has the permission.
     *
     * @param userId     the user to check
     * @param permission the permission to check
     * @return true if userId has the specified permission
     */
    public boolean hasUserPermission(String userId, Permission permission) {
        List<Permission> permissionsForUser = getPermissionsForUser(userId);
        return permissionsForUser.contains(permission);
    }

    /**
     * Removes a permission for every user having the specified role
     *
     * @param role       for which to remove the permission
     * @param permission permission to remove
     */
    public void removePermissionFromRole(String role, Permission permission) {
        if (mRolesPermissions.containsKey(role)) {
            mRolesPermissions.get(role).remove(permission);
            try {
                updateToServer(GROUP_ENTRY_ROLES_PERMISSIONS);
            } catch (PFException e) {
                mRolesPermissions.get(role).add(permission);
            }
        }
    }

    /**
     * Removes a permission for a user. This permission is removed from every
     * role the user is in.
     *
     * @param userId     the ID of the user for which to remove the role
     * @param permission the permission to remove from the user
     */
    public void removePermissionFromUser(String userId, Permission permission) {
        List<String> rolesForUser = getRolesForUser(userId);
        for (String role : rolesForUser) {
            removePermissionFromRole(role, permission);
        }
    }
}