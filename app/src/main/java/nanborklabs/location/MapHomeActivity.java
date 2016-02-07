package nanborklabs.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;
import static nanborklabs.location.MyLocationHandler.*;

public class MapHomeActivity extends AppCompatActivity implements OnMapReadyCallback, Mapupdate {

    //this is just  Formal code,changing class as features increasing
    //



//string tag
    public static String RUNTITME = "RUNNING";
    //string to sotore username,collge,bus route

    String uname, cname, rname;

    //shared prefs file name
    public static String PREFS = "MyPrefs";
    private GoogleMap gMap;
    private MapFragment mapFragment;
    LocationManager locationManager;
    Mapupdate mapupdate;
    GoogleApiClient googleApiClient;
    MyLocationHandler mLcoationHandler;


    public MapHomeActivity() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mLcoationHandler.ConnectApi();


    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {
        mLcoationHandler.disConnectApi();
        super.onStop();
    }

    public static void log(String message) {
        Log.i(RUNTITME, message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_home);
        readFromSharedprefs();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(cname);
        toolbar.setSubtitle(rname);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        mLcoationHandler = new MyLocationHandler(this, googleApiClient);


        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * a Function to Read Credentials from SharedPRrefrences
     *
     * @link String FIlename
     *
     */

    private void readFromSharedprefs() {
        SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        uname = sp.getString("User_name", "User Name");
        cname = sp.getString("College_name", "College name");
        rname = sp.getString("Route_id", "Route");


    }

    @Override
    protected void onResume() {
        super.onResume();
        setmapupdatelistner(mapupdate);

    }

    private void setmapupdatelistner(Mapupdate mapupdate) {
        mLcoationHandler.setMapupdate(mapupdate);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        LatLng pos = new LatLng(11.0104033, 76.9499028);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14));
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
        gMap.setMyLocationEnabled(true);


    }

    @Override
    public void updateMap(Location location) {

        gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("your Position"));

    }

    @Override
    public void updateMap() {
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gMap.setMyLocationEnabled(true);
    }





}
