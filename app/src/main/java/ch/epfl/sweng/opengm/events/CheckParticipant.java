package ch.epfl.sweng.opengm.events;

/**
 * Created by virgile on 26/10/2015.
 */
public class CheckParticipant {
    private String mParticipantName;
    private boolean mIsCheck;

    public String getName() {
        return mParticipantName;
    }

    public void setName(String mParticipantName) {
        this.mParticipantName = mParticipantName;
    }

    public boolean getCheck() {
        return mIsCheck;
    }

    public void setCheck(boolean mIsCheck) {
        this.mIsCheck = mIsCheck;
    }

    public CheckParticipant(String participantName, boolean isCheck) {
        mParticipantName = participantName;
        mIsCheck = isCheck;
    }
}
