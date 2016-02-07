package nanborklabs.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.util.Map;

/**
 * Created by nandhu on 27/1/16.
 */
public class LegacyLocation {

     LocationManager mlocaLocationManager;
    Context mcontext;
    android.location.LocationListener mListener;

    public LegacyLocation(Context context) {
        MapHomeActivity.log("swithcing TO LegacyLocation");
        this.mcontext=context;


        getlocation();
    }

    public void getlocation() {
        String LocationProvider = LocationManager.GPS_PROVIDER;
        mlocaLocationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);
        if(mlocaLocationManager==null){
            MapHomeActivity.log("Location Mamnager Null");
        }
        MapHomeActivity.log("NEtwork PROvider : "+LocationProvider);

        mListener=new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                MapHomeActivity.log("location changed of legacy location");

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        MapHomeActivity.log("checkig permision");
        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            MapHomeActivity.log("permission not granted for legacy location");




        }

        MapHomeActivity.log("permission granted for legacy location");
        mlocaLocationManager.requestLocationUpdates(LocationProvider, 0, 0, mListener);
        MapHomeActivity.log("Location request method call done ");
        Location location=mlocaLocationManager.getLastKnownLocation(LocationProvider);
        if(location!=null){
            MapHomeActivity.log("Location obtained");
        }
        else{
            MapHomeActivity.log("get lst know location is also  is null");
        }


        }

    }


