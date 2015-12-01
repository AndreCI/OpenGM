package ch.epfl.sweng.opengm.groups;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

import ch.epfl.sweng.opengm.OpenGMApplication;
import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.utils.NetworkUtils;

import static ch.epfl.sweng.opengm.OpenGMApplication.getCurrentUser;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_BEGINS_WITH_SPACE;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_CORRECT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_LONG;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_TOO_SHORT;
import static ch.epfl.sweng.opengm.identification.InputUtils.INPUT_WITH_SYMBOL;
import static ch.epfl.sweng.opengm.identification.InputUtils.isGroupNameValid;
import static ch.epfl.sweng.opengm.utils.Utils.onTapOutsideBehaviour;

public class CreateEditGroupActivity extends AppCompatActivity {

    private EditText mGroupName;
    private EditText mGroupDescription;
    private Button mAddImage;

    private PFGroup currentGroup = null;

    private String initialName = null;
    private String initialDescription = null;
    private Bitmap image = null;

    private boolean isCreatingAGroup = false;

    private final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.create_group_outmostLayout);
        onTapOutsideBehaviour(layout, this);

        mGroupName = (EditText) findViewById(R.id.enterGroupName);
        mGroupDescription = (EditText) findViewById(R.id.enterGroupDescription);

        mAddImage = (Button)findViewById(R.id.chooseGroupImage);
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // trigger image gallery intent
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        currentGroup = OpenGMApplication.getCurrentGroup();

        if (currentGroup != null) {
            mGroupName.setText(currentGroup.getName());
            mGroupDescription.setText(currentGroup.getDescription());
            initialName = currentGroup.getName();
            initialDescription = currentGroup.getDescription();
            image = currentGroup.getPicture();
            findViewById(R.id.createGroupsMembersButton).setVisibility(View.VISIBLE);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void manageMembers(View view) {
        Intent intent = new Intent(this, MembersActivity.class);
        startActivity(intent);
    }

    public void createGroup(View view) {
        if (NetworkUtils.haveInternet(getBaseContext()) && !isCreatingAGroup) {
            isCreatingAGroup = true;
            String name = mGroupName.getText().toString();
            String description = mGroupDescription.getText().toString();

            int groupNameValid = isGroupNameValid(name);

            if (groupNameValid == INPUT_CORRECT) {
                if (currentGroup != null) {
                    if (!name.equals(initialName)) {
                        currentGroup.setName(name);
                    }
                    if (!description.equals(initialDescription)) {
                        currentGroup.setDescription(description);
                    }
                    currentGroup.setPicture(image);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    try {
                        PFGroup newGroup = PFGroup.createNewGroup(getCurrentUser(), name, description, image);
                        getCurrentUser().addToAGroup(newGroup);
                        OpenGMApplication.setCurrentGroup(OpenGMApplication.getCurrentUser().getGroups().size() - 1);
                        startActivity(new Intent(CreateEditGroupActivity.this, GroupsHomeActivity.class));
                    } catch (PFException e) {
                        Toast.makeText(getBaseContext(), "Couldn't create the group: there where problems when contacting the server.", Toast.LENGTH_LONG).show();
                        isCreatingAGroup = false;
                    }
                }
            } else {
                String errorMessage;
                switch (groupNameValid) {
                    case INPUT_TOO_SHORT:
                        errorMessage = getString(R.string.groupNameTooShort);
                        break;
                    case INPUT_TOO_LONG:
                        errorMessage = getString(R.string.groupNameTooLong);
                        break;
                    case INPUT_BEGINS_WITH_SPACE:
                        errorMessage = getString(R.string.groupNameStartsWithSpace);
                        break;
                    case INPUT_WITH_SYMBOL:
                        errorMessage = getString(R.string.groupNameIllegalCharacters);
                        break;
                    default:
                        errorMessage = getString(R.string.groupNameInvalid);
                        break;
                }
                mGroupName.setError(errorMessage);
                mGroupName.requestFocus();
                isCreatingAGroup = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_members, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // get back the image from the gallery
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    String path = getRealPathFromURI(imageUri);

                    // handle images that are too big
                    BitmapFactory.Options imSize = new BitmapFactory.Options();
                    imSize.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, imSize);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    int sampleSize = ((imSize.outHeight / 1080) > (imSize.outWidth / 1920)) ? (imSize.outHeight / 1080) : (imSize.outWidth / 1920);
                    opts.inSampleSize = sampleSize;

                    image = BitmapFactory.decodeFile(path, opts);
                }
                break;
            default:
        }
    }

    // get path from URI
    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
