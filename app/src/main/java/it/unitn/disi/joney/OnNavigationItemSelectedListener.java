package it.unitn.disi.joney;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OnNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener  {

    DrawerLayout drawer;
    Context context;

    public OnNavigationItemSelectedListener(Context context, DrawerLayout drawer) {
        this.context = context;
        this.drawer = drawer;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intHome = new Intent(context, HomeActivity.class);
            intHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intHome);
        } else if (id == R.id.nav_profile) {
            Intent intUserProfile = new Intent(context, UserProfileActivity.class);
            intUserProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intUserProfile);
        } else if (id == R.id.nav_my_jobs) {
            // Handle the my jobs action
            Intent intMyJobs = new Intent(context, MyJobsActivity.class);
            intMyJobs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intMyJobs);
        } else if (id == R.id.nav_find_jobs) {
            Intent intFindJobs = new Intent(context, FindJobActivity.class);
            intFindJobs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intFindJobs);
        } else if (id == R.id.nav_post_job) {
            Intent intPostJob = new Intent(context, PostJobActivity.class);
            intPostJob.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intPostJob);
        } else if (id == R.id.nav_send_ticket) {
            Intent intSendTicket = new Intent(context, SendTicketActivity.class);
            intSendTicket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intSendTicket);
        } else if (id == R.id.nav_logout) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putBoolean(Constants.PREF_REMEMBER_ME, false).commit();
            prefs.edit().putInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN).commit();
            Intent intLogIn = new Intent(context, LoginActivity.class);
            intLogIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intLogIn);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
