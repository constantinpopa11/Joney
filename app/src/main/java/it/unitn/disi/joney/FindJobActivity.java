package it.unitn.disi.joney;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class FindJobActivity extends AppCompatActivity {

    SeekBar sbDistance;
    TextView tvDistance, tvFilterToggle;
    ImageView ivFilterToggle;
    Spinner spnCategory;
    boolean collapsedFilters = false;
    LinearLayout llSearchFilters, llSearchResults, llFilterToggle;
    Button btnFindJobs;
    ExpandableListView elvJobResults;
    ExpandableJobListAdapter jobListAdapter;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvDistance = (TextView) findViewById(R.id.tv_distance);
        tvFilterToggle = (TextView) findViewById(R.id.tv_filter_toggle);
        ivFilterToggle = (ImageView) findViewById(R.id.iv_filter_toggle);

        sbDistance = (SeekBar) findViewById(R.id.sb_distance);
        addSeekBarDistanceListener();

        spnCategory = (Spinner) findViewById(R.id.spn_category);
        List<JobCategory> jobCategoryList = db.getAllJobCategories();
        //add "no selection" choice manually at the beginning
        jobCategoryList.add(0, new JobCategory(Constants.INVALID_ITEM_VALUE, Constants.NO_SPINNER_SELCTION, null));
        ArrayAdapter<JobCategory> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, jobCategoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(dataAdapter);
        spnCategory.setOnItemSelectedListener(new OnSpinnerItemSelectedListener());

        llSearchFilters = (LinearLayout) findViewById(R.id.ll_search_filters);
        llSearchResults = (LinearLayout) findViewById(R.id.ll_search_results);

        llFilterToggle = (LinearLayout) findViewById(R.id.ll_filter_toggle);

        llFilterToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(collapsedFilters) {
                    expandSearchFilters();
                } else {
                    collapseSearchFilters();
                }

            }
        });

        btnFindJobs = (Button) findViewById(R.id.btn_find_jobs);
        btnFindJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!collapsedFilters){
                    collapseSearchFilters();
                }
                elvJobResults = findViewById(R.id.elv_job_results);

                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);

                final List<Job> postedJobs = db.getUserPostedJobs(currentUserId);
                for(Job job : postedJobs) {
                    job.setJobCategory(db.getJobCategoryById(job.getCategoryId()));
                    job.setWorker(db.getUserById(job.getWorkerId()));
                    job.setAuthor(db.getUserById(job.getAuthorId()));
                }

                if(postedJobs.size() > 0) {
                    jobListAdapter = new ExpandableJobListAdapter(getApplicationContext(), postedJobs, Constants.POSTED_JOB_TAB);
                    elvJobResults.setAdapter(jobListAdapter);
                    elvJobResults.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                        @Override
                        public void onGroupExpand(int groupPosition) {
                            Toast.makeText(getApplicationContext(),
                                    postedJobs.get(groupPosition).getTitle() + " List Expanded.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    elvJobResults.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                        @Override
                        public void onGroupCollapse(int groupPosition) {
                            Toast.makeText(getApplicationContext(),
                                    postedJobs.get(groupPosition).getTitle() + " List Collapsed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

                    elvJobResults.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v,
                                                    int groupPosition, int childPosition, long id) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    postedJobs.get(groupPosition).getTitle()
                                            + " -> "
                                            + postedJobs.get(groupPosition).getDescription(), Toast.LENGTH_SHORT
                            ).show();
                            return false;
                        }
                    });
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

    public void addSeekBarDistanceListener(){
        sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvDistance.setText(Integer.toString(progress));
                //Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void collapseSearchFilters(){
        Utils.collapse(llSearchFilters);
        collapsedFilters = true;
        tvFilterToggle.setText("Show filters");
        ivFilterToggle.setBackground(getResources().getDrawable(R.drawable.ic_arrow_down));
    }

    private void expandSearchFilters(){
        Utils.expand(llSearchFilters);
        collapsedFilters = false;
        tvFilterToggle.setText("Hide filters");
        ivFilterToggle.setBackground(getResources().getDrawable(R.drawable.ic_arrow_up));
    }
}
