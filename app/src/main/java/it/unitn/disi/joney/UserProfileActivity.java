package it.unitn.disi.joney;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static it.unitn.disi.joney.ImageUploadUtils.saveImage;

public class UserProfileActivity extends AppCompatActivity implements PictureUploadListener {

    EditText etUserInfo;
    TextView tvUserName;
    FloatingActionButton btnSaveChanges;
    ImageView ivUserProfileImage;
    Button btnText;

    boolean descriptionChanged = false, profileImageChanged = false;
    boolean isAnotherUser = false;

    DatabaseHandler db = new DatabaseHandler(this);

    UserProfileImage upi;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        isAnotherUser = intent.hasExtra("userId");
        //using toolbar if currentUser profile
        if (!isAnotherUser){
            setTheme(R.style.AppTheme_NoActionBar);
            setContentView(R.layout.activity_user_profile);
        }
        super.onCreate(savedInstanceState);
        if(isAnotherUser)
            setContentView(R.layout.content_user_profile);

        mContext = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);
        //Toast.makeText(getApplicationContext(),"User id: " + String.valueOf(currentUserId),Toast.LENGTH_LONG).show();

        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        etUserInfo = (EditText) findViewById(R.id.et_user_info);
        ivUserProfileImage = (ImageView) findViewById(R.id.iv_user_profile_image);
        btnSaveChanges = (FloatingActionButton) findViewById(R.id.btn_save_description);
        btnText = (Button) findViewById(R.id.btn_text);

        /*Intent*/ intent = getIntent();
        //looking for another user profile
        if (intent.hasExtra("userId")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            int userId = intent.getIntExtra("userId",-1);
            etUserInfo.setFocusable(false);
            //ivUserProfileImage.setClickable(false);

            User user = db.getUserById(userId);

            this.setTitle(user.getFirstName() + "'s Profile");
            tvUserName.setText(user.getFirstName() + " " + user.getLastName());
            etUserInfo.setText(user.getDescription());
            upi = db.getUserProfileImage(userId);
            if (upi != null) {
                File imgFile = new File(upi.getSource());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivUserProfileImage.setImageBitmap(myBitmap);
                }
            }

        }
        //going to my profile
        else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(getApplicationContext(), drawer));

            User user = db.getUserById(currentUserId);

            this.setTitle(user.getFirstName() + "'s Profile");
            tvUserName.setText(user.getFirstName() + " " + user.getLastName());
            etUserInfo.setText(user.getDescription());

            /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
            ivUserProfileImage.setImageBitmap();*/

            upi = db.getUserProfileImage(currentUserId);
            if (upi != null) {
                Log.d("Image", "Exist");
                File imgFile = new File(upi.getSource());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivUserProfileImage.setImageBitmap(myBitmap);
                    Log.d("Image", "Loaded");
                }
            }

            ivUserProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        showMissingPermissionAlert("Storage", "Pictures");
                    else {
                        if (upi == null) {
                            ImageUploadUtils.showPictureOptionDialog(mContext, UserProfileActivity.this, -1);
                        } else
                            ImageUploadUtils.showPictureOptionDialog(mContext, UserProfileActivity.this, 0);
                    }
                }
            });

            btnSaveChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (descriptionChanged) {
                        db.updateUserDescription(currentUserId, etUserInfo.getText().toString());
                        btnSaveChanges.setVisibility(View.INVISIBLE);
                        descriptionChanged = false;
                    }
                    if (profileImageChanged) {
                        if (!ivUserProfileImage.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_pictures).getConstantState())) {
                            Bitmap bitmap = ((BitmapDrawable) ivUserProfileImage.getDrawable()).getBitmap();
                            Log.d("Pictures","Different");
                            String path = saveImage(getApplicationContext(), bitmap, Constants.PATH_USER_PROFILE_IMAGES);
                            if (upi == null) {
                                UserProfileImage userProfileImage = new UserProfileImage(path, currentUserId);
                                db.addUserProfileImage(userProfileImage);
                            } else {
                                db.updateUserProfileImage(currentUserId, path);
                            }
                        } else {
                            Log.d("Pictures","Equals");
                            if (upi != null)
                                db.removeUserProfileImage(currentUserId);
                        }

                        btnSaveChanges.setVisibility(View.INVISIBLE);
                        profileImageChanged = false;
                    }
                }
            });

            etUserInfo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    btnSaveChanges.setVisibility(View.VISIBLE);
                    descriptionChanged = true;
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intUser = new Intent(UserProfileActivity.this,UserProfileActivity.class);
                intUser.putExtra("userId",2);
                startActivity(intUser);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(isAnotherUser)
            super.onBackPressed();
        else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(isAnotherUser) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    super.onBackPressed();
                    return true;
            }
        }
        else {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMissingPermissionAlert(String permission, String hardware) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable " + permission)
                .setMessage("Your need to grant " + permission + " Permission if you want to use " + hardware + ".")
                .setPositiveButton("App Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        myIntent.setData(uri);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        Bitmap bitmap = null;
        if (requestCode == Constants.UPLOAD_FROM_GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                Log.d("Img URI", contentURI.toString());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == Constants.UPLOAD_FROM_CAMERA) {
            bitmap = (Bitmap) data.getExtras().get("data");
        }

        if (bitmap != null) {
            btnSaveChanges.setVisibility(View.VISIBLE);
            profileImageChanged = true;
            //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();

            //setting image resource
            ivUserProfileImage.setImageBitmap(bitmap);

            final int thumbnailSize = (int) getApplicationContext().getResources().getDimension(R.dimen.pic_thumbnail_size);
            final int thumbnailMargin = (int) getApplicationContext().getResources().getDimension(R.dimen.pic_thumbnail_margin);

            ivUserProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedImgIndex = 0;
                    ImageUploadUtils.showPictureOptionDialog(mContext, UserProfileActivity.this, clickedImgIndex);
                }
            });
        }
    }

    public void onRemovePicture(int imgViewIndex) {
        if (ivUserProfileImage != null) {
            ivUserProfileImage.setImageResource(R.drawable.ic_pictures);
            btnSaveChanges.setVisibility(View.VISIBLE);
            profileImageChanged = true;
        }
    }

    public void onChangePicture(int index) {
        Log.d("Change", String.valueOf(index));
        /*this.picToBeChangedIndex = index;*/
    }
}
