package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_PARTICIPANTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_PLACE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_TITLE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.collectionToArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;

public final class PFEvent extends PFEntity implements Parcelable, Comparable<PFEvent> {
    private final static String PARSE_TABLE_EVENT = PFConstants.EVENT_TABLE_NAME;

    private String mTitle;
    private String mDescription;
    private Date mDate;
    private String mPlace;
    private String mPicturePath;
    private String mPictureName;
    private Bitmap mPicture;
    private HashMap<String, PFMember> mParticipants;
    private boolean participantsGet = false;

    public PFEvent(Parcel in) {
        super(in, PARSE_TABLE_EVENT);
        mTitle = in.readString();
        mDescription = in.readString();
        mDate = Utils.stringToDate(in.readString());
        mPlace = in.readString();
        mPicturePath = in.readString();
        mPictureName = in.readString();
        mPicture=null;
        ArrayList<String> ids = in.createStringArrayList();
        mParticipants = new HashMap<>();
        for(String s : ids){
            try {
                mParticipants.put(s, PFMember.fetchExistingMember(s));
            } catch (PFException e) {
            }
        }
      /*  new AsyncTask<List<String>, Void, Void>(){

            @Override
            protected Void doInBackground(List<String>... params) {
                for(String s : params[0]){
                    try {
                        mParticipants.put(s, PFMember.fetchExistingMember(s));
                    } catch (PFException e) {

                    }
                }
                return null;
            }
        }.execute(ids);*/
      /*  List<String> participantKeys = in.createStringArrayList();
        Parcelable[] array = in.readParcelableArray(PFMember.class.getClassLoader());
        List<PFMember> participants = new ArrayList<>();
        for(Parcelable parcelable : array) {
            participants.add((PFMember) parcelable);
        }
        mParticipants = new HashMap<>();
        for(int i = 0; i < participants.size(); ++i) {
            mParticipants.put(participantKeys.get(i), participants.get(i));
        }*/
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

    private PFEvent(String id, Date updated, String name, String place, Date date,
                    String description, List<PFMember> participants, String picturePath,
                    String pictureName, Bitmap picture) {
        super(id, PARSE_TABLE_EVENT, updated);
        this.mTitle = name;
        this.mPlace = place;
        this.mDate = new Date(date.getYear(), date.getMonth(), date.getDate(),
                date.getHours(), date.getMinutes());
        this.mDescription = description;
        this.mParticipants = new HashMap<>();
        for(PFMember p : participants) {
            mParticipants.put(p.getId(), p);
        }
        this.mPicturePath=picturePath;
        this.mPictureName = pictureName;
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

    public String getPicturePath() {return mPicturePath;}

    public void setPicturePath(String s){this.mPicturePath=s;}

    public String getPictureName() {return mPictureName;}

    public void setPictureName(String s){this.mPictureName=s;}

    public Bitmap getPicture() {
        return mPicture;
    }

    public void setPicture(Bitmap mPicture) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        mPicture.compress(Bitmap.CompressFormat.PNG, 40, out);
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
        //TODO : gerer les images
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

                    String[] groupsArray = convertFromJSONArray(
                            object.getJSONArray(EVENT_ENTRY_PARTICIPANTS));
                    List<String> participants = new ArrayList<>(Arrays.asList(groupsArray));

                    HashSet<String> oldParticipants = new HashSet<>();
                    for (String memberId : participants) {
                        oldParticipants.add(memberId);
                    }
                    if (!new HashSet<>(participants).equals(oldParticipants)) {
                        this.mParticipants = new HashMap<>();
                        for (String participantID : participants) {
                            try {
                                mParticipants.put(participantID,
                                        PFMember.fetchExistingMember(participantID));
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
                                object.put(EVENT_ENTRY_PARTICIPANTS,
                                        collectionToArray(mParticipants.values()));
                                break;
                            case EVENT_ENTRY_PICTURE:
                                if (mPicture != null) {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    mPicture.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] imageData = stream.toByteArray();
                                    ParseFile image = new ParseFile(getName()+ ".png", imageData);
                                  // image.saveInBackground();
                                    object.put(EVENT_ENTRY_PICTURE, image);
                                } else if(mPicturePath!=PFUtils.pathNotSpecified){
                                    object.remove(EVENT_ENTRY_PICTURE);
                                }
                                break;
                            default:
                                return;
                        }
                        object.saveInBackground();
                    }
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

    public static void deleteWithId(String id) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_EVENT);
        try {
            ParseObject object = query.get(id);
            object.delete();
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    public void updateAllFields() throws PFException {
        updateToServer(EVENT_ENTRY_DATE);
        updateToServer(EVENT_ENTRY_TITLE);
        updateToServer(EVENT_ENTRY_DESCRIPTION);
        updateToServer(EVENT_ENTRY_PARTICIPANTS);
        updateToServer(EVENT_ENTRY_PLACE);
    }

    public static PFEvent createEvent(PFGroup group, String name, String place, Date date,
                                      List<PFMember> participants, String description,
                                      String picturePath, String pictureName, Bitmap picture)
            throws PFException {
        ParseObject object = new ParseObject(EVENT_TABLE_NAME);
        object.put(EVENT_ENTRY_TITLE, name);
        object.put(EVENT_ENTRY_PLACE, place);
        object.put(EVENT_ENTRY_DATE, date);
        object.put(EVENT_ENTRY_DESCRIPTION, description);

        JSONArray participantsIds = new JSONArray();
        for (PFMember member : participants) {
            participantsIds.put(member.getId());
        }
        object.put(EVENT_ENTRY_PARTICIPANTS, participantsIds);
        //TODO : here we write the bitmap through a ParseFile in the internet
        if (picture != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ParseFile file = new ParseFile(pictureName+".png", image);
           // file.saveInBackground();
            object.put(EVENT_ENTRY_PICTURE, file);
        }

        try {
            object.save();
            String id = object.getObjectId();
            PFEvent event = new PFEvent(id, object.getUpdatedAt(), name, place, date,
                    description, participants, picturePath, pictureName, picture);
            group.addEvent(event);
            return event;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new PFException();
        }
    }


    public static PFEvent fetchExistingEvent(String id, PFGroup group) throws PFException {
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

                String[] groupsArray = convertFromJSONArray(
                        object.getJSONArray(EVENT_ENTRY_PARTICIPANTS));
                List<String> participants = new ArrayList<>(Arrays.asList(groupsArray));

                List<PFMember> members = new ArrayList<>();

                for (String participantID : participants) {
                    members.add(group.getMember(participantID));
                }
                String imagePath = PFUtils.pathNotSpecified;
                String imageName = PFUtils.nameNotSpecified;
                ParseFile imageFile = (ParseFile) object.get(EVENT_ENTRY_PICTURE);
                final PFEvent event =  new PFEvent(id, object.getUpdatedAt(), title, place, date,
                        description, members, imagePath, imageName, null);
                if (imageFile != null) {
                    imageFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                            event.setPicture(picture);
                        }
                    });
                }
                return event;
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
        dest.writeString(mPicturePath);
        dest.writeString(mPictureName);
        List<String> participantKeys = new ArrayList<>();
        //List<PFMember> participants = new ArrayList<>();


        for(String s : mParticipants.keySet()) {
            participantKeys.add(s);
          //  participants.add(mParticipants.get(s));
        }
        dest.writeStringList(participantKeys);
        //Parcelable[] array = new Parcelable[participants.size()];
        //dest.writeParcelableArray(participants.toArray(array), 0);
        //dest.writeTypedList(participants);
    }

    @Override
    public int compareTo(PFEvent another) {
        return mDate.compareTo(another.mDate);
    }
}
