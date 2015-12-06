package ch.epfl.sweng.opengm.userProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.identification.InputUtils;
import ch.epfl.sweng.opengm.identification.RegisterActivity;
import ch.epfl.sweng.opengm.identification.phoneNumber.PhoneAddingActivity;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFUser;

import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_CORRECT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_NOT_CASE_SENSITIVE;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_LONG;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_SHORT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITHOUT_LETTER;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITHOUT_NUMBER;
import static ch.epfl.sweng.opengm.utils.Utils.getRealPathFromURI;

public class EditUserProfileActivity extends AppCompatActivity {

    private final int RESULT_LOAD_IMAGE = 13772;

    private ImageView mPhotoImageView;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private EditText mPhoneNumberEditText;
    private EditText mDescriptionEditText;

    private final PFUser currentUser = OpenGMApplication.getCurrentUser();
    private Bitmap image = null;

    private boolean startingChangePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_edit_layout);

        if (currentUser != null) {

            // Display profile picture of user :
            mPhotoImageView = (ImageView) findViewById(R.id.userPhoto);
            if (currentUser.getPicture() == null) {
                mPhotoImageView.setImageResource(R.drawable.avatar_male1);
            } else {
                mPhotoImageView.setImageBitmap(currentUser.getPicture());
            }
            // Display first name of user :
            mFirstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
            mFirstNameEditText.setText(currentUser.getFirstName());

            // Display last name of user :
            mLastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
            mLastNameEditText.setText(currentUser.getLastName());

            // Display e-mail address of user :
            mEmailEditText = (EditText) findViewById(R.id.emailEditText);
            mEmailEditText.setText(currentUser.getEmail());

            // Display phone number of user :
            mPhoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
            mPhoneNumberEditText.setText(currentUser.getPhoneNumber());
            mPhoneNumberEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditUserProfileActivity.this, PhoneAddingActivity.class);
                    startActivityForResult(i, RegisterActivity.PHONE_ACT_KEY);
                }
            });

            // Display description of user :
            mDescriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
            mDescriptionEditText.setText(currentUser.getAboutUser());

        }
    }

    public void changePicture(View view) {
        startingChangePicture = true;
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save_profile:
                mFirstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
                mLastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
                mEmailEditText = (EditText) findViewById(R.id.emailEditText);
                mPhoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
                mDescriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

                try {
                    currentUser.setFirstName(mFirstNameEditText.getText().toString());
                    currentUser.setLastName(mLastNameEditText.getText().toString());
                    currentUser.setEmail(mEmailEditText.getText().toString());
                    currentUser.setPhoneNumber(mPhoneNumberEditText.getText().toString());
                    currentUser.setAboutUser(mDescriptionEditText.getText().toString());
                    if (startingChangePicture) {
                        currentUser.setPicture(image);
                        for(PFGroup group : currentUser.getGroups()){
                            group.getMember(currentUser.getId()).setPicture(image);
                        }
                    }
                    Toast.makeText(this, getString(R.string.success_edit_profile), Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                } catch (PFException e) {
                    Toast.makeText(this, getString(R.string.error_edit_profile), Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_cancel_edit_profile:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RegisterActivity.PHONE_ACT_KEY:
                if (resultCode == Activity.RESULT_OK) {
                    mPhoneNumberEditText.setText(data.getStringExtra(RegisterActivity.PHONE_KEY));
                }
                break;
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    String path = getRealPathFromURI(imageUri, this);

                    // handle images that are too big
                    BitmapFactory.Options imSize = new BitmapFactory.Options();
                    imSize.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, imSize);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    int sampleSize = ((imSize.outHeight / 1080) > (imSize.outWidth / 1920)) ? (imSize.outHeight / 1080) : (imSize.outWidth / 1920);
                    opts.inSampleSize = sampleSize;
                    image = BitmapFactory.decodeFile(path, opts);
                    if (image != null) {
                        mPhotoImageView.setImageBitmap(image);
                    }
                }
                break;
            default:
        }
    }


    public void changePassword(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View changePasswordView = inflater.inflate(R.layout.dialog_change_password, null);

        // Set buttons with a null callback --> Implement the 2 callback functions in the following lines
        builder.setView(changePasswordView).setPositiveButton(R.string.ok, null).setNegativeButton(R.string.cancel, null);
        final AlertDialog changePasswordDialog = builder.create();
        changePasswordDialog.show();

        // When click on the "Cancel" button
        changePasswordDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do nothing, just cancel the dialog
                changePasswordDialog.dismiss();
            }
        });

        // When click on the "Ok" button
        changePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseUser currentUser = ParseUser.getCurrentUser();

                final EditText currentPasswordEditText = (EditText) changePasswordView.findViewById(R.id.currentPasswordEditText);
                final EditText newPasswordEditText = (EditText) changePasswordView.findViewById(R.id.newPasswordEditText);
                final EditText confirmPasswordEditText = (EditText) changePasswordView.findViewById(R.id.confirmPasswordEditText);

                currentPasswordEditText.setError(null);
                newPasswordEditText.setError(null);
                confirmPasswordEditText.setError(null);

                final String currentPassword = currentPasswordEditText.getText().toString();
                final String newPassword = newPasswordEditText.getText().toString();
                final String confirmPassword = confirmPasswordEditText.getText().toString();

                int inputErrorCode;

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

                // Password is not strong enough
                else if ((inputErrorCode = InputUtils.isPasswordInvalid(newPassword)) != INPUT_CORRECT) {
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
                    newPasswordEditText.setError(errorString);
                    newPasswordEditText.requestFocus();
                }

                // Check if currentPassword is the correct user password, and update it if it's the case
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
                                try {
                                    currentUser.save();
                                    Toast.makeText(changePasswordDialog.getContext(), getString(R.string.success_password_change_toast), Toast.LENGTH_LONG).show();
                                } catch (ParseException exception) {
                                    exception.printStackTrace();
                                    Toast.makeText(changePasswordDialog.getContext(), getString(R.string.error_password_change_toast), Toast.LENGTH_LONG).show();
                                }
                                changePasswordDialog.dismiss();
                            }
                        }
                    });
                }

            }
        });


    }

}
