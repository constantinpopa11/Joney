package it.unitn.disi.joney;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PostJobActivity extends AppCompatActivity {

    Spinner spnCategory;
    Button btnPostJob;
    ImageView ivPicture;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*
        ivPicture = (ImageView) findViewById(R.id.iv_add_pictures_post_job);
        ivPicture.setOnClickListener(new AddImageListener());
*/

        spnCategory = (Spinner) findViewById(R.id.spn_category);

        List<JobCategory> jobCategoryList = db.getAllJobCategories();
        //add invalid choice manually at the beginning
        jobCategoryList.add(0, new JobCategory(Constants.INVALID_ITEM_VALUE, Constants.NO_SPINNER_SELCTION, null));
        ArrayAdapter<JobCategory> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, jobCategoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(dataAdapter);
        spnCategory.setOnItemSelectedListener(new OnSpinnerItemSelectedListener());

        btnPostJob = (Button) findViewById(R.id.btn_post_job);
        btnPostJob.setOnClickListener(new PostJobListener());

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
            double pay = 0.0d;
            EditText etJobTitle, etDescription, etPay;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);
            Log.i("Current user ", Integer.toString(currentUserId));

            etJobTitle = (EditText) findViewById(R.id.et_job_title);
            jobTitle = etJobTitle.getText().toString();
            etDescription = (EditText) findViewById(R.id.et_job_description);
            description = etDescription.getText().toString();
            etPay = (EditText) findViewById(R.id.et_pay);
            payStr = etPay.getText().toString();
            if(payStr != null && !payStr.trim().equalsIgnoreCase("")) {
                pay = Double.parseDouble(payStr);
            }
            JobCategory jobCategory = (JobCategory) spnCategory.getSelectedItem();

            if(jobTitle.length() == 0)
                Toast.makeText(getApplicationContext(), "You must specify a job title", Toast.LENGTH_SHORT).show();
            else if(jobCategory.getId() == Constants.INVALID_ITEM_VALUE)
                Toast.makeText(getApplicationContext(), "You must select a job category from the dropdown", Toast.LENGTH_SHORT).show();
            else if(pay <= 0.0)
                Toast.makeText(getApplicationContext(), "You have inserted an invalid pay amount", Toast.LENGTH_SHORT).show();
            else {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String now = dateFormat.format(date);
                Job job = new Job(jobTitle, description, false, now, 0.0f, 0.0f, jobCategory.getId(), currentUserId, null);
                db.addJob(job);

                Intent intMyJobs = new Intent(PostJobActivity.this, MyJobsActivity.class);
                startActivity(intMyJobs);
            }
        }
    }

    ////////////////////////////////////////

    private class AddImageListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            showPictureDialog();
        }
    }

    //Pick image from gallery || camera
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, Constants.GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == Constants.GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    ivPicture.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == Constants.CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ivPicture.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory().toString() + Constants.PATH_JOB_IMAGES);
        //Toast.makeText(getApplicationContext(), Environment.getExternalStorageDirectory().toString() + "/post_job_image/", Toast.LENGTH_SHORT).show();
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
            Toast.makeText(getApplicationContext(), "Failed to create directory. Grant storage permission.", Toast.LENGTH_LONG).show();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            //Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed to save image. Grant storage permission.", Toast.LENGTH_LONG).show();
        }
        return "";
    }
    ////////////////////////////////////////
}
