package ch.epfl.sweng.opengm.parse;

import android.os.Parcel;
import android.os.Parcelable;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.UtilsTest;

import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PFPollTest {

    private PFUser currentUser;
    private PFGroup currentGroup;
    private PFPoll currentPoll;

    @Before
    public void newIds() throws PFException {
        currentUser = PFUser.createNewUser(UtilsTest.getRandomId(), "name", "0123456789", "email", "firstname", "lastname");
        currentGroup = PFGroup.createNewGroup(currentUser, "name", "description", null);
        currentPoll = null;
    }

    @Test
    public void testGetters() {

        String name = "Poll test getters";
        String description = "Description for the getters test";
        Date deadline = new Date(1, 1, 1);
        List<String> answers = new ArrayList<>(singletonList("Do you like it?"));
        int nOfAnswers = 1;
        List<PFMember> members = new ArrayList<>();

        try {
            currentPoll = PFPoll.createNewPoll(currentGroup, name, description, nOfAnswers, answers, deadline, members);
            assertEquals(name, currentPoll.getName());
            assertEquals(description, currentPoll.getDescription());
            assertEquals(deadline, currentPoll.getDeadline());
            assertEquals(answers.size(), currentPoll.getAnswers().size());
            assertEquals(answers.get(0), currentPoll.getAnswers().get(0).getAnswer());
            assertEquals(0, currentPoll.getAnswers().get(0).getVotes());
            assertEquals(nOfAnswers, currentPoll.getNOfAnswers());
            assertEquals(members.size(), currentPoll.getParticipants().size());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testFetch() {
        String name = "Poll test fetch";
        String description = "Description for the fetch test";
        Date deadline = new Date(1, 1, 1);
        List<String> answers = new ArrayList<>(singletonList("Do you like it bis?"));
        int nOfAnswers = 1;
        List<PFMember> members = new ArrayList<>();

        try {
            currentPoll = PFPoll.createNewPoll(currentGroup, name, description, nOfAnswers, answers, deadline, members);
            PFPoll poll2 = PFPoll.fetchExistingPoll(currentPoll.getId());
            assertEquals(currentPoll.getName(), poll2.getName());
            assertEquals(currentPoll.getDescription(), poll2.getDescription());
            assertEquals(currentPoll.getDeadline(), poll2.getDeadline());
            assertEquals(currentPoll.getAnswers(), poll2.getAnswers());
            assertEquals(currentPoll.getNOfAnswers(), poll2.getNOfAnswers());
            assertEquals(currentPoll.getParticipants(), poll2.getParticipants());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @After
    public void deleteAfterTesting() {
        if (currentGroup != null) {
            currentGroup.deleteGroup();
        }
        UtilsTest.deleteUserWithId(currentUser.getId());
        if (currentPoll != null)
            try {
                currentPoll.delete();
            } catch (PFException e) {
                // Ok that's fine
            }
    }


    @Test
    public void writeToParcel() {
        String name = "When should we meet?";
        String description = "Sweng meeting";
        Date deadline = new Date(1, 1, 1);
        List<String> answers = new ArrayList<>(asList("Monday", "Tuesday", "Wednesday"));
        int nOfAnswers = 2;
        List<PFMember> members = new ArrayList<>();

        try {
            PFMember member = PFMember.fetchExistingMember(currentUser.getId());
            members.add(member);
        } catch (PFException e) {
            fail("Network error");
        }

        try {
            currentPoll = PFPoll.createNewPoll(currentGroup, name, description, nOfAnswers, answers, deadline, members);
        } catch (PFException e) {
            fail("Network error");
        }

        Parcel parcel = Parcel.obtain();
        currentPoll.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        assertEquals(currentPoll.getId(), parcel.readString());
        parcel.readString();
        assertEquals(name, parcel.readString());
        assertEquals(description, parcel.readString());
        assertEquals(dateToString(currentPoll.getDeadline()), parcel.readString());
        assertEquals(nOfAnswers, parcel.readInt());
        assertEquals(0, parcel.readByte());

        ArrayList<String> answersKeys = parcel.createStringArrayList();
        assertEquals(answers.size(), answersKeys.size());
        assertEquals(answers, answersKeys);

        int[] answersArray = parcel.createIntArray();
        assertEquals(answers.size(), answersArray.length);
        assertEquals(0, answersArray[0]);

        ArrayList<String> participantKeys = parcel.createStringArrayList();
        assertEquals(members.size(), participantKeys.size());
        assertEquals(currentUser.getId(), participantKeys.get(0));

        boolean[] votersArray = parcel.createBooleanArray();
        assertEquals(members.size(), votersArray.length);
        assertEquals(false, votersArray[0]);

        ArrayList<PFMember> participants = new ArrayList<>(participantKeys.size());
        Parcelable[] parcelables = parcel.readParcelableArray(PFMember.class.getClassLoader());
        for (Parcelable parcelable : parcelables) {
            participants.add((PFMember) parcelable);
        }
        assertEquals(members.size(), participants.size());
        assertEquals(members.get(0), participants.get(0));
    }

    @Test
    public void createFromParcel() {
        Parcel parcel = Parcel.obtain();

        String id = "123456789";
        Date date = new Date(10, 10, 2000);

        String name = "When should we meet?";
        String description = "Sweng meeting";
        Date deadline = new Date(1, 1, 1);
        List<String> answers = new ArrayList<>(asList("Monday", "Tuesday", "Wednesday"));
        int[] votes = {0, 0, 0};
        boolean[] voters = {false, false, false};
        int nOfAnswers = 2;
        List<PFMember> members = new ArrayList<>();

        try {
            PFMember member = PFMember.fetchExistingMember(currentUser.getId());
            members.add(member);
        } catch (PFException e) {
            fail("Network error");
        }

        parcel.writeString(id);
        parcel.writeString(dateToString(date));
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(dateToString(deadline));
        parcel.writeInt(nOfAnswers);
        parcel.writeByte((byte) 1);

        parcel.writeStringList(answers);
        parcel.writeIntArray(votes);

        List<String> membersIds = new ArrayList<>(singletonList(members.get(0).getId()));

        parcel.writeStringList(membersIds);
        parcel.writeBooleanArray(voters);

        Parcelable[] membersArray = new Parcelable[1];
        membersArray[0] = members.get(0);
        parcel.writeParcelableArray(membersArray, 0);

        parcel.setDataPosition(0);

        currentPoll = new PFPoll(parcel);

        parcel.recycle();

        assertEquals(name, currentPoll.getName());
        assertEquals(description, currentPoll.getDescription());
        assertEquals(dateToString(deadline), dateToString(currentPoll.getDeadline()));
        assertEquals(nOfAnswers, currentPoll.getNOfAnswers());
        assertEquals(true, currentPoll.isOpen());


        assertEquals(answers.size(), currentPoll.getAnswers().size());

        for (int i = 0; i < currentPoll.getAnswers().size(); i++) {
            assertEquals(answers.get(i), currentPoll.getAnswers().get(i).getAnswer());
            assertEquals(0, currentPoll.getAnswers().get(i).getVotes());
        }

        assertEquals(members.size(), currentPoll.getParticipants().size());

        for (int i = 0; i < members.size(); i++) {
            String mId = members.get(i).getId();
            assertTrue(currentPoll.getParticipants().containsKey(mId));
            assertFalse(currentPoll.getVoters().get(mId));
        }

    }


}
