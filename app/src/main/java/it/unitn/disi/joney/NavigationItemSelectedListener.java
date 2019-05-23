package it.unitn.disi.joney;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

public class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener  {

    DrawerLayout drawer;

    public NavigationItemSelectedListener(DrawerLayout drawer) {
        this.drawer = drawer;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the my jobs action
        } else if (id == R.id.nav_profile) {
            // Handle the my jobs action
        } else if (id == R.id.nav_my_jobs) {
            // Handle the my jobs action
        } else if (id == R.id.nav_find_jobs) {

        } else if (id == R.id.nav_post_job) {

        } else if (id == R.id.nav_send_ticket) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
