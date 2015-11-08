package ch.epfl.sweng.opengm.events;

import ch.epfl.sweng.opengm.parse.PFMember;

/**
 * Created by virgile on 26/10/2015.
 */
public class CheckParticipant {
    private PFMember mParticipant;
    private boolean mIsCheck;

    public String getName() {
        return mParticipant.getName();
    }

    public boolean isChecked() {
        return mIsCheck;
    }

    public void setCheck(boolean mIsCheck) {
        this.mIsCheck = mIsCheck;
    }

    public PFMember getParticipant() {
        return mParticipant;
    }


    public CheckParticipant(PFMember participant, boolean isCheck) {
        mParticipant = participant;
        mIsCheck = isCheck;
    }
}
