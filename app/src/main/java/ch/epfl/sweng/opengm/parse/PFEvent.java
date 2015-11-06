package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.events.Utils;

import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_DATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_PARTICIPANTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_PLACE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_ENTRY_TITLE;
import static ch.epfl.sweng.opengm.parse.PFConstants.USER_ENTRY_PICTURE;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;
import static ch.epfl.sweng.opengm.parse.PFUtils.retrieveFileFromServer;

public final class PFEvent extends PFEntity implements Parcelable {

    private final static String PARSE_TABLE_EVENT = PFConstants.EVENT_TABLE_NAME;

    private String mTitle;
    private String mDescription;
    private Date mDate;
    private String mPlace;
    private Bitmap mPicture;
    private List<PFMember> mParticipants;

    public PFEvent() {
        super(null,null);
    }

    public PFEvent(Parcel in) {
        super(in.readString(), PARSE_TABLE_EVENT);
        mTitle = in.readString();
        mDescription = in.readString();
        mDate = Utils.stringToDate(in.readString());
        mPlace = in.readString();
        mPicture = in.readParcelable(Bitmap.class.getClassLoader());
        Parcelable[] members = in.readParcelableArray(PFMember.class.getClassLoader());
        mParticipants = new ArrayList<>();
        for(Parcelable p : members) {
            mParticipants.add((PFMember) p);
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

    private PFEvent(String id, String name, String place, Date date, String description, List<String> participants, Bitmap picture) {
        super(id, PARSE_TABLE_EVENT);
        this.mTitle = name;
        this.mPlace = place;
        this.mDate = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes());
        this.mDescription = description;
        this.mParticipants = new ArrayList<>();
        for (String participantID : participants) {
            try {
                mParticipants.add(PFMember.fetchExistingMember(participantID));
            } catch (PFException e) {
                // Just do not add this guy :)
            }
        }
        this.mPicture = picture;
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

    public List<PFMember> getParticipants() {
        return mParticipants;
    }

    public void setParticipants(List<PFMember> mParticipants) {
        this.mParticipants = mParticipants;
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
//                            case EVENT_ENTRY_PARTICIPANTS:
//                                object.put(EVENT_ENTRY_PARTICIPANTS, ???);
//                                break;
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
                retrieveFileFromServer(object, USER_ENTRY_PICTURE, picture);
                String[] groupsArray = convertFromJSONArray(object.getJSONArray(EVENT_ENTRY_PARTICIPANTS));
                List<String> participants = new ArrayList<>(Arrays.asList(groupsArray));

                return new PFEvent(id, title, place, date, description, participants, picture[0]);
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
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(Utils.dateToString(mDate));
        dest.writeString(mPlace);
        dest.writeParcelable(mPicture, flags);
        dest.writeParcelableArray(mParticipants.toArray(new PFMember[0]), flags);
    }
}
