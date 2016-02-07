package nanborklabs.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by nandhu on 26/1/16.
 */
public class MyLocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private Context mContext;
    LocationManager mLocationManager;
    android.location.LocationListener locationListener;
    public Mapupdate mapupdate;
    static String providerName = LocationManager.GPS_PROVIDER;
   public Boolean GPSEnabled;

    public void setMapupdate(Mapupdate mapupdate) {
        this.mapupdate = mapupdate;
    }

    public MyLocationHandler(Context context, GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
        connect_G_Api_client(context);
        this.mContext = context;

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.GPSEnabled=false;
        MapHomeActivity.log(" control in mLocationHAndler");



    }


    private void getlocation() {

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






        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 50, locationListener);
        Location location=mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location!=null){
            MapHomeActivity.log("received from Req updates Not null");
            if (mapupdate!=null){
                MapHomeActivity.log("Mapupdate  is  fuckin null");
                mapupdate.updateMap(location);
            }

        }
        else{
            MapHomeActivity.log(" null from req updates,passing tp setEnabled");
            if(mapupdate!=null){
                mapupdate.updateMap();

            }
            else{
                MapHomeActivity.log("Map update in null");
            }



        }


    }

    private void setupListener() {


        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mapupdate.updateMap(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {


            }

            @Override
            public void onProviderEnabled(String provider) {

                MapHomeActivity.log("provider Enabled");
                GPSEnabled=true;



            }

            @Override
            public void onProviderDisabled(String provider) {
                MapHomeActivity.log("Providers disabled");
                showGPSpage();
                GPSEnabled=true;
            }
        };


    }

    private void showGPSpage() {
        mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }










    protected void ConnectApi() {
        googleApiClient.connect();
    }

    protected void disConnectApi() {
        googleApiClient.disconnect();
    }


    @Override
    public void onConnected(Bundle bundle) {

        MapHomeActivity.log("on connected method of google API");
        if (GPSEnabled) {
            getlocation();
        }
        else {
            MapHomeActivity.log("GPS Not enabled");
        }

    }



    @Override
    public void onConnectionSuspended(int i) {
        MapHomeActivity.log("APi Client suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


        MapHomeActivity.log("hear that connection is failed");

    }



    private void connect_G_Api_client(Context context) {

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        MapHomeActivity.log("Google Api Client has been build");
    }

    public interface Mapupdate{
        void updateMap(Location location);
        void updateMap();
    }


}
