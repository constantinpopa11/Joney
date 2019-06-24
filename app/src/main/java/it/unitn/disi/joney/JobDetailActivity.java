package it.unitn.disi.joney;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.graphics.Paint;
import android.net.Uri;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JobDetailActivity extends AppCompatActivity {

    DatabaseHandler db;
    Context mContext;

    private Animator currentAnimator;
    private int shortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new DatabaseHandler(this);
        mContext = this;

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

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
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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
                            //Toast.makeText(mContext, "Job has been marked as completed", Toast.LENGTH_SHORT).show();

                            Intent jobDetailIntent = new Intent(mContext, JobDetailActivity.class);
                            jobDetailIntent.putExtra(Constants.JOB_ID_EXTRA, jobId);
                            jobDetailIntent.putExtra(Constants.JOB_DETAIL_ACTIVITY_TYPE, Constants.POSTED_JOB_DETAILS);
                            jobDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(jobDetailIntent);
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
                            //Toast.makeText(mContext, "Your request has been canceled successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, MyJobsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
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
                        //Toast.makeText(mContext, "Your request has been sent successfully", Toast.LENGTH_SHORT).show();

                        Intent jobDetailIntent = new Intent(mContext, JobDetailActivity.class);
                        jobDetailIntent.putExtra(Constants.JOB_ID_EXTRA, jobId);
                        jobDetailIntent.putExtra(Constants.JOB_DETAIL_ACTIVITY_TYPE, Constants.PENDING_JOB_DETAILS);
                        jobDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(jobDetailIntent);
                    }
                });
                break;
        }

        final ImageView ivMainPicture = (ImageView) findViewById(R.id.iv_job_main_picture);
        List<JobImage> jobImages = db.getJobImages(jobId);
        if (jobImages.size() > 0) {
            File imgFile = new File(jobImages.get(0).getSource());
            if (imgFile.exists()) {
                final Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivMainPicture.setImageBitmap(bitmap);
                ivMainPicture.setBackgroundResource(0);
                ivMainPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomImageFromThumb(ivMainPicture, bitmap);
                    }
                });
            }
        }

        if (jobImages.size() > 1) {
            LinearLayout llPictures = (LinearLayout) findViewById(R.id.ll_pictures);

            for(int i=1; i<jobImages.size(); i++){
                File imgFile = new File(jobImages.get(i).getSource());

                final int thumbnailSize = (int) getApplicationContext().getResources().getDimension(R.dimen.job_detail_thumbnail_size);
                final int thumbnailMargin = (int) getApplicationContext().getResources().getDimension(R.dimen.job_detail_thumbnail_margin);


                final ImageView imageView = new ImageView(this);
                //setting image size and margins
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
                layoutParams.setMargins(thumbnailMargin, 0, thumbnailMargin, 0);
                imageView.setLayoutParams(layoutParams);
                if (imgFile.exists()) {
                    final Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            zoomImageFromThumb(imageView, bitmap);
                        }
                    });
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

        final TextView tvAddress = (TextView) findViewById(R.id.tv_job_location);
        tvAddress.setText(Utils.getStreetName(job.getLatitude(), job.getLongitude(), this));
        tvAddress.setPaintFlags(tvAddress.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri
                        .parse("geo:0,0?q=" + tvAddress.getText().toString()));
                startActivity(geoIntent);
            }
        });

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

    private void zoomImageFromThumb(final View thumbView, Bitmap zoomedBitmap) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageBitmap(zoomedBitmap);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.fl_container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

}
