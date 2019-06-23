package it.unitn.disi.joney;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ChooseCandidateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_candidate);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DatabaseHandler db = new DatabaseHandler(this);

        Intent intent = getIntent();
        int jobId = intent.getIntExtra(Constants.JOB_ID_EXTRA, -1);

        ListView listView = (ListView) findViewById(R.id.lv_candidates);
        List<JobCandidate> candidateList = db.getCandidatesByJobId(jobId);


        CandidateListAdapter adapter = new CandidateListAdapter(this, candidateList);
        listView.setAdapter(adapter);
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
