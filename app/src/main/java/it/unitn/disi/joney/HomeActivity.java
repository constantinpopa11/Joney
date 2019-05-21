package it.unitn.disi.joney;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TextView tvFindJob, tvPostJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvFindJob = (TextView) findViewById(R.id.tv_find_job);
        tvPostJob = (TextView) findViewById(R.id.tv_post_job);

        tvFindJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intFindJob = new Intent(HomeActivity.this, FindJobActivity.class);
                startActivity(intFindJob);
            }
        });

        tvPostJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intPostJob = new Intent(HomeActivity.this, PostJobActivity.class);
                startActivity(intPostJob);
            }
        });
    }
}
