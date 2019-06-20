package it.unitn.disi.joney;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static it.unitn.disi.joney.ImageUploadUtils.saveImage;

public class PostJobActivity extends AppCompatActivity implements PictureUploadListener {

    Spinner spnCategory;
    Button btnPostJob;
    ImageView btnAddPicture;
    LinearLayout llPictures;
    EditText etAddress;

    Pair<Double,Double> location = null;

    int uploadedPicCounter = 0;
    int picToBeChangedIndex = -1;

    private Context mContext;

    DatabaseHandler db = new DatabaseHandler(this);
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        mContext = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        llPictures = (LinearLayout) findViewById(R.id.ll_pictures);
        btnAddPicture = (ImageView) findViewById(R.id.btn_add_picture);
        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isStoragePermissionGranted(getApplicationContext()))
                    showMissingPermissionAlert("Storage","Pictures");
                else if(uploadedPicCounter < Constants.MAX_JOB_PICTURE_NUMBER) {
                    ImageUploadUtils.showPictureOptionDialog(mContext, PostJobActivity.this, -1, Constants.PATH_JOB_IMAGES);
                }
            }
        });

        spnCategory = (Spinner) findViewById(R.id.spn_category);

        List<JobCategory> jobCategoryList = db.getAllJobCategories();
        //add invalid choice manually at the beginning
        jobCategoryList.add(0, new JobCategory(Constants.INVALID_ITEM_VALUE, Constants.NO_SPINNER_SELECTION, null));
        ArrayAdapter<JobCategory> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, jobCategoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(dataAdapter);
        spnCategory.setOnItemSelectedListener(new OnSpinnerItemSelectedListener());

        btnPostJob = (Button) findViewById(R.id.btn_post_job);
        btnPostJob.setOnClickListener(new PostJobListener());

        etAddress = (EditText) findViewById(R.id.et_job_address);
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(PostJobActivity.this, MapActivity.class);
                startActivityForResult(mapIntent,Constants.PICK_LOCATION);
            }
        });

        if (!Utils.isLocationPermissionGranted(this))
            showMissingPermissionAlert("Location","GPS");
        else if(!Utils.isGPSEnabled(this)) {
            int gpsAlertPref = prefs.getInt(Constants.PREF_GPS_ALERT, Constants.GPS_ALERT_SHOW);
            if(gpsAlertPref == Constants.GPS_ALERT_SHOW)
                Utils.showGPSDisabledAlert(this);
        } else {
            location = Utils.getLocation(this);

            if(location != null) {
                //etAddress.setText(location.first.toString() + "," + location.second.toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String streetName = Utils.getStreetName(location.first,location.second,mContext);
                        etAddress.post(new Runnable() {
                            @Override
                            public void run() {
                                etAddress.setText(streetName);
                            }
                        });
                    }
                }) .start();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class PostJobListener implements  View.OnClickListener {
        @Override
        public void onClick(View view) {
            String jobTitle, description;
            String payStr;
            int pay = 0;
            EditText etJobTitle, etDescription, etPay;


            int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);
            Log.i("Current user ", Integer.toString(currentUserId));

            etJobTitle = (EditText) findViewById(R.id.et_job_title);
            jobTitle = etJobTitle.getText().toString();
            etDescription = (EditText) findViewById(R.id.et_job_description);
            description = etDescription.getText().toString();
            etPay = (EditText) findViewById(R.id.et_pay);
            payStr = etPay.getText().toString();
            if(payStr != null && !payStr.trim().equalsIgnoreCase("")) {
                pay = Integer.parseInt(payStr);
            }
            JobCategory jobCategory = (JobCategory) spnCategory.getSelectedItem();

            if(jobTitle.length() == 0)
                Toast.makeText(getApplicationContext(), "You must specify a job title", Toast.LENGTH_SHORT).show();
            else if(jobCategory.getId() == Constants.INVALID_ITEM_VALUE)
                Toast.makeText(getApplicationContext(), "You must select a job category from the dropdown", Toast.LENGTH_SHORT).show();
            else if(location == null)
                Toast.makeText(getApplicationContext(), "You have inserted an invalid address", Toast.LENGTH_SHORT).show();
            else if(pay <= 0)
                Toast.makeText(getApplicationContext(), "You have inserted an invalid pay amount", Toast.LENGTH_SHORT).show();
            else {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String now = dateFormat.format(date);
                //Log.d("Date",now);

                Job job = new Job(jobTitle, description, pay, false, now, location.first, location.second, jobCategory.getId(), currentUserId, null);
                int jobId = db.addJob(job);

                int attachmentNum = llPictures.getChildCount();
                for(int i=0; i<attachmentNum; i++) {
                    //save image on the device and register it to the database
                    ImageView imgView = (ImageView) llPictures.getChildAt(i);
                    Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
                    String path = saveImage(getApplicationContext(), bitmap, Constants.PATH_JOB_IMAGES);
                    JobImage jobImage = new JobImage(path, jobId);
                    db.addJobImage(jobImage);
                }

                Intent intMyJobs = new Intent(PostJobActivity.this, MyJobsActivity.class);
                startActivity(intMyJobs);
            }
        }
    }

    private void showMissingPermissionAlert(String permission, String hardware) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable " + permission)
            .setMessage("You need to grant " + permission + " Permission if you want to use " + hardware +".")
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
                Log.d("Img URI",contentURI.toString());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == Constants.UPLOAD_FROM_CAMERA) {
            bitmap = (Bitmap) data.getExtras().get("data");
        } else if (requestCode == Constants.PICK_LOCATION) {
            File tempImg = new File(Environment.getExternalStorageDirectory(), Constants.PATH_JOB_IMAGES + "temp.jpg");
            Uri photoUri = Uri.fromFile(tempImg);
            //bitmap = (Bitmap) data.getExtras().get("data");
            Log.d("Uri", photoUri.toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == Constants.RECEIVED_LOCATION) {
            if(resultCode == RESULT_OK) {
                final Double lat = data.getDoubleExtra("latitude",0.0);
                final Double lon = data.getDoubleExtra("longitude",0.0);
                location = new Pair<Double,Double>(lat,lon);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String streetName = Utils.getStreetName(lat,lon,mContext);
                        etAddress.post(new Runnable() {
                            @Override
                            public void run() {
                                etAddress.setText(streetName);
                            }
                        });
                    }
                }) .start();

            }
        }

        if(bitmap != null) {
            //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();

            ImageView newImgView;
            if(picToBeChangedIndex != -1) {
                newImgView = (ImageView) llPictures.getChildAt(picToBeChangedIndex);
            } else {
                newImgView = new ImageView(this);
            }

            final ImageView imageView = newImgView;
            //setting image resource
            imageView.setImageBitmap(bitmap);

            if(picToBeChangedIndex == -1) {

                final int thumbnailSize = (int) getApplicationContext().getResources().getDimension(R.dimen.pic_thumbnail_size);
                final int thumbnailMargin = (int) getApplicationContext().getResources().getDimension(R.dimen.pic_thumbnail_margin);

                //setting image size and margins
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
                layoutParams.setMargins(0, 0, thumbnailMargin, 0);
                imageView.setLayoutParams(layoutParams);


                llPictures.addView(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int clickedImgIndex = llPictures.indexOfChild(imageView);
                        ImageUploadUtils.showPictureOptionDialog(mContext, PostJobActivity.this, clickedImgIndex,Constants.PATH_JOB_IMAGES);
                    }
                });
            }

            picToBeChangedIndex = -1;
            uploadedPicCounter++;
            //hide add btn if there are 4 pics already
            if(uploadedPicCounter == Constants.MAX_JOB_PICTURE_NUMBER) {
                btnAddPicture.setVisibility(View.GONE);
            }
        }
    }

    public void onRemovePicture(int imgViewIndex) {

        ImageView imgView = (ImageView) llPictures.getChildAt(imgViewIndex);

        if(imgView != null) {
            llPictures.removeView(imgView);
            uploadedPicCounter--;

            if(btnAddPicture.getVisibility() == View.GONE) {
                btnAddPicture.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onChangePicture(int index) {
        this.picToBeChangedIndex = index;
    }
}