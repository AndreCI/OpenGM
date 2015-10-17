package ch.epfl.sweng.opengm.parse;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_TABLE_DATE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_TABLE_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_TABLE_PLACE;
import static ch.epfl.sweng.opengm.parse.PFConstants.EVENT_TABLE_TITLE;

public class PFEvent extends PFEntity {

    private final static String PARSE_TABLE_EVENT = PFConstants.EVENT_TABLE_NAME;

    private String mTitle;
    private String mDescription;
    private String mDate;  // Gregorian Calendar Date ???
    private String mPlace;
//    private Bitmap mPicture;
    private List<PFMember> mParticipants;

    public PFEvent() {
        super(null, null);
    }

    @Override
    protected void updateToServer(final String entry) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_EVENT);
        query.getInBackground(getId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        switch (entry) {
                            case EVENT_TABLE_TITLE:
                                object.put(EVENT_TABLE_TITLE, mTitle);
                                break;
                            case EVENT_TABLE_DESCRIPTION:
                                object.put(EVENT_TABLE_DESCRIPTION, mDescription);
                                break;
                            case EVENT_TABLE_DATE:
                                object.put(EVENT_TABLE_DATE, mDate);
                                break;
                            case EVENT_TABLE_PLACE:
                                object.put(EVENT_TABLE_PLACE, mPlace);
                                break;
//                            case EVENT_TABLE_PARTICIPANTS:
//                                object.put(EVENT_TABLE_PARTICIPANTS, ???);
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


    public static class Builder extends PFEntity.Builder {

        public Builder(String id) {
            super(id);
        }

        @Override
        protected void retrieveFromServer() throws PFException {

        }

        public PFEvent build() {
            return new PFEvent();
        }
    }
}
