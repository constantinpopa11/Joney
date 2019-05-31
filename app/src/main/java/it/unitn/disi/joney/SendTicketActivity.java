package it.unitn.disi.joney;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import java.util.Calendar;
import java.util.List;

public class SendTicketActivity extends AppCompatActivity {

    Spinner spnJob;
    EditText etIssue;
    Button btnSendTicket;
    ImageView ivPicture;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_ticket);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(getApplicationContext(), drawer));

        spnJob = (Spinner) findViewById(R.id.spn_job);
        etIssue = (EditText) findViewById(R.id.et_issue_description);

        ivPicture = (ImageView) findViewById(R.id.iv_add_pictures_send_ticket);
        ivPicture.setOnClickListener(new AddImageListener());

        List<Job> jobList = db.getAllUserJobs(currentUserId);
        //add invalid choice manually at the beginning
        jobList.add(0, new Job(Constants.INVALID_ITEM_VALUE, Constants.NO_SPINNER_SELCTION, null));
        ArrayAdapter<Job> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, jobList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnJob.setAdapter(dataAdapter);
        //spnJob.setOnItemSelectedListener(new OnSpinnerItemSelectedListener());

        btnSendTicket = (Button) findViewById(R.id.btn_send_ticket);
        btnSendTicket.setOnClickListener(new SendTicketListener());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_ticket, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SendTicketListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Job job;
            String issue;

            job = (Job) spnJob.getSelectedItem();
            issue = etIssue.getText().toString();

            if (job.getId() == Constants.INVALID_ITEM_VALUE)
                Toast.makeText(getApplicationContext(), "You must select a job from the dropdown", Toast.LENGTH_SHORT).show();
            else if (issue.length() == 0)
                Toast.makeText(getApplicationContext(), "You must write the issue", Toast.LENGTH_SHORT).show();
            else {
                Ticket ticket = new Ticket(job.getId(),issue);
                db.addTicket(ticket);

                Toast.makeText(getApplicationContext(), "Ticket sent correctly", Toast.LENGTH_SHORT).show();
                Intent intHome = new Intent(SendTicketActivity.this, HomeActivity.class);
                startActivity(intHome);
            }

        }
    }

    public class AddImageListener implements View.OnClickListener{
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
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
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
                Environment.getExternalStorageDirectory() + "/Joney/ticket_image/");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
            Toast.makeText(getApplicationContext(), "Failed to create directory0. Grant storage permission.", Toast.LENGTH_LONG).show();
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

}

