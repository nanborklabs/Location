package nanborklabs.testmodule;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

/**
 * Created by nandhu on 1/2/16.
 */
public class MyLocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context mContext;
    public boolean Google_ApiOnline;
    public static String TAG = "LocationHandler";
    GoogleApiClient mclient;
    LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTEVAL = 3000;
    private static int DISPLACEMENT = 5;
    static final int REQUEST_CHECK_SETTINGS = 0x1;
    LocationListener mLocationListener;
    LocationManager mLocationManager;
    public boolean isGPSEnabled;
    public boolean canGetLocation;
    Location mCurrentLcoation;
    public boolean GPSon;
    LocationSettingsRequest mLocationSettingsRequest;
    private boolean locationObtained;

    public boolean isLocationObtained() {
        return locationObtained;
    }

    public GoogleApiClient getMclient() {
        return mclient;
    }


    public boolean isGPSon() {
        return GPSon;
    }

    public MyLocationHandler(Context context) {
        //constructor to create client, listner,LOcation request
        log("Constructor Location Handler");
        this.mContext = context;
        buildGooglepApi();//Google API
        createLocationListner();//location listener, implements Location changed callback
        create_Location_request();
    }

    private void createLocationListner() {

        //GMS location, not android.location
        log("creating listner method");
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }
        };


    }

    private void create_Location_request() {
        //create lcoation request with update interval ,highpriority,interval to update
        log("creating request method");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(FASTEST_INTEVAL)
                .setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * this method calls Fused API and requests periodic location updates
     *
     */

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            log("permission denied in startlocation updates");
            return;
        }

        log("mClient : " + mclient.isConnected() + " GPS: " + GPSon);


        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mclient, mLocationRequest, mLocationListener).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    log("requesting location updates fused provider " + status.getStatus().toString());
                }
            });
            log("fusedlocation requested,implents onlocation changed");
        } catch (Exception e) {
            log("Fused failed to update,GPS IF OFF Exception " + e.getLocalizedMessage());
        }
    }

    public static void log(String log) {

        Log.d(TAG, log);

    }


    public void disconnectApi() {
        mclient.disconnect();
    }


    public void connectGAPI() {
        mclient.connect();
    }

    private void buildGooglepApi() {

        //Build Google API with lcoation services API

        mclient = new GoogleApiClient.Builder(mContext).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                build();

        log("APi built");

    }


    public LocationRequest getmLocationRequest() {
        return mLocationRequest;
    }

    @Override
    public void onConnected(Bundle bundle) {
        log("onConnected G-API");

        Google_ApiOnline = true;
        if (this.isGPSon()) {
            startLocationUpdates();

            try {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location mloc = LocationServices.FusedLocationApi.getLastLocation(mclient);
                if (mloc != null) {
                    mCurrentLcoation = mloc;


                } else {
                    mCurrentLcoation = null;
                    getLastKownLocationFromGPS();
                }
            } catch (Exception e) {
                log("exception Occured");
            }

        } else {
            startLocationUpdates();
            log("GPSon variable IS flase");
        }
        //start location updates


    }

    protected void getLastKownLocationFromGPS() {
        if (isGPSon()){
         if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
            }
             Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

              if (location != null) {
                mCurrentLcoation=location;
                  log("got location fuck yeah");
                canGetLocation=true;
                locationObtained=true;
             }
            else{
                canGetLocation=false;
                Toast.makeText(mContext, "NO location-GPS", Toast.LENGTH_SHORT).show();
                mCurrentLcoation=null;
            }
        }
        else{
            Toast.makeText(mContext, "LOC IS UNavaile",Toast.LENGTH_SHORT).show();
        }

        }

    /**
     * Function to check whether GPS is ON/OFF from in built location provider
     * @return true/false
     */
    protected boolean checkGPS() {
        try {
            //Getting Location Manager
            mLocationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);
            //Getting a flag variale to check is GPS on OFF
            boolean flag = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (flag) {
                //GPS is on
                log("gps is on_LMMANAGER");
                GPSon = true;
                return true;
            } else {
                log("gps is off _LMMANAGER");
                GPSon = false;
                return false;
            }
        } catch (Exception e) {

            log("LOCATION MANAGER ERROR");
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void onConnectionSuspended(int i) {
        log("Connected suspended G-API");

        mclient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        log("Connected Failed G-API");

        this.Google_ApiOnline = false;
        Toast.makeText(mContext,"Google Play services Error",Toast.LENGTH_SHORT).show();

    }

    /**
     * boolean function to set GPS boolean variable ON/OFF
     *
     *
     * @param GPSon
     */

    public void setGPSon(boolean GPSon) {
        this.GPSon = GPSon;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.mCurrentLcoation = location;
            log("FRom fused location  changed _location Listner");

        }


    }

    /**
     * get Current Location
     * @return
     */

    public Location getmCurrentLcoation() {

        if (mCurrentLcoation!= null){
            return  mCurrentLcoation;
        }
        else{
            log("trying passive");
            Location mloc;

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }

            mclient.connect();
            try{
                mloc=LocationServices.FusedLocationApi.getLastLocation(mclient);
                if(mloc != null){
                    log("location obtianed from fused");
                    return mloc;
                }
                else{
                    log("location null from fused");
                    Toast.makeText(mContext,"Location not obtained",Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e){
                log(" Exception" +e.getLocalizedMessage());

            }
        }


        return null;
    }



    public boolean isGoogle_ApiOnline() {
        return Google_ApiOnline;
    }


    public boolean canGetLocation() {
        if (mCurrentLcoation!=null){
            return  true;

        }
        else {
            return false;
        }

    }
}




