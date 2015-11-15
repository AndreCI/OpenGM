package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_EVENTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ISPRIVATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_NICKNAMES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_ROLES;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_USERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.checkNullArguments;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;
import static ch.epfl.sweng.opengm.utils.Utils.unzipRoles;
import static ch.epfl.sweng.opengm.utils.Utils.zipRole;

/**
 * This class represents a group which extends PFEntity since it
 * is linked to a table on the server
 */
public final class PFGroup extends PFEntity {

    private final static String PARSE_TABLE_GROUP = PFConstants.GROUP_TABLE_NAME;

    private HashMap<String, PFMember> mMembers;

    private HashMap<String, PFEvent> mEvents;

    private String mName;
    private String mDescription;
    private boolean mIsPrivate;
    private Bitmap mPicture;

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
        mPicture = null;//in.readParcelable(Bitmap.class.getClassLoader());
    }


    private PFGroup(String groupId, Date date, String name, List<String> users, List<String> nicknames, List<String[]> roles, List<String> events, boolean isPrivate, String description, Bitmap picture) {
        super(groupId, PARSE_TABLE_GROUP, date);
        if ((users == null) || (nicknames == null) || (roles == null) || (events == null)) {
            throw new IllegalArgumentException("One of the array  is null");
        }
        if ((users.size() != nicknames.size()) || (users.size() != roles.size())) {
            throw new IllegalArgumentException("Arrays' size don't match for group " + groupId + " " + users.size() + " " + nicknames.size() + " " + roles.size());
        }
        fillMembersMap(users, nicknames, roles);
        mEvents = new HashMap<>();
        for (String eventId : events) {
            try {
                mEvents.put(eventId, PFEvent.fetchExistingEvent(eventId));
            } catch (PFException e) {
                // Do not add the event but do nothing
            }
        }
        mName = name;
        mIsPrivate = isPrivate;
        mDescription = description;
        mPicture = picture;
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
                    //TODO : what to do ?
                }
            }
        } else {
            throw new IllegalArgumentException("list size different: " + users.size() + '-' + nicknames.size() + '-' + roles.size());
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
                            mEvents.put(eventId, PFEvent.fetchExistingEvent(eventId));
                        } catch (PFException e) {
                            // Do not add the event but to nothing
                        }
                    }
                }

                mDescription = object.getString(GROUP_ENTRY_DESCRIPTION);

                Bitmap[] picture = {null};
                retrieveFileFromServer(object, GROUP_ENTRY_PICTURE, picture);
                mPicture = picture[0];
            }
            for (PFMember member : mMembers.values()) {
                member.reload();
            }
            for (PFEvent event : mEvents.values()) {
                event.reload();
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
                if (e == null && object != null) {
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
                            object.put(GROUP_ENTRY_EVENTS, PFUtils.collectionToArray(new ArrayList<PFEntity>(mEvents.values())));
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
                            if (e != null) {
                                // throw new ParseException("No object for the selected id.");
                            }
                        }
                    });
                } else {
                    // throw new ParseException("No object for the selected id.");
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
     * @return A map of members in the group
     */
    public HashMap<String, PFMember> getMembers() {
        return new HashMap<>(mMembers);
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
        for (Map.Entry<String, PFMember> member : mMembers.entrySet()) {
            removeUser(member.getKey());
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_GROUP);
        query.whereEqualTo(OBJECT_ID, getId());
        try {
            ParseObject object = query.getFirst();
            object.delete();
        } catch (ParseException e) {
            // TODO what to do if deleting failed?
            Log.v("INFO", "group does not exist. Deletion aborted.");
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

    public List<PFEvent> getEvents() {
        return new ArrayList<>(mEvents.values());
    }

    public boolean hasMembers() {
        return (mMembers != null && !mMembers.isEmpty());
    }

    /**
     * Add an event to the list of events of this group
     *
     * @param event The event we want to add
     */
    public void addEvent(PFEvent event) {
        if (!mEvents.containsKey(event.getId())) {
            try {
                mEvents.put(event.getId(), event);
                updateToServer(GROUP_ENTRY_EVENTS);
            } catch (PFException e) {
                mEvents.remove(event.getId());
            }
        }
    }

    /**
     * Add an event to the list of events of this group
     *
     * @param eventId The if of the event we want to add
     */
    public void addEvent(String eventId) {
        if (!mEvents.containsKey(eventId)) {
            try {
                addEvent(PFEvent.fetchExistingEvent(eventId));
            } catch (PFException e) {
                // TODO what ?
            }
        }
    }

    public void updateEvent(PFEvent event) {
        removeEvent(event);
        addEvent(event);
    }

    /**
     * Remove an event to the list of events of this group
     *
     * @param event The event we want to remove
     */
    public void removeEvent(PFEvent event) {
        if (mEvents.containsKey(event.getId())) {
            try {
                mEvents.remove(event.getId());
                updateToServer(GROUP_ENTRY_EVENTS);
                // Delete also the event of the server
                event.delete();
            } catch (PFException e) {
                //TODO: ????
                mEvents.put(event.getId(), event);
            }
        }
    }

    /**
     * Remove an event to the list of events of this group
     *
     * @param eventId The if of the event we want to remove
     */
    public void removeEvent(String eventId) {
        if (mEvents.containsKey(eventId)) {
            try {
                removeEvent(PFEvent.fetchExistingEvent(eventId));
            } catch (PFException e) {
                // TODO what ?
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
     *              user we would like to add to the group
     * @throws PFException If something went wrong with the server
     */
    public void addUserWithUsername(String username) throws PFException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(PFConstants._USER_TABLE_USERNAME, username);
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
        query.whereEqualTo(PFConstants._USER_TABLE_EMAIL, email);
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
        if (!mPicture.equals(picture)) {
            Bitmap oldPicture = mPicture;
            this.mPicture = picture;
            try {
                updateToServer(GROUP_ENTRY_PICTURE);
            } catch (PFException e) {
                this.mPicture = oldPicture;
            }
        }
    }

    public Bitmap getPicture() {
        return (mPicture == null) ? null : Bitmap.createBitmap(mPicture);
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
                return new PFGroup(id, object.getUpdatedAt(), name, users, nickNames, roles, events, privacy, description, picture[0]);
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
            PFGroup newGroup = new PFGroup(id, object.getUpdatedAt(), name, usersList, nickNamesList, rolesList, new ArrayList<String>(), false, about, picture);
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

}