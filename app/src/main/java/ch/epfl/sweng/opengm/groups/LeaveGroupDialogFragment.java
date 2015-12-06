package ch.epfl.sweng.opengm.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.NetworkUtils;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;

public class LeaveGroupDialogFragment extends DialogFragment {

    private PFGroup groupToLeave;


    public LeaveGroupDialogFragment() {
        groupToLeave = null;
    }

    public LeaveGroupDialogFragment setGroupToLeave(PFGroup groupToLeave) {
        this.groupToLeave = groupToLeave;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (groupToLeave == null) {
            throw new UnsupportedOperationException();
        }

        String leaveThisGroupWarning = String.format(getString(R.string.leaveGroupWarning), groupToLeave.getName());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(leaveThisGroupWarning)
                .setPositiveButton(R.string.leaveTheGroup, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Remove the user from this group
                        if (NetworkUtils.haveInternet(getActivity())) {
                            try {
                                getCurrentUser().removeFromGroup(groupToLeave.getId());
                            } catch (PFException e) {
                                Toast.makeText(getActivity(), "Error while leaving the group. Operation aborted", Toast.LENGTH_LONG).show();
                            }
                            // Go back to MyGroupsActivity
                            Intent intent = new Intent(getActivity(), MyGroupsActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        return builder.create();
    }
}