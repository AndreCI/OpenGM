package ch.epfl.sweng.opengm.parse;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.opengm.UtilsTest;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;

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
    public void deleteAfterTesting() throws PFException {
        if (currentGroup != null) {
            currentGroup.deleteGroup();
        }
        UtilsTest.deleteUserWithId(currentUser.getId());
        if (currentPoll != null)
            currentPoll.delete();
    }

}
