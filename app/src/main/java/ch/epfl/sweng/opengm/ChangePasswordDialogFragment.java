package ch.epfl.sweng.opengm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ChangePasswordDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        final ParseUser currentUser = ParseUser.getCurrentUser();

                        final EditText currentPasswordEditText = (EditText) getActivity().findViewById(R.id.currentPasswordEditText);
                        final EditText newPasswordEditText = (EditText) getActivity().findViewById(R.id.newPasswordEditText);
                        final EditText confirmPasswordEditText = (EditText) getActivity().findViewById(R.id.confirmPasswordEditText);

                        currentPasswordEditText.setError(null);
                        newPasswordEditText.setError(null);
                        confirmPasswordEditText.setError(null);

                        String currentPassword = currentPasswordEditText.getText().toString();
                        final String newPassword = newPasswordEditText.getText().toString();
                        String confirmPassword = confirmPasswordEditText.getText().toString();

                        // Text fields are empty
                        if (TextUtils.isEmpty(currentPassword)) {
                            currentPasswordEditText.setError(getString(R.string.empty_password_activity_register));
                            currentPasswordEditText.requestFocus();
                        } else if (TextUtils.isEmpty(newPassword)) {
                            newPasswordEditText.setError(getString(R.string.empty_password_activity_register));
                            newPasswordEditText.requestFocus();
                        } else if (TextUtils.isEmpty(confirmPassword)) {
                            confirmPasswordEditText.setError(getString(R.string.empty_password_activity_register));
                            confirmPasswordEditText.requestFocus();
                        }

                        // Passwords don't match
                        else if (!newPassword.equals(confirmPassword)) {
                            confirmPasswordEditText.setError(getString(R.string.non_matching_passwords_activity_register));
                            confirmPasswordEditText.requestFocus();
                        }

                        // FIXME: is it the only way to check correctness of the Parse user password ?
                        // Check if currentPassword is the correct user password
                        else {
                            ParseUser.logInInBackground(currentUser.getUsername(), currentPassword, new LogInCallback() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    // Password entered in the currentPassword text field is not the correct user password
                                    if (parseUser == null) {
                                        currentPasswordEditText.setError(getString(R.string.incorrect_password));
                                        currentPasswordEditText.requestFocus();
                                    }
                                    // OK, fine. It's the correct password AND newPassword matches confirmPassword
                                    else {
                                        currentUser.setPassword(newPassword);
                                    }
                                }
                            });
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