package it.unitn.disi.joney;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static it.unitn.disi.joney.ImageUploadUtils.saveImage;

public class UserProfileActivity extends AppCompatActivity implements PictureUploadListener {

    EditText etUserInfo;
    TextView tvUserName;
    FloatingActionButton btnSaveChanges;
    CircularImageView ivUserProfileImage;
    RatingBar rbUserAverage;

    ArrayList<Feedback> feedbackList;
    ListView lvReviews;
    FeedbackListAdapter flaReviews;
    LinearLayout llChat;

    boolean descriptionChanged = false, profileImageChanged = false;
    boolean isAnotherUser = false;
    int currentUserId;

    DatabaseHandler db = new DatabaseHandler(this);

    UserProfileImage upi;
    User user;

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
        currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);
        //Toast.makeText(getApplicationContext(),"User id: " + String.valueOf(currentUserId),Toast.LENGTH_LONG).show();

        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        rbUserAverage = (RatingBar) findViewById(R.id.rb_rating_average);
        etUserInfo = (EditText) findViewById(R.id.et_user_info);
        ivUserProfileImage = (CircularImageView) findViewById(R.id.iv_user_profile_image);
        btnSaveChanges = (FloatingActionButton) findViewById(R.id.btn_save_description);
        lvReviews = (ListView) findViewById(R.id.review_list);
        llChat = (LinearLayout) findViewById(R.id.ll_chat);

        /*Intent*/ intent = getIntent();
        //looking for another user profile
        if (intent.hasExtra("userId")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            int userId = intent.getIntExtra("userId",-1);
            etUserInfo.setFocusable(false);
            //ivUserProfileImage.setClickable(false);

            user = db.getUserById(userId);

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

            feedbackList = db.getUserFeedbacks(userId);
            rbUserAverage.setRating(Utils.getUserAverageRating(this, user.id));
            //Toast.makeText(getApplicationContext(),"Total feedbacks = " + String.valueOf(feedbackList.size()),Toast.LENGTH_SHORT).show();
            flaReviews = new FeedbackListAdapter(this,feedbackList);
            lvReviews.setAdapter(flaReviews);

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

            user = db.getUserById(currentUserId);


            this.setTitle("Your Profile");
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


            new Thread(new Runnable() {
                @Override
                public void run() {
                    feedbackList = db.getUserFeedbacks(currentUserId);
                    rbUserAverage.setRating(Utils.getUserAverageRating(mContext, user.id));
                    //Toast.makeText(getApplicationContext(),"Total feedbacks = " + String.valueOf(feedbackList.size()),Toast.LENGTH_SHORT).show();
                    flaReviews = new FeedbackListAdapter(mContext,feedbackList);

                    lvReviews.post(new Runnable() {
                        @Override
                        public void run() {
                            lvReviews.setAdapter(flaReviews);
                        }
                    });
                }
            }) .start();
            /*feedbackList = db.getUserFeedbacks(currentUserId);
            rbUserAverage.setRating(Utils.getUserAverageRating(this, user.id));
            Toast.makeText(getApplicationContext(),"Total feedbacks = " + String.valueOf(feedbackList.size()),Toast.LENGTH_SHORT).show();
            flaReviews = new FeedbackListAdapter(this,feedbackList);
            lvReviews.setAdapter(flaReviews);*/


            ivUserProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        showMissingPermissionAlert("Storage", "Pictures");
                    else {
                        try {
                            if (upi == null) {
                                ImageUploadUtils.showPictureOptionDialog(mContext, UserProfileActivity.this, -1, Constants.PATH_USER_PROFILE_IMAGES);
                            } else
                                ImageUploadUtils.showPictureOptionDialog(mContext, UserProfileActivity.this, 0, Constants.PATH_USER_PROFILE_IMAGES);
                        } catch (Exception e) {
                            Log.d("Error","can't create file");
                        }
                        //photo.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + Constants.PATH_USER_PROFILE_FULL_IMAGES + "culo.jpg"));
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
                            Bitmap bitmap =  ((BitmapDrawable) ivUserProfileImage.getDrawable()).getBitmap();
                            Log.d("Pictures","Different");
                            String path = saveImage(getApplicationContext(), bitmap, Constants.PATH_USER_PROFILE_IMAGES);
                            if (upi == null) {
                                UserProfileImage userProfileImage = new UserProfileImage(path, currentUserId);
                                db.addUserProfileImage(userProfileImage);
                            } else {
                                Log.d("Path",path);
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
            llChat.setVisibility(View.INVISIBLE);
            llChat.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    /*Intent intUser = new Intent(UserProfileActivity.this,UserProfileActivity.class);
                    intUser.putExtra("userId",1);
                    startActivity(intUser);*/

                    /*Intent intChat = new Intent(UserProfileActivity.this,ChatActivity.class);
                    intChat.putExtra("senderId",currentUserId);
                    intChat.putExtra("receiverId",currentUserId==2?1:2);
                    startActivity(intChat);*/

                    /*Intent intFeed = new Intent(UserProfileActivity.this,AddFeedbackActivity.class);
                    intFeed.putExtra("jobId",3);
                    startActivity(intFeed);*/

                    /*createNotificationChannel();
                    NotificationCompat.Builder ncb = getNotification();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                    notificationManager.notify(1, ncb.build());*/

                    /*Utils.createNotificationChannel("newMessage","To notify new messages",
                            "idNewMessage",getSystemService(NotificationManager.class));
                    Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                    intent.putExtra("senderId",1);
                    intent.putExtra("receiverId",2);
                    NotificationCompat.Builder build = Utils.getNotification(mContext,intent,"idNewMessage","New message!","You got a new message from " + db.getUserById(2).getFirstName());
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                    notificationManager.notify(1, build.build());*/
                }
            });


        }
        //outside else
        if(intent.hasExtra("FromFacebook")) {
            //Load facebook picture
            String profile_pic = "";
            Log.d("Extra","found");
            String id = AccessToken.getCurrentAccessToken().getUserId();
            Log.d("UserId",id);
            //String id = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.PREF_CURRENT_USER_FACEBOOK_ID,"not_found");
            if(id != null) {
                profile_pic = "https://graph.facebook.com/" + id + "/picture?type=large";
                Log.d("Path",profile_pic);
                new saveProfileImage(currentUserId).execute(profile_pic);
            }
        }
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
                /*try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }*/
                cropImage(contentURI);
            }

        } else if (requestCode == Constants.UPLOAD_FROM_CAMERA) {
            //bitmap = /*getBitmap();*/ (Bitmap) data.getExtras().get("data");
            //Uri tempUri = getImageUri(getApplicationContext(), bitmap);
            //Log.d("URI getUri", tempUri.toString());
            //bitmap = null;
            File tempImg = new File(Environment.getExternalStorageDirectory(), Constants.PATH_USER_PROFILE_IMAGES + "temp.jpg");
            Uri userImageUri = Uri.fromFile(tempImg);
            Log.d("Uri",userImageUri.toString());
            //cropImage(tempUri);
            cropImage(userImageUri);
        } else if(requestCode == Constants.CROP_PIC) {
            Uri contentURI = data.getData();
            Log.d("Img URI", contentURI.toString());
            try {
                //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                //bitmap = Utils.handleSamplingAndRotationBitmap(this, contentURI);
                bitmap = Utils.handleSamplingAndRotationBitmap(this, contentURI);
            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        }

        if (bitmap != null) {
            btnSaveChanges.setVisibility(View.VISIBLE);
            profileImageChanged = true;
            //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();

            //setting image resource
            ivUserProfileImage.setImageBitmap(bitmap);

            //final int thumbnailSize = (int) getApplicationContext().getResources().getDimension(R.dimen.post_job_thumbnail_size);
            //final int thumbnailMargin = (int) getApplicationContext().getResources().getDimension(R.dimen.post_job_thumbnail_margin);

            ivUserProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedImgIndex = 0;
                    ImageUploadUtils.showPictureOptionDialog(mContext, UserProfileActivity.this, clickedImgIndex, Constants.PATH_USER_PROFILE_IMAGES);
                }
            });
        }
    }

    public void onRemovePicture(int imgViewIndex) {
        if (ivUserProfileImage != null) {
            ivUserProfileImage.setImageResource(R.drawable.ic_pictures);
            ivUserProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageUploadUtils.showPictureOptionDialog(mContext, UserProfileActivity.this, -1, Constants.PATH_USER_PROFILE_IMAGES);
                }
            });
            btnSaveChanges.setVisibility(View.VISIBLE);
            profileImageChanged = true;
        }
    }

    public void onChangePicture(int index) {
        Log.d("Change", String.valueOf(index));
        /*this.picToBeChangedIndex = index;*/
    }

    public void cropImage(Uri uri)
    {
        try {
            Intent myCropIntent = new Intent("com.android.camera.action.CROP");

            myCropIntent.setDataAndType(uri,"image/*");
            myCropIntent.putExtra("crop", "true");
            myCropIntent.putExtra("aspectX", 1);
            myCropIntent.putExtra("aspectY", 1);
            myCropIntent.putExtra("outputX", 512);
            myCropIntent.putExtra("outputY", 512);
            myCropIntent.putExtra("return-data", false);
            //myCropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(myCropIntent, Constants.CROP_PIC);
        }

        catch (Exception e) {
            Log.d("Error",e.getMessage().toString());
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private class saveProfileImage extends AsyncTask<String, Void, Bitmap> {
        int userId;
        public saveProfileImage(int userId) {
            this.userId = userId;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {

            if (result != null) {
                btnSaveChanges.setVisibility(View.VISIBLE);
                profileImageChanged = true;
                ivUserProfileImage.setImageBitmap(result);
                ivUserProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int clickedImgIndex = 0;
                        ImageUploadUtils.showPictureOptionDialog(mContext, UserProfileActivity.this, clickedImgIndex, Constants.PATH_USER_PROFILE_IMAGES);
                    }
                });
            }

            /*String path = saveImage(getApplicationContext(), result, Constants.PATH_USER_PROFILE_IMAGES);

            UserProfileImage userProfileImage = db.getUserProfileImage(currentUserId);
            if (userProfileImage == null) {
                userProfileImage = new UserProfileImage(path, currentUserId);
                db.addUserProfileImage(userProfileImage);
            } else {
                Log.d("Path",path);
                db.updateUserProfileImage(currentUserId, path);
            }*/
        }
    }


    /*public Bitmap getBitmap(){
        //this.getContentResolver().notifyChange(Uri.fromFile(photo), null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap = null;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, Uri.fromFile(photo));
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d("Error", "Failed to load", e);
        }
        return bitmap;
    }*/

}
