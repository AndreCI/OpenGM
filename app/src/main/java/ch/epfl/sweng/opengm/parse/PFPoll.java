package ch.epfl.sweng.opengm.parse;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sweng.opengm.events.Utils;

import static ch.epfl.sweng.opengm.parse.PFConstants.OBJECT_ID;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_ANSWERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_DEADLINE;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_DESCRIPTION;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_NAME;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_NUMBER_ANSWERS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_PARTICIPANTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_ENTRY_RESULTS;
import static ch.epfl.sweng.opengm.parse.PFConstants.POLL_TABLE_NAME;
import static ch.epfl.sweng.opengm.parse.PFUtils.convertFromJSONArray;

public class PFPoll extends PFEntity {

    private final static String PARSE_TABLE_POLL = POLL_TABLE_NAME;


    private final String mName;
    private final String mDescription;
    private final int nOfAnswers;
    private final HashMap<String, PFMember> mParticipants;
    private final List<Answer> mAnswers;
    private final Date mDeadline;
    private final boolean isOpen;

    private PFPoll(String id, Date updated, String name, Date deadline, String description, List<Answer> answers, int nOfAnswers, List<PFMember> participants) {
        super(id, PARSE_TABLE_POLL, updated);
        this.mName = name;
        this.mDescription = description;
        this.mParticipants = new HashMap<>();
        for (PFMember member : participants) {
            mParticipants.put(member.getId(), member);
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

        List<String> answers = in.createStringArrayList();
        List<String> values = in.createStringArrayList();

        // TODO handles this

        mAnswers = new ArrayList<>();

        List<String> participantKeys = in.createStringArrayList();
        Parcelable[] array = in.readParcelableArray(PFMember.class.getClassLoader());
        List<PFMember> participants = new ArrayList<>();
        for (Parcelable parcelable : array) {
            participants.add((PFMember) parcelable);
        }
        mParticipants = new HashMap<>();
        for (int i = 0; i < participants.size(); ++i) {
            mParticipants.put(participantKeys.get(i), participants.get(i));
        }
    }

    @Override
    public void reload() throws PFException {

    }

    @Override
    protected void updateToServer(String entry) throws PFException {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

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

    public static PFPoll fetchExistingPoll(String id) throws PFException {
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

                String[] votesArray = convertFromJSONArray(object.getJSONArray(POLL_ENTRY_RESULTS));
                String[] answersArray = convertFromJSONArray(object.getJSONArray(POLL_ENTRY_ANSWERS));
                String[] membersArray = convertFromJSONArray(object.getJSONArray(POLL_ENTRY_PARTICIPANTS));

                List<Answer> answers = new ArrayList<>();

                for (int i = 0; i < answersArray.length; i++) {
                    answers.add(new Answer(answersArray[i], votesArray[i]));
                }

                List<String> participants = new ArrayList<>(Arrays.asList(membersArray));

                List<PFMember> members = new ArrayList<>();

                for (String participantID : participants) {
                    try {
                        members.add(PFMember.fetchExistingMember(participantID));
                    } catch (PFException e) {
                        // Just do not add this guy :)
                    }
                }
                return new PFPoll(id, object.getUpdatedAt(), name, deadline, description, answers, nAnswers, members);
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
        object.put(POLL_ENTRY_DEADLINE, deadline);

        List<Answer> answers = new ArrayList<>();
        JSONArray propositions = new JSONArray();
        JSONArray votes = new JSONArray();
        for (String response : responses) {
            answers.add(new Answer(response));
            propositions.put(response);
            votes.put(0);
        }

        JSONArray participants = new JSONArray();
        for (PFMember member : members) {
            participants.put(member.getId());
        }

        object.put(POLL_ENTRY_ANSWERS, propositions);
        object.put(POLL_ENTRY_RESULTS, votes);
        object.put(POLL_ENTRY_PARTICIPANTS, participants);

        try {
            object.save();
            String id = object.getObjectId();
            PFPoll poll = new PFPoll(id, object.getUpdatedAt(), name, deadline, description, answers, nOfAnswers, members);
            group.addPoll(poll);
            return poll;
        } catch (ParseException e) {
            throw new PFException();
        }
    }


    public static class Answer {

        private final String mAnswer;
        private int nVotes;

        public Answer(String answer) {
            this(answer, 0);
        }

        public Answer(String answer, String vote) {
            this(answer, vote == null ? 0 : Integer.parseInt(vote));
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
}
