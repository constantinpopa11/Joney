package it.unitn.disi.joney;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    LinearLayout llFindJob, llPostJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        llFindJob = (LinearLayout) findViewById(R.id.ll_find_job);
        llPostJob = (LinearLayout) findViewById(R.id.ll_post_job);

        llFindJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intFindJob = new Intent(HomeActivity.this, FindJobActivity.class);
                startActivity(intFindJob);
            }
        });

        llPostJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intPostJob = new Intent(HomeActivity.this, PostJobActivity.class);
                startActivity(intPostJob);
            }
        });
    }
}
