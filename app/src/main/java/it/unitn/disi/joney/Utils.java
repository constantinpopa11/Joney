package it.unitn.disi.joney;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Utils {
    //get distance in km
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(lat1 * Math.PI / 180.0)
                * Math.sin(lat2 * Math.PI / 180.0)
                + Math.cos(lat1 * Math.PI / 180.0)
                * Math.cos(lat2 * Math.PI / 180.0)
                * Math.cos(theta * Math.PI / 180.0);
        dist = Math.acos(dist);
        dist = dist * 180.0 / Math.PI;
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = false;

        try {
            enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        return enabled;
    }

    public static boolean isLocationPermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    public static boolean isStoragePermissionGranted(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    public static Pair<Double, Double> getLocation(Context context) {
        Double lat, lon;

        if (Utils.isLocationPermissionGranted(context)) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            Log.d("gpsUtils","ok");

            if (location != null) {
                // now get the lat/lon from the location and do something with it.
                lat = location.getLatitude();
                lon = location.getLongitude();
                //Log.d("LATLON",lat.toString() + " " + lon.toString());
                return new Pair<Double, Double>(lat, lon);
            }
        }
        return null;
    }

    public static String getStreetName(double lat, double lon, Context c)
    {
        Geocoder geocoder = new Geocoder(c, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                }
                /*strReturnedAddress.append(returnedAddress.getLocality()).append(", ");
                strReturnedAddress.append(returnedAddress.getAdminArea()).append(", ");
                strReturnedAddress.append(returnedAddress.getCountryName());*/
                return strReturnedAddress.toString();
            }
            else {
                return "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Cannot get Address!";
        }
    }

    public static Pair<Double,Double> getCoordinates(String address, Context c)
    {
        Geocoder geocoder = new Geocoder(c,Locale.getDefault());
        Log.d("Street",address);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
            Log.d("Size",String.valueOf(addresses.size()));
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                Log.d("Lat",String.valueOf(latitude));
                Log.d("Lon",String.valueOf(longitude));
                return new Pair<Double, Double>(latitude, longitude);
            } else
                return new Pair<Double, Double>(0.0, 0.0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Pair<Double, Double>(0.0, 0.0);
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(1.5 * targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(1.5 * initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void showGPSDisabledAlert(final Context context){
        new AlertDialog.Builder(context)
            .setMessage("GPS needs to be enabled in order to use full location features.")
            .setPositiveButton("Don't show again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(Constants.PREF_GPS_ALERT, Constants.GPS_ALERT_HIDE).commit();
                }
            })
            .setNegativeButton("Dismiss", null)
            .show();
    }

    public static NotificationCompat.Builder getNotification(Context context,Intent intent, String channelId, String title, String text)
    {
        Intent newIntent = intent;
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.logo_joney_grey)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);;

        return builder;
    }

    public static void createNotificationChannel(String channelName, String channelDescription, String channelId, Object systemService) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            String description = channelDescription;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) systemService;// pass this getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static float getUserAverageRating(Context context, int userId){
        DatabaseHandler db = new DatabaseHandler(context);
        List<Feedback> feedbacks = db.getUserFeedbacks(userId);

        float sum = 0.0f;
        for(Feedback f:feedbacks)
        {
            sum += f.getRating();
        }

        return sum/(float)feedbacks.size();
    }
}
