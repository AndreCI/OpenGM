package ch.epfl.sweng.opengm.parse;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.opengm.events.Utils;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_ANSWERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_DEADLINE;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_NUMBER_ANSWERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_PARTICIPANTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_RESULTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_VOTERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;

public class PFPoll extends PFEntity implements Comparable<PFPoll> {

    private final static String PARSE_TABLE_POLL = POLL_TABLE_NAME;


    private final String mName;
    private final String mDescription;
    private final int nOfAnswers;
    private final HashMap<String, PFMember> mParticipants;
    private final HashMap<String, Boolean> mVoters;
    private final List<Answer> mAnswers;
    private final Date mDeadline;
    private final boolean isOpen;

    private PFPoll(String id, Date updated, String name, Date deadline, String description, List<Answer> answers, int nOfAnswers, List<PFMember> participants, List<Boolean> hasParticpantVoted) {
        super(id, PARSE_TABLE_POLL, updated);
        this.mName = name;
        this.mDescription = description;
        this.mParticipants = new HashMap<>();
        this.mVoters = new HashMap<>();
        int i = 0;
        for (PFMember member : participants) {
            mParticipants.put(member.getId(), member);
            mVoters.put(member.getId(), hasParticpantVoted.get(i++));
        }
        this.mAnswers = new ArrayList<>(answers);
        this.nOfAnswers = nOfAnswers;
        this.isOpen = deadline.after(new Date());
        this.mDeadline = deadline;
    }


    protected PFPoll(Parcel in) {
        super(in, PARSE_TABLE_POLL);
        this.mName = in.readString();
        this.mDescription = in.readString();
        this.mDeadline = Utils.stringToDate(in.readString());
        this.nOfAnswers = in.readInt();
        this.isOpen = in.readByte() != 0;

        List<String> answersQuestions = in.createStringArrayList();
        int[] answersVote = in.createIntArray();

        List<String> participantIds = in.createStringArrayList();
        boolean[] voters = in.createBooleanArray();
        Parcelable[] array = in.readParcelableArray(PFMember.class.getClassLoader());

        mAnswers = new ArrayList<>();

        int i = 0;
        for (String answer : answersQuestions) {
            mAnswers.add(new Answer(answer, answersVote[i++]));
        }

        mVoters = new HashMap<>();
        mParticipants = new HashMap<>();
        i = 0;
        for (Parcelable parcelable : array) {
            PFMember member = (PFMember) parcelable;
            String id = participantIds.get(i);
            boolean hasVoted = voters[i++];
            mVoters.put(id, hasVoted);
            mParticipants.put(id, member);
        }
    }

    @Override
    public void reload() throws PFException {

    }

    @Override
    protected void updateToServer(final String entry) throws PFException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_POLL);
        query.whereEqualTo(OBJECT_ID, getId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object != null) {
                        switch (entry) {
                            case POLL_ENTRY_ANSWERS:
                                JSONArray votes = new JSONArray();
                                JSONArray votersIds = new JSONArray();
                                JSONArray voters = new JSONArray();
                                for (Map.Entry<String, Boolean> voter : mVoters.entrySet()) {
                                    votersIds.put(voter.getKey());
                                    voters.put(voter.getValue());
                                }
                                for (Answer answer : mAnswers) {
                                    votes.put(answer.getVotes());
                                }
                                object.put(POLL_ENTRY_RESULTS, votes);
                                object.put(POLL_ENTRY_PARTICIPANTS, votersIds);
                                object.put(POLL_ENTRY_VOTERS, voters);
                                break;
                            default:
                                return;
                        }
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.d("ERROR 1", "ERROR 1");
                                    e.printStackTrace();
                                    // throw new ParseException("No object for the selected id.");
                                }
                            }
                        });
                    } else {
                        Log.d("ERROR 2", "ERROR 2");
                        // throw new ParseException(1,"");
                    }
                } else {
                    Log.d("ERROR 3", "ERROR 3");
                    e.printStackTrace();
                    // throw new ParseException("Error while sending the request to the server");
                }
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public HashMap<String, Boolean> getVoters() {
        return new HashMap<>(mVoters);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(dateToString(this.lastModified));
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(dateToString(mDeadline));
        dest.writeInt(nOfAnswers);
        dest.writeByte((byte) (isOpen ? 1 : 0));

        List<String> answersQuestions = new ArrayList<>();
        int[] answersArray = new int[mAnswers.size()];

        int i = 0;
        for (Answer answer : mAnswers) {
            answersQuestions.add(answer.getAnswer());
            answersArray[i++] = answer.getVotes();
        }

        dest.writeStringList(answersQuestions);
        dest.writeIntArray(answersArray);

        List<String> participantKeys = new ArrayList<>();
        List<PFMember> participants = new ArrayList<>();
        boolean[] voters = new boolean[mVoters.size()];
        i = 0;
        for (Map.Entry<String, PFMember> entry : mParticipants.entrySet()) {
            participantKeys.add(entry.getKey());

            participants.add(entry.getValue());
            voters[i++] = mVoters.get(entry.getKey());
        }

        dest.writeStringList(participantKeys);
        dest.writeBooleanArray(voters);
        Parcelable[] array = new Parcelable[participants.size()];
        dest.writeParcelableArray(participants.toArray(array), 0);
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getNOfAnswers() {
        return nOfAnswers;
    }

    public HashMap<String, PFMember> getParticipants() {
        return mParticipants;
    }

    public List<Answer> getAnswers() {
        return mAnswers;
    }

    public Date getDeadline() {
        return mDeadline;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void updateAnswers(String userId) throws PFException {
        if (userId == null && !mVoters.containsKey(userId)) {
            throw new PFException();
        }
        if (!mVoters.get(userId)) {
            mVoters.put(userId, true);
            try {
                updateToServer(POLL_ENTRY_ANSWERS);
                Log.d("SUCCESS", "UPDATE ANSWERS SUCCESS");
            } catch (PFException e) {
                mVoters.put(userId, false);
                throw new PFException(e);
            }
        }
    }

    public void increaseVoteForAnswer(Answer answer) {
        mAnswers.get(mAnswers.indexOf(answer)).increaseVote();
    }

    public boolean isUserEnrolled(String userId) {
        return mVoters.containsKey(userId);
    }

    public boolean hasUserAlreadyVoted(String userId) {
        if (!mVoters.containsKey(userId))
            return false;
        return mVoters.get(userId);
    }

    public void delete() throws PFException {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(POLL_TABLE_NAME);
        query1.whereEqualTo(OBJECT_ID, getId());
        try {
            ParseObject obj = query1.getFirst();
            obj.delete();
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    public static PFPoll fetchExistingPoll(String id, PFGroup group) throws PFException {
        if (id == null) {
            throw new PFException("Id is null");
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_TABLE_POLL);
        query.whereEqualTo(PFConstants.OBJECT_ID, id);
        try {
            ParseObject object = query.getFirst();
            if (object != null) {

                String name = object.getString(POLL_ENTRY_NAME);
                String description = object.getString(POLL_ENTRY_DESCRIPTION);
                Date deadline = object.getDate(POLL_ENTRY_DEADLINE);
                int nAnswers = object.getInt(POLL_ENTRY_NUMBER_ANSWERS);

                JSONArray votesArray = object.getJSONArray(POLL_ENTRY_RESULTS);
                String[] answersArray = convertFromJSONArray(object.getJSONArray(POLL_ENTRY_ANSWERS));
                String[] membersArray = convertFromJSONArray(object.getJSONArray(POLL_ENTRY_PARTICIPANTS));
                JSONArray votersArray = object.getJSONArray(POLL_ENTRY_VOTERS);

                List<Answer> answers = new ArrayList<>();

                for (int i = 0; i < answersArray.length; i++) {
                    try {
                        answers.add(new Answer(answersArray[i], votesArray.getInt(i)));
                    } catch (JSONException e) {
                        // Just do not add it
                        e.printStackTrace();
                    }
                }

                List<String> participants = new ArrayList<>(Arrays.asList(membersArray));

                List<PFMember> members = new ArrayList<>();
                List<Boolean> voters = new ArrayList<>();
                int i = 0;
                for (String participantID : participants) {
                    try {
                        members.add(group.getMember(participantID));
                        voters.add(votersArray.getBoolean(i));
                    } catch (JSONException e) {
                        throw new PFException("Error while reading datas from the server");
                    }
                    i++;
                }
                return new PFPoll(id, object.getUpdatedAt(), name, deadline, description, answers, nAnswers, members, voters);
            } else {
                throw new PFException("No object found for this idu");
            }
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    public static PFPoll createNewPoll(PFGroup group, String name, String description, int nOfAnswers, List<String> responses, Date deadline, List<PFMember> members) throws PFException {

        ParseObject object = new ParseObject(PARSE_TABLE_POLL);
        object.put(POLL_ENTRY_NAME, name);
        object.put(POLL_ENTRY_DESCRIPTION, description);
        object.put(POLL_ENTRY_NUMBER_ANSWERS, nOfAnswers);
        deadline.setYear(deadline.getYear() - 1900);
        object.put(POLL_ENTRY_DEADLINE, deadline);

        List<Answer> answers = new ArrayList<>();
        JSONArray propositions = new JSONArray();
        JSONArray votes = new JSONArray();
        for (String response : responses) {
            answers.add(new Answer(response));
            propositions.put(response);
            votes.put(0);
        }

        JSONArray voters = new JSONArray();
        ArrayList<Boolean> hasParticipantVoted = new ArrayList<>();
        JSONArray participants = new JSONArray();
        for (PFMember member : members) {
            participants.put(member.getId());
            voters.put(false);
            hasParticipantVoted.add(false);
        }

        object.put(POLL_ENTRY_ANSWERS, propositions);
        object.put(POLL_ENTRY_RESULTS, votes);
        object.put(POLL_ENTRY_PARTICIPANTS, participants);
        object.put(POLL_ENTRY_VOTERS, voters);

        try {
            object.save();
            String id = object.getObjectId();
            PFPoll poll = new PFPoll(id, object.getUpdatedAt(), name, deadline, description, answers, nOfAnswers, members, hasParticipantVoted);
            group.addPoll(poll);
            return poll;
        } catch (ParseException e) {
            throw new PFException();
        }
    }

    @Override
    public int compareTo(PFPoll another) {
        return mDeadline.compareTo(another.mDeadline);
    }

    public static class Answer {

        private final String mAnswer;
        private int nVotes;

        public Answer(String answer) {
            this(answer, 0);
        }

        public Answer(String answer, int vote) {
            this.mAnswer = answer;
            this.nVotes = vote;
        }

        public String getAnswer() {
            return mAnswer;
        }

        public int getVotes() {
            return nVotes;
        }

        public void increaseVote() {
            this.nVotes++;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Answer answer = (Answer) o;

            if (nVotes != answer.nVotes) return false;
            return !(mAnswer != null ? !mAnswer.equals(answer.mAnswer) : answer.mAnswer != null);

        }

        @Override
        public int hashCode() {
            int result = mAnswer != null ? mAnswer.hashCode() : 0;
            result = 31 * result + nVotes;
            return result;
        }
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public PFPoll createFromParcel(Parcel source) {
            return new PFPoll(source);
        }

        @Override
        public PFPoll[] newArray(int size) {
            return new PFPoll[size];
        }
    };
}
