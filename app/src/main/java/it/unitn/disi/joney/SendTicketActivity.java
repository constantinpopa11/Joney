package it.unitn.disi.joney;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static it.unitn.disi.joney.ImageUploadUtils.saveImage;

public class SendTicketActivity extends AppCompatActivity implements PictureUploadListener {

    Spinner spnJob;
    EditText etIssue;
    Button btnSendTicket;
    ImageView btnAddPicture;
    LinearLayout llPictures;

    int uploadedPicCounter = 0;
    int picToBeChangedIndex = -1;
    int currentUserId;

    private Context mContext;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_ticket);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

        mContext = this;

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

        llPictures = (LinearLayout) findViewById(R.id.ll_pictures);
        btnAddPicture = (ImageView) findViewById(R.id.btn_add_picture);
        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isStoragePermissionGranted(getApplicationContext()))
                    showMissingPermissionAlert("Storage", "Pictures");
                else if (uploadedPicCounter < Constants.MAX_JOB_PICTURE_NUMBER) {
                    ImageUploadUtils.showPictureOptionDialog(mContext, SendTicketActivity.this, -1,Constants.PATH_TICKET_IMAGES);
                }
            }
        });

        List<Job> jobList = db.getAllUserJobs(currentUserId);
        //add invalid choice manually at the beginning
        jobList.add(0, new Job(Constants.INVALID_ITEM_VALUE, Constants.NO_SPINNER_SELECTION, null));
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
                Toast.makeText(getApplicationContext(), "Issue can't be empty", Toast.LENGTH_SHORT).show();
            else {
                Ticket ticket = new Ticket(job.getId(), issue);
                int ticketId = db.addTicket(ticket);


                int attachmentNum = llPictures.getChildCount();
                for (int i = 0; i < attachmentNum; i++) {
                    //save image on the device and register it to the database
                    ImageView imgView = (ImageView) llPictures.getChildAt(i);
                    Bitmap bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                    String path = saveImage(getApplicationContext(), bitmap, Constants.PATH_TICKET_IMAGES);
                    TicketImage ticketImage = new TicketImage(path, ticketId);
                    db.addTicketImage(ticketImage);
                }

                //Toast.makeText(getApplicationContext(), "Ticket sent successfully", Toast.LENGTH_SHORT).show();
                Intent intHome = new Intent(SendTicketActivity.this, HomeActivity.class);
                startActivity(intHome);
                finish();
            }

        }
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
                Log.d("Uri", contentURI.toString());
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == Constants.UPLOAD_FROM_CAMERA) {
            File tempImg = new File(Environment.getExternalStorageDirectory(), Constants.PATH_TICKET_IMAGES + "temp.jpg");
            /*try {
                copyFile(tempImg, new File(Environment.getExternalStorageDirectory(), Constants.PATH_TICKET_IMAGES + Calendar.getInstance().getTimeInMillis() + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            Uri photoUri = Uri.fromFile(tempImg);
            //bitmap = (Bitmap) data.getExtras().get("data");
            Log.d("Uri", photoUri.toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        }

        if (bitmap != null) {
            //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();

            ImageView newImgView;
            if (picToBeChangedIndex != -1) {
                newImgView = (ImageView) llPictures.getChildAt(picToBeChangedIndex);
            } else {
                newImgView = new ImageView(this);
            }

            final ImageView imageView = newImgView;
            //setting image resource
            imageView.setImageBitmap(bitmap);

            if (picToBeChangedIndex == -1) {

                final int thumbnailSize = (int) getApplicationContext().getResources().getDimension(R.dimen.post_job_thumbnail_size);
                final int thumbnailMargin = (int) getApplicationContext().getResources().getDimension(R.dimen.post_job_thumbnail_margin);

                //setting image size and margins
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
                layoutParams.setMargins(0, 0, thumbnailMargin, 0);
                imageView.setLayoutParams(layoutParams);


                llPictures.addView(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int clickedImgIndex = llPictures.indexOfChild(imageView);
                        ImageUploadUtils.showPictureOptionDialog(mContext, SendTicketActivity.this, clickedImgIndex, Constants.PATH_TICKET_IMAGES);
                    }
                });
            }

            picToBeChangedIndex = -1;
            uploadedPicCounter++;
            //hide add btn if there are 4 pics already
            if (uploadedPicCounter == Constants.MAX_JOB_PICTURE_NUMBER) {
                btnAddPicture.setVisibility(View.GONE);
            }
        }
    }

    public void onRemovePicture(int imgViewIndex) {

        ImageView imgView = (ImageView) llPictures.getChildAt(imgViewIndex);

        if (imgView != null) {
            llPictures.removeView(imgView);
            uploadedPicCounter--;

            if (btnAddPicture.getVisibility() == View.GONE) {
                btnAddPicture.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onChangePicture(int index) {
        this.picToBeChangedIndex = index;
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
}
