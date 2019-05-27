package it.unitn.disi.joney;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyJobsPagerAdapter extends FragmentStatePagerAdapter {
    public MyJobsPagerAdapter(FragmentManager fm){
        super(fm);
    }
    @Override    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PostedJobsFragment();
            case 1:
                return new PendingJobsFragment();
            case 2:
                return new CompletedJobsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "POSTED";
            case 1:
                return "PENDING";
            case 2:
                return "COMPLETED";
            default:
                return null;
        }
    }
}
