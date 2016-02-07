package nanborklabs.testmodule;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<LocationSettingsResult>, LocationHandler, GpsStatus.Listener {

    private GoogleMap mMap;
    public Location mLastLocation;
    public static String TAG = "LocationHandler";
    MyLocationHandler mLocationHandler;
    static final int REQUEST_CHECK_SETTINGS = 0x1;
    LocationSettingsRequest settingsRequest;
    LocationHandler mHandler;
    BitmapDescriptor icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        log("COntrol in BASE");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //class for GPS handling

        mLocationHandler = new MyLocationHandler(getApplicationContext());
        // setListener();


        mapFragment.getMapAsync(this);

    }


    /** fires of a Pending Result TO be showd to user if GPS is oFF
     * this is called if GPS  provider is off,
     * called from @method ShowDialog
     * */


    private void check() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mLocationHandler.getMclient(),
                        settingsRequest
                );
        result.setResultCallback(this);
    }


    /**
     * Method to log/debug
     * @param log
     */
    public static void log(String log) {

        Log.d(TAG, log);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        log(" Map ready");
        if (mLocationHandler.isGPSon()) {
            log("GPS ON");
        }
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        log("on Resume");
        if (mLocationHandler.isGPSon()) {
            //GPS is on
            mLocationHandler.getLastKownLocationFromGPS();
        }

        //check whether GPS is on OFF
        //if no -error
        //else getcurrent location from GPS and Fused
        if (mLocationHandler.canGetLocation) {
            mLastLocation = mLocationHandler.getmCurrentLcoation();
            addBusinfo();
        }

    }

    private void addBusinfo() {

        //add marker for Bus
        if (mLocationHandler.isGPSon()) {
            mMap.addMarker(new MarkerOptions().title("Bus").snippet("Will reach within 5 mins").icon(icon).position(new LatLng(10.9371567, 76.949456)));
        }
    }


    /**
     * after locationhandler's object creation
     * Builds Location Create request
     *
     */

    private void showDialog() {
        if (mLocationHandler != null) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationHandler.getmLocationRequest());
            settingsRequest = builder.build();
            check();

        } else {
            log("location handler null");
        }


    }


    public void updateMap(Location mLastLocation) {

        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .title("You are Here"));


    }


    @Override
    protected void onStart() {
        super.onStart();
        //get BUS ICON
        icon = BitmapDescriptorFactory.fromResource(R.drawable.bus);


        //if GPS is ON
        if (mLocationHandler.checkGPS()) {
            mLocationHandler.setGPSon(true);
        } else {
            mLocationHandler.setGPSon(false);
            showDialog();

        }
        //Connect goolge Api
        mLocationHandler.connectGAPI();
        //check whether GPS is ON/OFF,set boolean


        log("on start");

    }

    @Override
    protected void onPause() {

        mLocationHandler.disconnectApi();
        super.onPause();

    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        log("User agreed to make required location settings changes.");
                        mLocationHandler.setGPSon(true);


                        break;
                    case Activity.RESULT_CANCELED:
                        log("User chose not to make required location settings changes.");
                        mLocationHandler.setGPSon(false);
                        break;
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        log("onlocation changed");
    }


    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                log("All location settings are satisfied.");

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                log("Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    log("PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                log("Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }

    }

    /**
     * Called to report changes in the GPS status.
     * The event number is one of:
     * <ul>
     * <li> {@link GpsStatus#GPS_EVENT_STARTED}
     * <li> {@link GpsStatus#GPS_EVENT_STOPPED}
     * <li> {@link GpsStatus#GPS_EVENT_FIRST_FIX}
     * <li> {@link GpsStatus#GPS_EVENT_SATELLITE_STATUS}
     * </ul>
     * <p/>
     * When this method is called, the client should call
     * {@link LocationManager#getGpsStatus} to get additional
     * status information.
     *
     * @param event event number for this notification
     */
    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                mLocationHandler.canGetLocation = true;
                log("First fix");
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                log("Satellite status");
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                mLocationHandler.setGPSon(true);
                log("GPS event started");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                mLocationHandler.setGPSon(false);
                log("GPs event stopped");
                break;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurretLocationfromFused();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mLocationHandler.connectGAPI();
    }

    private void getCurretLocationfromFused() {
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = FusedLocationApi.getLastLocation(mLocationHandler.getMclient());
            if (location != null) {
                //Location Obtained From fused,save it
                mLastLocation = location;
            } else {
                //location failed to update from fused


            }


        } catch (Exception e) {
            log(" Exception e:" + e.getLocalizedMessage());
        }
    }


}


