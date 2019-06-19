package it.unitn.disi.joney;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import java.util.List;
import java.util.prefs.Preferences;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;
    Style mapStyle;

    ImageView dropPinView;
    EditText etAddress;

    Context mContext;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;


        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map);

        Button btnChooseAddress = (Button) findViewById(R.id.btn_choose_address);
        FloatingActionButton btnLocateUser = (FloatingActionButton) findViewById(R.id.btn_locate_user);
        etAddress = (EditText) findViewById(R.id.et_address);
        etAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    etAddress.clearFocus();
                    InputMethodManager in = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(etAddress.getWindowToken(), 0);
                    //to avoid block of app
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Pair<Double,Double> latLon = Utils.getCoordinates(etAddress.getText().toString(),getApplicationContext());
                            etAddress.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(latLon.first == 0.0 && latLon.second == 0.0)
                                        Toast.makeText(getApplicationContext(),"Cannot find any street",Toast.LENGTH_SHORT).show();
                                    else
                                    {
                                        CameraPosition position = new CameraPosition.Builder()
                                                .target(new LatLng(latLon.first,latLon.second))
                                                .zoom(15) // Sets the zoom
                                                .build(); // Creates a CameraPosition from the builder

                                        mapboxMap.animateCamera(CameraUpdateFactory
                                                .newCameraPosition(position), 1500);
                                    }
                                }
                            });
                        }
                    }) .start();

                    return true;
                }
                return false;
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                MapActivity.this.mapboxMap = mapboxMap;
                mapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(41.890251,12.492373))
                        .zoom(5)
                        .build());
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                        mapStyle = style;
                        enableLocationComponent(style,false);
                    }
                });
            }
        });

        dropPinView = new ImageView(this);
        dropPinView.setImageResource(R.drawable.ic_pin);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        float density = getResources().getDisplayMetrics().density;
        params.bottomMargin = (int) (12 * density);
        dropPinView.setLayoutParams(params);
        mapView.addView(dropPinView);

        btnChooseAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;
                Toast.makeText(getApplicationContext(), "Lat: " + String.valueOf(mapTargetLatLng.getLatitude())
                        + "\n" + "Lon: " + String.valueOf(mapTargetLatLng.getLongitude()), Toast.LENGTH_LONG).show();

                Intent postJobIntent = new Intent();
                postJobIntent.putExtra("latitude", mapTargetLatLng.getLatitude());
                postJobIntent.putExtra("longitude", mapTargetLatLng.getLongitude());

                setResult(RESULT_OK, postJobIntent);
                finish();
            }
        });

        btnLocateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utils.isLocationPermissionGranted(mContext)) {
                    showMissingPermissionAlert("Location", "GPS");
                } else if(!Utils.isGPSEnabled(mContext)) {
                    int gpsAlertPref = PreferenceManager.getDefaultSharedPreferences(mContext).getInt(Constants.PREF_GPS_ALERT, Constants.GPS_ALERT_SHOW);
                    if(gpsAlertPref == Constants.GPS_ALERT_SHOW)
                        Utils.showGPSDisabledAlert(mContext);
                } else {
                    enableLocationComponent(mapStyle,true);
                }
            }
        });
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle,boolean locateMe) {
        // Check if permissions are enabled and if not request
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            showMissingPermissionAlert("Location","GPS");
        else {
            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            if(locateMe) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude()))
                        .zoom(15) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 1500);
            }

        }
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
}