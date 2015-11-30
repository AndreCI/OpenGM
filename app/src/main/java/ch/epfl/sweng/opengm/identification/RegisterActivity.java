package ch.epfl.sweng.opengm.identification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.groups.MyGroupsActivity;
import ch.epfl.sweng.opengm.identification.phoneNumber.PhoneAddingActivity;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFUser;
import ch.epfl.sweng.opengm.utils.NetworkUtils;
import ch.epfl.sweng.opengm.utils.Utils;

import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_CORRECT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_NOT_CASE_SENSITIVE;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_LONG;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_SHORT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITHOUT_LETTER;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITHOUT_NUMBER;
import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;
import static com.parse.ParseException.EMAIL_TAKEN;
import static com.parse.ParseException.USERNAME_MISSING;

public class RegisterActivity extends AppCompatActivity {


    public static final String USERNAME_KEY = "ch.epfl.ch.opengm.identification.registeractivity.username";
    public static final String PASSWORD_KEY = "ch.epfl.ch.opengm.identification.registeractivity.password";
    public static final String PHONE_KEY = "ch.epfl.ch.opengm.identification.registeractivity.phone";
    public static final int PHONE_ACT_KEY = 1061;


    private EditText mEditUsername;
    private EditText mEditPassword1;
    private EditText mEditPassword2;
    private EditText mEditFirstname;
    private EditText mEditLastname;
    private EditText mEditEmail;
    private EditText mEditPhone;
    private PopupWindow popup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent registerIntent = getIntent();

        mEditUsername = (EditText) findViewById(R.id.register_username);
        mEditPassword1 = (EditText) findViewById(R.id.register_password1);
        mEditPassword2 = (EditText) findViewById(R.id.register_password2);
        mEditFirstname = (EditText) findViewById(R.id.register_firstname);
        mEditLastname = (EditText) findViewById(R.id.register_lastname);
        mEditEmail = (EditText) findViewById(R.id.register_email);
        mEditPhone = (EditText) findViewById(R.id.register_phone);

        popup = new PopupWindow(RegisterActivity.this);

        // Auto complete fields if user fills first the login fields
        mEditUsername.setText(registerIntent.getStringExtra(USERNAME_KEY));
        mEditPassword1.setText(registerIntent.getStringExtra(PASSWORD_KEY));

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.register_outmostLayout);
        onTapOutsideBehaviour(layout, this);
    }

    public void displayHowToPwd(View anchorView) {
        View layout = getLayoutInflater().inflate(R.layout.popup_password, null);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView == null ? findViewById(R.id.pwd_info) : anchorView);
    }

    public void dismisssPopUp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                popup.dismiss();
            }
        });
    }

    public void onClickRegister(View v) {
        if(NetworkUtils.haveInternet(getBaseContext())) {
            final String username = mEditUsername.getText().toString();
            String password1 = mEditPassword1.getText().toString();
            String password2 = mEditPassword2.getText().toString();
            final String lastname = mEditLastname.getText().toString();
            final String firstname = mEditFirstname.getText().toString();
            final String email = mEditEmail.getText().toString();
            final String number = mEditPhone.getText().toString();

            mEditUsername.setError(null);
            mEditPassword1.setError(null);
            mEditPassword2.setError(null);
            mEditLastname.setError(null);
            mEditFirstname.setError(null);
            mEditEmail.setError(null);
            mEditPhone.setError(null);

            int inputErrorCode;
            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(username)) {
                mEditUsername.setError(getString(R.string.emtpy_username_activity_register));
                focusView = mEditUsername;
                cancel = true;
            } else if (TextUtils.isEmpty(password1)) {
                mEditPassword1.setError(getString(R.string.empty_password_activity_register));
                focusView = mEditPassword1;
                cancel = true;
                displayHowToPwd(null);
            } else if (TextUtils.isEmpty(password2)) {
                mEditPassword2.setError(getString(R.string.empty_password_activity_register));
                focusView = mEditPassword2;
                cancel = true;
                displayHowToPwd(null);
            } else if (TextUtils.isEmpty(firstname)) {
                mEditFirstname.setError(getString(R.string.empty_firstname_activity_register));
                focusView = mEditFirstname;
                cancel = true;
            } else if (TextUtils.isEmpty(lastname)) {
                mEditLastname.setError(getString(R.string.empty_lastname_activity_register));
                focusView = mEditLastname;
                cancel = true;
            } else if (TextUtils.isEmpty(email)) {
                mEditEmail.setError(getString(R.string.empty_email_activity_register));
                focusView = mEditEmail;
                cancel = true;
            } else if (!InputUtils.isEmailValid(email)) {
                mEditEmail.setError(getString(R.string.incorrect_email_activity_register));
                focusView = mEditEmail;
                cancel = true;
            } else if (!InputUtils.isPhoneEnteredValid(number)) {
                mEditPhone.setError(getString(R.string.incorrect_phone_number_activity_register));
                focusView = mEditPhone;
                cancel = true;
            } else if ((inputErrorCode = InputUtils.isPasswordInvalid(password1)) != INPUT_CORRECT) {
                String errorString = "";
                switch (inputErrorCode) {
                    case INPUT_TOO_SHORT:
                        errorString = getString(R.string.short_password_activity_register);
                        break;
                    case INPUT_TOO_LONG:
                        errorString = getString(R.string.long_password_activity_register);
                        break;
                    case INPUT_NOT_CASE_SENSITIVE:
                        errorString = getString(R.string.case_password_activity_register);
                        break;
                    case INPUT_WITHOUT_NUMBER:
                        errorString = getString(R.string.no_number_password_activity_register);
                        break;
                    case INPUT_WITHOUT_LETTER:
                        errorString = getString(R.string.no_letter_password_activity_register);
                        break;
                    default:
                }
                mEditPassword1.setError(errorString);
                focusView = mEditPassword1;
                cancel = true;
                displayHowToPwd(null);
            } else if (!password1.equals(password2)) {
                mEditPassword1.setError(getString(R.string.non_matching_passwords_activity_register));
                focusView = mEditPassword1;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                // First : SignUp the user in the _User table
                final ProgressDialog dialog = Utils.getProgressDialog(this);

                final ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password1);
                user.setEmail(email);
                user.signUpInBackground(new SignUpCallback() {
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    try {
                                                        PFUser.createNewUser(user.getObjectId(), number, email, username, firstname, lastname);
                                                        dialog.hide();
                                                        Intent intent = new Intent(RegisterActivity.this, MyGroupsActivity.class);
                                                        intent.putExtra(MyGroupsActivity.COMING_FROM_KEY, true);
                                                        startActivity(intent);
                                                    } catch (PFException e1) {
                                                        dialog.hide();
                                                        Toast.makeText(getApplicationContext(), e1.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    dialog.hide();
                                                    // error while updating the _User table
                                                    switch (e.getCode()) {
                                                        case EMAIL_TAKEN:
                                                            mEditEmail.setError(String.format(getString(R.string.taken_email_activity_register), username));
                                                            mEditEmail.requestFocus();
                                                            break;
                                                        case USERNAME_MISSING:
                                                            mEditUsername.setError(String.format(getString(R.string.taken_email_activity_register), email));
                                                            mEditUsername.requestFocus();
                                                            break;
                                                        default:
                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }
                );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PHONE_ACT_KEY) {
            if (resultCode == Activity.RESULT_OK) {
                mEditPhone.setText(data.getStringExtra(PHONE_KEY));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }


    public void showPhoneChoice(View v) {
        Intent i = new Intent(this, PhoneAddingActivity.class);
        startActivityForResult(i, PHONE_ACT_KEY);
    }
}