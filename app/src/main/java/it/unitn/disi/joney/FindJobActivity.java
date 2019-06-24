package it.unitn.disi.joney;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    TextView tvDistance, tvFilterToggle, tvSearchResults;
    ImageView ivFilterToggle;
    Spinner spnCategory;
    EditText etMinPay, etMaxPay, etAddress;
    LinearLayout llSearchFilters, llSearchResults, llFilterToggle;
    Button btnFindJobs;

    Pair<Double,Double> location = null;

    boolean collapsedFilters = false;

    ExpandableListView elvJobResults;
    ExpandableJobListAdapter jobListAdapter;
    List<Job> eligibleJobs;

    SharedPreferences prefs;
    Context mContext;
    DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mContext = this;
        db = new DatabaseHandler(mContext);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        tvDistance = (TextView) findViewById(R.id.tv_distance);
        tvDistance.setText("1");
        etMinPay = (EditText) findViewById(R.id.et_min_pay);
        etMaxPay = (EditText) findViewById(R.id.et_max_pay);
        tvFilterToggle = (TextView) findViewById(R.id.tv_filter_toggle);
        ivFilterToggle = (ImageView) findViewById(R.id.iv_filter_toggle);

        sbDistance = (SeekBar) findViewById(R.id.sb_distance);
        addSeekBarDistanceListener();

        spnCategory = (Spinner) findViewById(R.id.spn_category);
        List<JobCategory> jobCategoryList = db.getAllJobCategories();
        //add "no selection" choice manually at the beginning
        jobCategoryList.add(0, new JobCategory(Constants.INVALID_ITEM_VALUE, Constants.NO_SPINNER_SELECTION, null));
        ArrayAdapter<JobCategory> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, jobCategoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(dataAdapter);
        spnCategory.setOnItemSelectedListener(new OnSpinnerItemSelectedListener());

        llSearchFilters = (LinearLayout) findViewById(R.id.ll_search_filters);
        llSearchResults = (LinearLayout) findViewById(R.id.ll_search_results);

        tvSearchResults = (TextView) findViewById(R.id.tv_search_results);
        elvJobResults = findViewById(R.id.elv_job_results);

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

        etAddress = (EditText) findViewById(R.id.et_job_address);
        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(FindJobActivity.this, MapActivity.class);
                startActivityForResult(mapIntent,Constants.PICK_LOCATION);
            }
        });

        if (!Utils.isLocationPermissionGranted(this))
            showMissingPermissionAlert("Location", "GPS");
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
                        final String streetName = Utils.getStreetName(location.first,location.second, mContext);
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

        btnFindJobs = (Button) findViewById(R.id.btn_find_jobs);
        btnFindJobs.setOnClickListener(new FindJobListener());


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

    private class FindJobListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int distance = Integer.parseInt((String)tvDistance.getText());
            int jobCategoryId = ((JobCategory) spnCategory.getSelectedItem()).getId();
            String minPayStr = etMinPay.getText().toString();
            String maxPayStr = etMaxPay.getText().toString();
            int minPay = 0, maxPay = Integer.MAX_VALUE;
            if(minPayStr != null && !minPayStr.trim().equalsIgnoreCase("")) {
                minPay = Integer.parseInt(minPayStr);
            }
            if(maxPayStr != null && !maxPayStr.trim().equalsIgnoreCase("")) {
                maxPay = Integer.parseInt(maxPayStr);
            }

            if(location == null) {
                Toast.makeText(mContext,
                        "You have inserted an invalid address.",
                        Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getApplicationContext(),
                //        "distance " + distance + " jobCategory " + jobCategoryId + " minpay " + minPay + " maxpay " + maxPay,
                //        Toast.LENGTH_SHORT).show();

                if(eligibleJobs != null)
                    eligibleJobs.clear();
                if(jobListAdapter != null)
                    jobListAdapter.notifyDataSetChanged();

                final int currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);
                eligibleJobs = db.getJobsBySearchFilters(currentUserId, location, distance, jobCategoryId, minPay, maxPay);
                for (Job job : eligibleJobs) {
                    job.setJobCategory(db.getJobCategoryById(job.getCategoryId()));
                    job.setWorker(db.getUserById(job.getWorkerId()));
                    job.setAuthor(db.getUserById(job.getAuthorId()));
                }

                if (eligibleJobs.size() > 0) {
                    tvSearchResults.setVisibility(View.GONE);
                    if(!collapsedFilters){
                        collapseSearchFilters();
                    }

                    jobListAdapter = new ExpandableJobListAdapter(getApplicationContext(), eligibleJobs, Constants.SEARCH_RESULT_TAB);
                    elvJobResults.setAdapter(jobListAdapter);

                    /*elvJobResults.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                        @Override
                        public void onGroupExpand(int groupPosition) {
                            Toast.makeText(getApplicationContext(),
                                    eligibleJobs.get(groupPosition).getTitle() + " List Expanded.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });*/

                    /*elvJobResults.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                        @Override
                        public void onGroupCollapse(int groupPosition) {
                            Toast.makeText(getApplicationContext(),
                                    eligibleJobs.get(groupPosition).getTitle() + " List Collapsed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });*/

                    /*elvJobResults.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v,
                                                    int groupPosition, int childPosition, long id) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    eligibleJobs.get(groupPosition).getTitle()
                                            + " -> "
                                            + eligibleJobs.get(groupPosition).getDescription(), Toast.LENGTH_SHORT
                            ).show();
                            return false;
                        }
                    });*/
                } else {
                    tvSearchResults.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }


        if (requestCode == Constants.PICK_LOCATION) {
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
