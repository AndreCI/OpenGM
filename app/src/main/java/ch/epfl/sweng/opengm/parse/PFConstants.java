package ch.epfl.sweng.opengm.parse;

/**
 * Name of the entries from the different tables of our Parse database
 */
public final class PFConstants {

    public static final String OBJECT_ID = "objectId";

    // Entries for "_User" table :

    public static final String _USER_TABLE_NAME = "_User";
    public static final String _USER_TABLE_USERNAME = "username";
    public static final String _USER_TABLE_EMAIL = "email";

    // Entries for "User" table :

    public static final String USER_TABLE_NAME = "User";
    public static final String USER_ENTRY_USERID = "userId";
    public static final String USER_ENTRY_USERNAME = "username";
    public static final String USER_ENTRY_FIRSTNAME = "firstName";
    public static final String USER_ENTRY_LASTNAME = "lastName";
    public static final String USER_ENTRY_PHONENUMBER = "phoneNumber";
    public static final String USER_ENTRY_GROUPS = "groups";
    public static final String USER_ENTRY_PICTURE = "profilePicture";
    public static final String USER_ENTRY_ABOUT = "description";

    // Entries for "Group" table :

    public static final String GROUP_TABLE_NAME = "Group";
    public static final String GROUP_ENTRY_NAME = "name";
    public static final String GROUP_ENTRY_ISPRIVATE = "isPrivate";
    public static final String GROUP_ENTRY_USERS = "users";
    public static final String GROUP_ENTRY_NICKNAMES = "nicknames";
    public static final String GROUP_ENTRY_ROLES = "roles";
    public static final String GROUP_ENTRY_POLLS = "polls";
    public static final String GROUP_ENTRY_DESCRIPTION = "description";
    public static final String GROUP_ENTRY_PICTURE = "groupPicture";
    public static final String GROUP_ENTRY_EVENTS = "events";
    public static final String GROUP_ENTRY_ROLES_PERMISSIONS = "rolesPermissions";

    // Entries for "Event" table :

    public static final String EVENT_TABLE_NAME = "Event";
    public static final String EVENT_ENTRY_TITLE = "title";
    public static final String EVENT_ENTRY_DESCRIPTION = "description";
    public static final String EVENT_ENTRY_DATE = "date";
    public static final String EVENT_ENTRY_PLACE = "place";
    public static final String EVENT_ENTRY_PARTICIPANTS = "participants";
    public static final String EVENT_ENTRY_PICTURE = "eventPicture";

    // Entries for "Poll" table :

    public static final String POLL_TABLE_NAME = "Poll";
    public static final String POLL_ENTRY_NAME = "name";
    public static final String POLL_ENTRY_DESCRIPTION = "description";
    public static final String POLL_ENTRY_DEADLINE = "deadline";
    public static final String POLL_ENTRY_PARTICIPANTS = "members";
    public static final String POLL_ENTRY_VOTERS = "voters";
    public static final String POLL_ENTRY_NUMBER_ANSWERS = "nOfPossibleAnswers";
    public static final String POLL_ENTRY_ANSWERS = "answers";
    public static final String POLL_ENTRY_RESULTS = "results";


    //Entries for "ConversationInformations" table :

    public static final String GROUP_ENTRY_CONVERSATIONS = "conversationInformations";

}
