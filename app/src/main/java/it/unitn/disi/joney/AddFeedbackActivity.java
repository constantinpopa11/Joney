package it.unitn.disi.joney;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddFeedbackActivity extends AppCompatActivity {

    TextView tvJobName;
    EditText etComment;
    RatingBar rbRate;
    Button btnSave;
    int currentUserId;
    Context mContext;

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);

        db = new DatabaseHandler(this);
        mContext = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

        Intent intent = getIntent();
        final int jobId, receiverId, activityType;
        jobId = intent.getIntExtra("jobId",-1);
        receiverId = intent.getIntExtra("receiverId",1);
        activityType = intent.getIntExtra("activityType", Constants.POSTED_JOB_DETAILS);

        this.setTitle("Write a review for " + db.getUserById(receiverId).getFirstName());

        tvJobName = (TextView) findViewById(R.id.tv_job_name);
        tvJobName.setText(db.getJobById(jobId).getTitle());
        etComment = (EditText) findViewById(R.id.et_feedback_comment);
        rbRate = (RatingBar) findViewById(R.id.rb_feedback_rate);
        btnSave = (Button) findViewById(R.id.btn_save_feedback);

        if(db.isFeedbackGiven(jobId,currentUserId)) {
            btnSave.setVisibility(View.INVISIBLE);
            etComment.setText("FEEDBACK ALREADY GIVEN!");
            etComment.setEnabled(false);
        }
        else {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comment = etComment.getText().toString();
                    int rate = (int) rbRate.getRating();
                    if (comment.length() == 0)
                        Toast.makeText(getApplicationContext(), "Review can't be empty", Toast.LENGTH_SHORT).show();
                    else if (rate == 0)
                        Toast.makeText(getApplicationContext(), "Please choose a rating before submitting", Toast.LENGTH_SHORT).show();
                    else {
                        //Toast.makeText(getApplicationContext(), String.valueOf(rate), Toast.LENGTH_SHORT).show();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
                        Date date = new Date();
                        String now = dateFormat.format(date);

                        Feedback feedback = new Feedback(rate, comment, now, jobId, currentUserId, receiverId);
                        db.addFeedback(feedback);
                        //Toast.makeText(getApplicationContext(), "Feedback sent!", Toast.LENGTH_SHORT).show();

                        Intent jobDetailIntent = new Intent(mContext, JobDetailActivity.class);
                        jobDetailIntent.putExtra(Constants.JOB_ID_EXTRA, jobId);
                        jobDetailIntent.putExtra(Constants.JOB_DETAIL_ACTIVITY_TYPE, activityType);
                        jobDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(jobDetailIntent);
                    }
                }
            });
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
}
