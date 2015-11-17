package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ch.epfl.sweng.opengm.events.Utils;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_DATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_PARTICIPANTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_PLACE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_TITLE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.GROUP_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFUtils.collectionToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

public final class PFEvent extends PFEntity implements Parcelable, Comparable<PFEvent> {

    private final static String PARSE_TABLE_EVENT = PFConstants.EVENT_TABLE_NAME;

    private String mTitle;
    private String mDescription;
    private Date mDate;
    private String mPlace;
    private Bitmap mPicture;
    private HashMap<String, PFMember> mParticipants;

    public PFEvent(Parcel in) {
        super(in, PARSE_TABLE_EVENT);
        mTitle = in.readString();
        mDescription = in.readString();
        mDate = Utils.stringToDate(in.readString());
        mPlace = in.readString();
        mPicture = in.readParcelable(Bitmap.class.getClassLoader());
        List<String> participantKeys = in.createStringArrayList();
        Parcelable[] array = in.readParcelableArray(getClass().getClassLoader());
        List<PFMember> participants = new ArrayList<>();
        //in.readList(participants, getClass().getClassLoader());
        for(Parcelable parcelable : array) {
            participants.add((PFMember) parcelable);
        }
        mParticipants = new HashMap<>();
        for(int i = 0; i < participants.size(); ++i) {
            mParticipants.put(participantKeys.get(i), participants.get(i));
        }
    }

    public static final Creator<PFEvent> CREATOR = new Creator<PFEvent>() {
        @Override
        public PFEvent createFromParcel(Parcel in) {
            return new PFEvent(in);
        }

        @Override
        public PFEvent[] newArray(int size) {
            return new PFEvent[size];
        }
    };

    private PFEvent(String id, Date updated, String name, String place, Date date, String description, List<PFMember> participants, Bitmap picture) {
        super(id, PARSE_TABLE_EVENT, updated);
        this.mTitle = name;
        this.mPlace = place;
        this.mDate = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes());
        this.mDescription = description;
        this.mParticipants = new HashMap<>();
        for(PFMember p : participants) {
            mParticipants.put(p.getId(), p);
            //TODO : participants is list of String or PFMemeber?
        }
        this.mPicture = picture;
    }

    public int getYear() {
        return mDate.getYear();
    }

    public int getMonth() {
        return mDate.getMonth() +1;
    }

    public int getDay() {
        return mDate.getDate();
    }

    public int getHours() {
        return mDate.getHours();
    }

    public int getMinutes() {
        return mDate.getMinutes();
    }

    public String getName() {
        return mTitle;
    }

    public void setName(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String mPlace) {
        this.mPlace = mPlace;
    }

    public Bitmap getPicture() {
        return mPicture;
    }

    public void setPicture(Bitmap mPicture) {
        this.mPicture = mPicture;
    }

    public HashMap<String, PFMember> getParticipants() {
        return new HashMap<>(mParticipants);
    }

    public PFMember addParticipant(String id, PFMember participant) {
        return mParticipants.put(id, participant);
    }

    public PFMember removeParticipant(String id) {
        return mParticipants.remove(id);
    }


    @Override
    public void reload() throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_EVENT);
        try {
            ParseObject object = query.get(getId());

            if (hasBeenModified(object)) {
                setLastModified(object);

                if (object != null) {

                    mTitle = object.getString(PFConstants.EVENT_ENTRY_TITLE);
                    mDescription = object.getString(EVENT_ENTRY_DESCRIPTION);
                    mPlace = object.getString(EVENT_ENTRY_PLACE);
                    mDate = object.getDate(EVENT_ENTRY_DATE);

                    Bitmap[] picture = {null};
                    retrieveFileFromServer(object, EVENT_ENTRY_PICTURE, picture);
                    mPicture = picture[0];

                    String[] groupsArray = convertFromJSONArray(object.getJSONArray(EVENT_ENTRY_PARTICIPANTS));
                    List<String> participants = new ArrayList<>(Arrays.asList(groupsArray));

                    HashSet<String> oldParticipants = new HashSet<>();
                    for (String memberId : participants) {
                        oldParticipants.add(memberId);
                    }
                    if (!new HashSet<>(participants).equals(oldParticipants)) {
                        this.mParticipants = new HashMap<>();
                        for (String participantID : participants) {
                            try {
                                mParticipants.put(participantID, PFMember.fetchExistingMember(participantID));
                            } catch (PFException e) {
                                // Just do not add this guy :)
                            }
                        }
                    }
                }
                for (PFMember member : mParticipants.values()) {
                    member.reload();
                }
            }
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    @Override
    protected void updateToServer(final String entry) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_EVENT);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        switch (entry) {
                            case EVENT_ENTRY_TITLE:
                                object.put(EVENT_ENTRY_TITLE, mTitle);
                                break;
                            case EVENT_ENTRY_DESCRIPTION:
                                object.put(EVENT_ENTRY_DESCRIPTION, mDescription);
                                break;
                            case EVENT_ENTRY_DATE:
                                object.put(EVENT_ENTRY_DATE, mDate);
                                break;
                            case EVENT_ENTRY_PLACE:
                                object.put(EVENT_ENTRY_PLACE, mPlace);
                                break;
                            case EVENT_ENTRY_PARTICIPANTS:
                                object.put(EVENT_ENTRY_PARTICIPANTS, collectionToArray(mParticipants.values()));
                                break;
                            case EVENT_ENTRY_PICTURE:
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] image = stream.toByteArray();
                                ParseFile file = new ParseFile(String.format("event%s.png", getId()), image);
                                file.saveInBackground();
                                object.put(EVENT_ENTRY_PICTURE, mPicture);
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
     * Delete the current event on the server
     *
     * @throws PFException if something bad happened while communicating with the serve
     */
    public void delete() throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_EVENT);
        try {
            ParseObject object = query.get(getId());
            object.delete();
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    public static PFEvent createEvent(PFGroup group, String name, String place, Date date, List<PFMember> participants, String description, Bitmap picture) throws PFException {
        ParseObject object = new ParseObject(EVENT_TABLE_NAME);
        object.put(EVENT_ENTRY_TITLE, name);
        object.put(EVENT_ENTRY_PLACE, place);
        object.put(EVENT_ENTRY_DATE, date);
        object.put(EVENT_ENTRY_DESCRIPTION, description);
//        object.put(EVENT_ENTRY_PICTURE, picture);

        JSONArray participantsIds = new JSONArray();
        for (PFMember member : participants) {
            participantsIds.put(member.getId());
        }
        object.put(EVENT_ENTRY_PARTICIPANTS, participantsIds);

        if (picture != null) {
            object.put(EVENT_ENTRY_PICTURE, picture);
        }

        try {
            object.save();
            String id = object.getObjectId();
            PFEvent event = new PFEvent(id, object.getUpdatedAt(), name, place, date, description, participants, picture);
            group.addEvent(event);
            return event;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new PFException();
        }
    }

    public static PFEvent createEvent(PFGroup group, String name, String place, Date date, String description, List<String> participants, Bitmap picture) throws PFException {
        List<PFMember> members = new ArrayList<>();

        for (String participantID : participants) {
            try {
                members.add(PFMember.fetchExistingMember(participantID));
            } catch (PFException e) {
                // Just do not add this guy :)
            }
        }
        return createEvent(group, name, place, date, members, description, picture);
    }

    public static PFEvent fetchExistingEvent(String id) throws PFException {
        if (id == null) {
            throw new PFException();
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_EVENT);
        try {
            ParseObject object = query.get(id);
            if (object != null) {

                String title = object.getString(PFConstants.EVENT_ENTRY_TITLE);
                String description = object.getString(EVENT_ENTRY_DESCRIPTION);
                String place = object.getString(EVENT_ENTRY_PLACE);
                Date date = object.getDate(EVENT_ENTRY_DATE);

                Bitmap[] picture = {null};
                retrieveFileFromServer(object, EVENT_ENTRY_PICTURE, picture);
                String[] groupsArray = convertFromJSONArray(object.getJSONArray(EVENT_ENTRY_PARTICIPANTS));
                List<String> participants = new ArrayList<>(Arrays.asList(groupsArray));

                List<PFMember> members = new ArrayList<>();

                for (String participantID : participants) {
                    try {
                        members.add(PFMember.fetchExistingMember(participantID));
                    } catch (PFException e) {
                        // Just do not add this guy :)
                    }
                }

                return new PFEvent(id, object.getUpdatedAt(), title, place, date, description, members, picture[0]);
            } else {
                throw new PFException("Parse query for id " + id + " failed");
            }
        } catch (ParseException e) {
            throw new PFException("Parse query for id " + id + " failed");
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(dateToString(this.lastModified));
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(dateToString(mDate));
        dest.writeString(mPlace);
        dest.writeParcelable(mPicture, flags);
        List<String> participantKeys = new ArrayList<>();
        List<PFMember> participants = new ArrayList<>();
        for(String s : mParticipants.keySet()) {
            participantKeys.add(s);
            participants.add(mParticipants.get(s));
        }
        dest.writeStringList(participantKeys);
        Parcelable[] array = new Parcelable[participants.size()];
        dest.writeParcelableArray(participants.toArray(array), 0);
        //dest.writeTypedList(participants);
    }

    @Override
    public int compareTo(PFEvent another) {
        return mDate.compareTo(another.mDate);
    }
}
