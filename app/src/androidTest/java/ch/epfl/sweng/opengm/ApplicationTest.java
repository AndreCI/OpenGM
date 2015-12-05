package ch.epfl.sweng.opengm;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest {

    private String userId = Calendar.getInstance().toString() + "u";
    private PFGroup group = null;

    @Before
    public void initiateTest() {
        OpenGMApplication.logOut();
        OpenGMApplication.setCurrentGroup(-1);
    }

    @Test
    public void testLogout() {
        try {
            PFUser u = PFUser.createNewUser(userId, "", "", "", "", "");
            OpenGMApplication.setCurrentUser(u);
            assertNotNull(OpenGMApplication.getCurrentUser());
            OpenGMApplication.logOut();
            assertNull(OpenGMApplication.getCurrentUser());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testUserGetterNotInitialized() {
        assertNull(OpenGMApplication.getCurrentUser());
    }

    @Test
    public void testGroupGetterNotInitialized() {
        assertNull(OpenGMApplication.getCurrentGroup());
    }

    @Test
    public void testUserSetterNullId() {
        assertNull(OpenGMApplication.setCurrentUser(null));
    }

    @Test
    public void testGroupSetterNullId() {
        try {
            PFUser u = PFUser.createNewUser(userId, "", "", "", "", "");
            OpenGMApplication.setCurrentUser(u);
            OpenGMApplication.setCurrentGroup(null);
            assertNull(OpenGMApplication.getCurrentGroup());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testUserSetterCreate() {
        try {
            PFUser u = PFUser.createNewUser(userId, "", "", "", "", "");
            OpenGMApplication.setCurrentUser(u);
            assertEquals(u, OpenGMApplication.getCurrentUser());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testUserSetterFetch() {
        try {
            PFUser u = PFUser.createNewUser(userId, "", "", "", "", "");
            OpenGMApplication.setCurrentUserWithId(userId);
            assertEquals(u, OpenGMApplication.getCurrentUser());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testGroupSetterCreate() {
        try {
            PFUser u = PFUser.createNewUser(userId, "", "", "", "", "");
            PFGroup g = PFGroup.createNewGroup(u, "", "", null);
            u.fetchGroupWithId(g.getId());
            OpenGMApplication.setCurrentUser(u);
            OpenGMApplication.setCurrentGroup(g);
            assertEquals(g, OpenGMApplication.getCurrentGroup());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testGroupSetterFetch() {
        try {
            PFUser u = PFUser.createNewUser(userId, "", "", "", "", "");
            PFGroup g = PFGroup.createNewGroup(u, "", "", null);
            u.fetchGroupWithId(g.getId());
            OpenGMApplication.setCurrentUser(u);
            OpenGMApplication.setCurrentGroup(u.getGroups().get(0));
            assertEquals(g, OpenGMApplication.getCurrentGroup());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testGroupSetterNoGroup() {
        try {
            PFUser u = PFUser.createNewUser(userId, "", "", "", "", "");
            PFGroup g = PFGroup.createNewGroup(u, "", "", null);
            u.fetchGroupWithId(g.getId());
            OpenGMApplication.setCurrentUser(u);
            OpenGMApplication.setCurrentGroup(u.getGroups().get(0));
            assertEquals(g, OpenGMApplication.getCurrentGroup());
            OpenGMApplication.setCurrentGroup(-1);
            assertNull(OpenGMApplication.getCurrentGroup());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @Test
    public void testUserSetterAlreadySet() {
        try {
            PFUser u1 = PFUser.createNewUser(userId, "", "", "", "", "");
            PFUser u2 = PFUser.createNewUser(userId + "2", "", "", "", "", "");
            OpenGMApplication.setCurrentUser(u1);
            assertEquals(u1, OpenGMApplication.setCurrentUser(u1));
            OpenGMApplication.setCurrentUser(u2);
            assertEquals(u1, OpenGMApplication.setCurrentUser(u1));
            deleteUserWithId(u2.getId());
        } catch (PFException e) {
            Assert.fail("Network error");
        }
    }

    @After
    public void deleteDatabase() {
        deleteUserWithId(userId);
        if (group != null) {
            group.deleteGroup();
        }
    }

}