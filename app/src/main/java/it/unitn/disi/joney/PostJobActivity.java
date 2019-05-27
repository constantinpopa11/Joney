package it.unitn.disi.joney;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class PostJobActivity extends AppCompatActivity {

    Spinner spnCategory;
    Button btnPostJob;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spnCategory = (Spinner) findViewById(R.id.spn_category);
        List<JobCategory> jobCategoryList = db.getAllJobCategories();
        //add invalid choice manually at the beginning
        jobCategoryList.add(0, new JobCategory(Constants.INVALID_JOB_CATEGORY, Constants.NO_JOB_CATEGORY_SELECTED, null));
        ArrayAdapter<JobCategory> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, jobCategoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(dataAdapter);
        spnCategory.setOnItemSelectedListener(new CustomOnItemSelectedListener());

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
            else if(jobCategory.getId() == Constants.INVALID_JOB_CATEGORY)
                Toast.makeText(getApplicationContext(), "You must select a job category from the dropdown", Toast.LENGTH_SHORT).show();
            else if(pay <= 0.0)
                Toast.makeText(getApplicationContext(), "You have inserted an invalid pay amount", Toast.LENGTH_SHORT).show();
            else {
                Job job = new Job(jobTitle, description, false, 0.0f, 0.0f, jobCategory.getId(), currentUserId, null);
                db.addJob(job);

                Intent intMyJobs = new Intent(PostJobActivity.this, HomeActivity.class);
                startActivity(intMyJobs);
            }
        }
    }


}
