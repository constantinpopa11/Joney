package it.unitn.disi.joney;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class FindJobActivity extends AppCompatActivity {

    SeekBar sbDistance;
    TextView tvDistance;
    Spinner spnCategory;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvDistance = (TextView) findViewById(R.id.tv_distance);

        sbDistance = (SeekBar) findViewById(R.id.sb_distance);
        addSeekBarDistanceListener();

        spnCategory = (Spinner) findViewById(R.id.spn_category);
        List<JobCategory> jobCategoryList = db.getAllJobCategories();
        //add "no selection" choice manually at the beginning
        jobCategoryList.add(0, new JobCategory(Constants.INVALID_JOB_CATEGORY, Constants.NO_JOB_CATEGORY_SELECTED, null));
        ArrayAdapter<JobCategory> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, jobCategoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(dataAdapter);
        spnCategory.setOnItemSelectedListener(new OnJobCategorySelectedListener());

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
}
