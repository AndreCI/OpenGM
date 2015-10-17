package ch.epfl.sweng.opengm.parse;

/**
 * Name of the entries from the different tables of our Parse database
 */
public class PFConstants {

    // TODO: check that entry names are the same as on Parse.com DB
    // FIXME: "Name" entry == name of the table in the DB, not the same as "name of user", or "name of group"

    public static final String OBJECT_ID = "objectId";

    // Entries for "_User" table :

    public static final String _USER_TABLE_NAME = "_User";
    public static final String _USER_TABLE_USERNAME = "username";
    public static final String _USER_TABLE_PASSWORD = "password";
    public static final String _USER_TABLE_EMAIL = "email";

    // Entries for "User" table :

    public static final String USER_TABLE_NAME = "User";
    public static final String USER_TABLE_USER_ID = "userId";
    public static final String USER_TABLE_USERNAME = "username";
    public static final String USER_TABLE_FIRST_NAME = "firstName";
    public static final String USER_TABLE_LAST_NAME = "lastName";
    public static final String USER_TABLE_PHONE_NUMBER = "phoneNumer";
    public static final String USER_TABLE_GROUPS = "groups";
    public static final String USER_TABLE_PICTURE = "profilePicture";
    public static final String USER_TABLE_ABOUT = "description";

    // Entries for "Group" table :

    public static final String GROUP_TABLE_NAME = "Group";
    public static final String GROUP_TABLE_TITLE = "name";
    public static final String GROUP_TABLE_ISPRIVATE = "isPrivate";
    public static final String GROUP_TABLE_USERS = "users";
    public static final String GROUP_TABLE_SURNAMES = "surnames";
    public static final String GROUP_TABLE_ROLES = "roles";
    public static final String GROUP_TABLE_DESCRIPTION = "description";
    public static final String GROUP_TABLE_PICTURE = "groupPicture";
    public static final String GROUP_TABLE_EVENTS = "events";

    // Entries for "Event" table :

    public static final String EVENT_TABLE_NAME = "Event";
    public static final String EVENT_TABLE_TITLE = "title";  // TODO: Entry yet to create
    public static final String EVENT_TABLE_DESCRIPTION = "description";
    public static final String EVENT_TABLE_DATE = "date";
    public static final String EVENT_TABLE_PLACE = "place";
    public static final String EVENT_TABLE_PARTICIPANTS = "participants";
}
