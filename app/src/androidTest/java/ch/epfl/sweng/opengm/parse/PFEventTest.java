package ch.epfl.sweng.opengm.parse;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

import static ch.epfl.sweng.opengm.UtilsTest.deleteUserWithId;
import static ch.epfl.sweng.opengm.UtilsTest.getRandomId;
import static ch.epfl.sweng.opengm.events.Utils.dateToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PFEventTest {

    private static final String PHONE_NUMBER = "0123456789";
    private final String EMAIL = "pf.event@junit.test";
    private final String USERNAME = "jeanInPFEventTest";
    private final String FIRST_NAME = "Jean";
    private final String LAST_NAME = "Sérien";

    private PFGroup group;
    private ArrayList<PFMember> participants;
    private PFEvent event;
    private PFUser user;
    private final String NAME = "name";
    private final String PLACE = "place";
    private final Date DATE = new Date();
    private final String DESCRIPTION = "description";
    private final Bitmap PICTURE = null;

    private String id;

    @Before
    public void newIds() {
        id = null;
        group = null;
        participants = null;
        event = null;
    }

/*    @Test
     public void writeToParcel() {
        id = getRandomId();
        try {
            user = PFUser.createNewUser(id, EMAIL, PHONE_NUMBER, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            e.printStackTrace();
            fail("create new user");
        }
        try {
            group = PFGroup.createNewGroup(user, NAME, DESCRIPTION, PICTURE);
        } catch (PFException e) {
            e.printStackTrace();
            fail("create new group");
        }
        PFMember member = null;
        try {
            member = PFMember.fetchExistingMember(id);
        } catch (PFException e) {
            e.printStackTrace();
            fail("fetch existing user");
        }
        participants = new ArrayList<>();
        participants.add(member);
        try {
            event = PFEvent.createEvent(group, NAME, PLACE, DATE, participants, DESCRIPTION, PICTURE);
        } catch (PFException e) {
            e.printStackTrace();
        }

        Parcel parcel = Parcel.obtain();
        event.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        parcel.readString(); //event id
        parcel.readString(); //last modified
        assertEquals(NAME, parcel.readString());
        assertEquals(DESCRIPTION, parcel.readString());
        assertEquals(dateToString(DATE), parcel.readString());
        assertEquals(PLACE, parcel.readString());
        assertNull(parcel.readParcelable(Bitmap.class.getClassLoader()));
        ArrayList<String> participantKeys = parcel.createStringArrayList();
        assertEquals(1, participantKeys.size());
        assertEquals(id, participantKeys.get(0));
        ArrayList<PFMember> participants = new ArrayList<>(participantKeys.size());
        Parcelable[] parcelables = parcel.readParcelableArray(PFMember.class.getClassLoader());
        for(Parcelable parcelable : parcelables) {
            participants.add((PFMember) parcelable);
        }
        assertEquals(1, participants.size());
        assertEquals(id, participantKeys.get(0));
        assertEquals(id, participants.get(0).getId());
    }

    @Test
    public void createFromParcel() {
        Parcel parcel = Parcel.obtain();
        ArrayList<String> participantKeys = new ArrayList<>();
        String idEvent = getRandomId();

        id = getRandomId();
        try {
            user = PFUser.createNewUser(id, EMAIL, PHONE_NUMBER, USERNAME, FIRST_NAME, LAST_NAME);
        } catch (PFException e) {
            e.printStackTrace();
        }
        PFMember member = null;
        try {
            member = PFMember.fetchExistingMember(id);
        } catch (PFException e) {
            e.printStackTrace();
        }
        participantKeys.add(id);
        Parcelable[] array = new Parcelable[1];
        array[0] = member;

        parcel.writeString(idEvent);
        parcel.writeString(dateToString(DATE));
        parcel.writeString(NAME);
        parcel.writeString(DESCRIPTION);
        parcel.writeString(dateToString(DATE));
        parcel.writeString(PLACE);
        parcel.writeParcelable(PICTURE, 0);
        parcel.writeStringList(participantKeys);
        parcel.writeParcelableArray(array, 0);

        parcel.setDataPosition(0);

        event = new PFEvent(parcel);

        parcel.recycle();

        assertEquals(NAME, event.getName());
        assertEquals(DESCRIPTION, event.getDescription());
        assertEquals(dateToString(DATE), dateToString(event.getDate()));
        assertEquals(PLACE, event.getPlace());
        assertNull(event.getPicture());
        assertEquals(participantKeys.size(), event.getParticipants().size());
        assertEquals(array.length, event.getParticipants().size());
        assertEquals(idEvent, event.getId());

    }*/

    @After
    public void deleteAfterTesting() {
        if (group != null) {
            group.deleteGroup();
        }
        if (event != null) {
            try {
                event.delete();
            } catch (PFException e) {
                e.printStackTrace();
            }
        }
        if (user != null) {
            deleteUserWithId(id);
        }
    }
}
