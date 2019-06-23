package it.unitn.disi.joney;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class JobDetailActivity extends AppCompatActivity {

    DatabaseHandler db;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DatabaseHandler(this);
        mContext = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(this, drawer));

        Intent intent = getIntent();
        final int jobId = intent.getIntExtra(Constants.JOB_ID_EXTRA, 0);
        final Job job = db.getJobById(jobId);
        int activityType = intent.getIntExtra(Constants.JOB_DETAIL_ACTIVITY_TYPE, Constants.JOB_RESULT_DETAILS);
        int candidateNum = db.getCandidatesByJobId(jobId).size();

        Button btnCandidateAction = findViewById(R.id.btn_candidate_action);
        TextView tvCandidateAction = findViewById(R.id.tv_candidate_action);

        switch (activityType){
            case Constants.POSTED_JOB_DETAILS:
                if(job.getStatus() == Constants.JOB_STATUS_AWAITING_CANDIDATES){

                    if(candidateNum > 0){
                        tvCandidateAction.setText("There " + (candidateNum==1 ? "is " : "are ") +
                                candidateNum + " " +
                                (candidateNum==1 ? "user" : "users") +  " interested in this job. Choose among the candidates now!");
                        btnCandidateAction.setText("Choose candidate");
                        btnCandidateAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, ChooseCandidateActivity.class);
                                intent.putExtra(Constants.JOB_ID_EXTRA, jobId);
                                startActivity(intent);
                            }
                        });

                    } else {
                        btnCandidateAction.setVisibility(View.GONE);
                        tvCandidateAction.setText("There are currently no candidates for this job.");
                    }

                } else if (job.getStatus() == Constants.JOB_STATUS_AWAITING_COMPLETION){
                    tvCandidateAction.setText("Is this job done? Mark it as completed and let us know how it went.");
                    btnCandidateAction.setText("Mark as completed");
                    btnCandidateAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db.updateJobStatus(jobId, Constants.JOB_STATUS_COMPLETED);
                            Toast.makeText(mContext, "Job has been marked as completed", Toast.LENGTH_SHORT).show();

                            Intent jobDetailIntent = new Intent(mContext, JobDetailActivity.class);
                            jobDetailIntent.putExtra(Constants.JOB_ID_EXTRA, jobId);
                            jobDetailIntent.putExtra(Constants.JOB_DETAIL_ACTIVITY_TYPE, Constants.POSTED_JOB_DETAILS);
                            mContext.startActivity(jobDetailIntent);
                            finish();
                        }
                    });


                } else if(job.getStatus() == Constants.JOB_STATUS_COMPLETED){
                    final Feedback feedback = db.getUserFeedbackForJob(jobId, currentUserId);

                    if(feedback == null) {
                        tvCandidateAction.setText("Are you happy with the work the other user has done? Let us know!");
                        btnCandidateAction.setText("Leave feedback");

                        btnCandidateAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent feedbackIntent = new Intent(mContext, AddFeedbackActivity.class);
                                feedbackIntent.putExtra("receiverId", job.getWorkerId());
                                feedbackIntent.putExtra("jobId", job.getId());
                                feedbackIntent.putExtra("activityTpe", Constants.POSTED_JOB_DETAILS);
                                startActivity(feedbackIntent);
                            }
                        });
                    } else {
                        tvCandidateAction.setText("Thank you for your feedback!");
                        btnCandidateAction.setVisibility(View.GONE);
                    }


                }

                LinearLayout llUserPreview = (LinearLayout) findViewById(R.id.ll_user_preview);
                llUserPreview.setVisibility(View.GONE);
                break;

            case Constants.PENDING_JOB_DETAILS:

                if(job.getStatus() == Constants.JOB_STATUS_AWAITING_CANDIDATES){
                    tvCandidateAction.setText("Have you changed your mind? Cancel your request below");
                    btnCandidateAction.setText("Cancel request");
                    btnCandidateAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            db.removeJobCandidate(currentUserId, jobId);
                            Toast.makeText(mContext, "Your request has been canceled successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, MyJobsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else if(job.getStatus() > Constants.JOB_STATUS_AWAITING_CANDIDATES && job.workerId != currentUserId){
                    tvCandidateAction.setText("Your request has been rejected. Better luck next time!");

                    btnCandidateAction.setVisibility(View.GONE);
                } else if(job.getStatus() > Constants.JOB_STATUS_AWAITING_CANDIDATES && job.workerId == currentUserId){
                    Feedback feedback = db.getUserFeedbackForJob(jobId, currentUserId);

                    if(feedback == null) {
                        tvCandidateAction.setText("Were you happy to work for the other user? Let us know!");
                        btnCandidateAction.setText("Leave feedback");

                        btnCandidateAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent feedbackIntent = new Intent(mContext, AddFeedbackActivity.class);
                                feedbackIntent.putExtra("receiverId", job.getAuthorId());
                                feedbackIntent.putExtra("jobId", job.getId());
                                feedbackIntent.putExtra("activityType", Constants.COMPLETED_JOB_DETAILS);
                                startActivity(feedbackIntent);
                            }
                        });
                    } else {
                        tvCandidateAction.setText("Thank you for your feedback!");
                        btnCandidateAction.setVisibility(View.GONE);
                    }
                }
                break;

            case Constants.COMPLETED_JOB_DETAILS:
                Feedback feedback = db.getUserFeedbackForJob(jobId, currentUserId);

                if(feedback == null) {
                    tvCandidateAction.setText("Were you happy to work for the other user? Let us know!");
                    btnCandidateAction.setText("Leave feedback");

                    btnCandidateAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent feedbackIntent = new Intent(mContext, AddFeedbackActivity.class);
                            feedbackIntent.putExtra("receiverId", job.getAuthorId());
                            feedbackIntent.putExtra("jobId", job.getId());
                            feedbackIntent.putExtra("activityType", Constants.COMPLETED_JOB_DETAILS);
                            startActivity(feedbackIntent);
                        }
                    });
                } else {
                    tvCandidateAction.setText("Thank you for your feedback!");
                    btnCandidateAction.setVisibility(View.GONE);
                }


                break;

            case Constants.JOB_RESULT_DETAILS:
                tvCandidateAction.setText("Hurry up, there are other users watching this post. Send your request now!");
                btnCandidateAction.setText("Send request");
                btnCandidateAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JobCandidate jobCandidate = new JobCandidate(jobId, currentUserId);
                        db.addJobCandidate(jobCandidate);
                        Toast.makeText(mContext, "Your request has been sent successfully", Toast.LENGTH_SHORT).show();

                        Intent jobDetailIntent = new Intent(mContext, JobDetailActivity.class);
                        jobDetailIntent.putExtra(Constants.JOB_ID_EXTRA, jobId);
                        jobDetailIntent.putExtra(Constants.JOB_DETAIL_ACTIVITY_TYPE, Constants.PENDING_JOB_DETAILS);
                        mContext.startActivity(jobDetailIntent);
                        finish();
                    }
                });
                break;
        }

        ImageView ivMainPicture = (ImageView) findViewById(R.id.iv_job_main_picture);
        List<JobImage> jobImages = db.getJobImages(jobId);
        if (jobImages.size() > 0) {
            File imgFile = new File(jobImages.get(0).getSource());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivMainPicture.setImageBitmap(myBitmap);
            }
        }

        if (jobImages.size() > 1) {
            LinearLayout llPictures = (LinearLayout) findViewById(R.id.ll_pictures);

            for(int i=1; i<jobImages.size(); i++){
                File imgFile = new File(jobImages.get(i).getSource());

                final int thumbnailSize = (int) getApplicationContext().getResources().getDimension(R.dimen.job_detail_thumbnail_size);
                final int thumbnailMargin = (int) getApplicationContext().getResources().getDimension(R.dimen.job_detail_thumbnail_margin);


                ImageView imageView = new ImageView(this);
                //setting image size and margins
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
                layoutParams.setMargins(thumbnailMargin, 0, thumbnailMargin, 0);
                imageView.setLayoutParams(layoutParams);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
                llPictures.addView(imageView);
            }
        }

        TextView tvTitle = (TextView) findViewById(R.id.tv_job_title);
        tvTitle.setText(job.getTitle());

        TextView tvJobCategory = (TextView) findViewById(R.id.tv_job_category);
        JobCategory jobCategory = db.getJobCategoryById(job.getCategoryId());
        tvJobCategory.setText(jobCategory.getName());

        TextView tvPay = (TextView) findViewById(R.id.tv_pay);
        tvPay.append(Integer.toString(job.getPay()));

        TextView tvCreatedAt = (TextView) findViewById(R.id.tv_created_at);
        tvCreatedAt.setText(job.getCreatedAt());

        ImageView ivStatus = (ImageView) findViewById(R.id.iv_status);
        TextView tvStatus = (TextView) findViewById(R.id.tv_status);
        if(job.getStatus() == Constants.JOB_STATUS_AWAITING_CANDIDATES){
            ivStatus.setBackground(getResources().getDrawable(R.drawable.ic_people));
            tvStatus.setText("Awaiting candidates");
        } else if(job.getStatus() == Constants.JOB_STATUS_AWAITING_COMPLETION) {
            ivStatus.setBackground(getResources().getDrawable(R.drawable.ic_times));
            tvStatus.setText("In progress");
        } else if(job.getStatus() == Constants.JOB_STATUS_COMPLETED) {
            ivStatus.setBackground(getResources().getDrawable(R.drawable.ic_check));
            tvStatus.setText("Completed");
        }

        TextView tvAddress = (TextView) findViewById(R.id.tv_job_location);
        tvAddress.setText(Utils.getStreetName(job.getLatitude(), job.getLongitude(), this));

        TextView tvDescription = (TextView) findViewById(R.id.tv_job_description);
        tvDescription.setText(job.getDescription());

        final User author = db.getUserById(job.getAuthorId());

        final Intent intUserProfile = new Intent(mContext, UserProfileActivity.class);
        intUserProfile.putExtra("userId", author.getId());
        intUserProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TextView tvAuthorName = (TextView) findViewById(R.id.tv_user_name);
        tvAuthorName.setText(author.getFirstName() + " " + author.getLastName());
        tvAuthorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intUserProfile);
            }
        });

        RatingBar rbAverageRating = (RatingBar) findViewById(R.id.rb_rating_average);
        rbAverageRating.setRating(Utils.getUserAverageRating(this, author.getId()));

        LinearLayout llChat = (LinearLayout) findViewById(R.id.ll_chat);
        llChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intChat = new Intent(JobDetailActivity.this, ChatActivity.class);
                intChat.putExtra("senderId", currentUserId);
                intChat.putExtra("receiverId", author.getId());
                startActivity(intChat);
            }
        });

        TextView tvAuthorDescription = (TextView) findViewById(R.id.tv_user_description);
        tvAuthorDescription.setText(author.getDescription());

        UserProfileImage upi = db.getUserProfileImage(author.getId());
        ImageView ivUserProfileImage = (ImageView) findViewById(R.id.iv_user_profile_image);
        if (upi != null) {
            File imgFile = new File(upi.getSource());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivUserProfileImage.setImageBitmap(myBitmap);
            }
        }
        ivUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intUserProfile);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.job_detail, menu);
        return true;
    }

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

}
