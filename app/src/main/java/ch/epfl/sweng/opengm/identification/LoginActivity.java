package ch.epfl.sweng.opengm.identification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.utils.Utils;

import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_CORRECT;
import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditUsername;
    private EditText mEditPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MyGroupsActivity.class);
            intent.putExtra(MyGroupsActivity.COMING_FROM_KEY, false);
            startActivity(intent);
        }

        setContentView(R.layout.activity_login);

        mEditUsername = (EditText) findViewById(R.id.login_username);
        mEditPassword = (EditText) findViewById(R.id.login_password);

        // TODO change this after testing
        mEditUsername.setText("JellyTester");
        mEditPassword.setText("Jelly123");

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.login_outmostLayout);
        onTapOutsideBehaviour(layout, this);
    }

    public void onClickLogin(View v) {
        String username = mEditUsername.getText().toString();
        String password = mEditPassword.getText().toString();

        mEditUsername.setError(null);
        mEditPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mEditUsername.setError(getString(R.string.emtpy_username_activity_register));
            focusView = mEditUsername;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mEditPassword.setError(getString(R.string.empty_password_activity_register));
            focusView = mEditPassword;
            cancel = true;
        } else if (InputUtils.isPasswordInvalid(password) != INPUT_CORRECT) {
            String errorString = getString(R.string.invalid_password_activity_login);
            mEditPassword.setError(errorString);
            focusView = mEditPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            final ProgressDialog dialog = Utils.getProgressDialog(this);

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        dialog.hide();
                        Intent intent = new Intent(LoginActivity.this, MyGroupsActivity.class);
                        intent.putExtra(MyGroupsActivity.COMING_FROM_KEY, false);
                        startActivity(intent);
                    } else {
                        dialog.hide();
                        switch (e.getCode()) {
                            case ParseException.OBJECT_NOT_FOUND:
                                mEditPassword.setError(getString(R.string.incorrect_activity_login));
                                mEditPassword.requestFocus();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    public void onClickForgotPassword(View v) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_password_forgotten, null);

        builder.setView(view)
                .setPositiveButton(R.string.continue_dialog_password, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.cancel_dialog_password, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing just hide this dialog
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editMail = (EditText) view.findViewById(R.id.editTextMail_dialog_forgot);
                String email = editMail.getText().toString();

                editMail.setError(null);

                boolean cancel = false;

                if (TextUtils.isEmpty(email)) {
                    editMail.setError(getString(R.string.empty_email_activity_register));
                    cancel = true;
                } else if (!InputUtils.isEmailValid(email)) {
                    editMail.setError(getString(R.string.incorrect_email_activity_register));
                    cancel = true;
                }
                if (cancel) {
                    editMail.requestFocus();
                } else {
                    ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(dialog.getContext(), getString(e == null ? R.string.success_dialog_password : R.string.error_dialog_password), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    public void onClickRegister(View v) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra(RegisterActivity.USERNAME_KEY, mEditUsername.getText().toString());
        registerIntent.putExtra(RegisterActivity.PASSWORD_KEY, mEditPassword.getText().toString());
        startActivity(registerIntent);
    }

}