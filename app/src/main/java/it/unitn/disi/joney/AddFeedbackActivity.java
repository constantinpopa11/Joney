package it.unitn.disi.joney;

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

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

        Intent intent = getIntent();
        final int jobId, receiverId;
        jobId = intent.getIntExtra("jobId",-1);
        receiverId = intent.getIntExtra("receiverId",1);
        this.setTitle("Write a review for " + db.getUserById(receiverId).getFirstName());

        tvJobName = (TextView) findViewById(R.id.tv_job_name);
        tvJobName.setText(db.getJobById(jobId).getTitle());
        etComment = (EditText) findViewById(R.id.et_feedback_comment);
        rbRate = (RatingBar) findViewById(R.id.rb_feedback_rate);
        btnSave = (Button) findViewById(R.id.btn_save_feedback);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                int rate = (int) rbRate.getRating();
                if(comment.length() == 0)
                    Toast.makeText(getApplicationContext(), "You must write a review", Toast.LENGTH_SHORT).show();
                else if (rate == 0)
                    Toast.makeText(getApplicationContext(), "You must choose the number of stars", Toast.LENGTH_SHORT).show();
                else {
                    //Toast.makeText(getApplicationContext(), String.valueOf(rate), Toast.LENGTH_SHORT).show();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
                    Date date = new Date();
                    String now = dateFormat.format(date);

                    Feedback feedback = new Feedback(rate,comment,now,jobId,currentUserId,receiverId);
                    db.addFeedback(feedback);
                    Toast.makeText(getApplicationContext(),"Feedback sent!",Toast.LENGTH_SHORT).show();

                    Intent intHome = new Intent(AddFeedbackActivity.this,HomeActivity.class);
                    startActivity(intHome);
                }
            }
        });

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
