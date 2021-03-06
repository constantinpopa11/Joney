package it.unitn.disi.joney;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MyJobsActivity extends AppCompatActivity implements PostedJobsFragment.OnFragmentInteractionListener,
    CompletedJobsFragment.OnFragmentInteractionListener, PendingJobsFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jobs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(getApplicationContext(), drawer));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final MyJobsPagerAdapter myPagerAdapter = new MyJobsPagerAdapter(getSupportFragmentManager());
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setAdapter(myPagerAdapter);
                    }
                });
            }
        }).start();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_my_jobs);
        tabLayout.setupWithViewPager(viewPager);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
