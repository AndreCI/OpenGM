package ch.epfl.sweng.opengm.identification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.utils.Utils;

import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditUsername;
    private EditText mEditPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEditUsername = (EditText) findViewById(R.id.login_username);
        mEditPassword = (EditText) findViewById(R.id.login_password);

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
        } else if (InputUtils.isPasswordInvalid(password)) {
            mEditPassword.setError(getString(R.string.short_password_activity_register));
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
                        Intent intent = new Intent(LoginActivity.this, GroupsOverviewActivity.class);
                        startActivity(intent);
                    } else {
                        dialog.hide();
                        switch (e.getCode()) {
                            case 101:
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

        Log.v("INFO", "Password forgotten");

    }

    public void onClickRegister(View v) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra(RegisterActivity.USERNAME_KEY, mEditUsername.getText().toString());
        registerIntent.putExtra(RegisterActivity.PASSWORD_KEY, mEditPassword.getText().toString());
        startActivity(registerIntent);
    }

}
