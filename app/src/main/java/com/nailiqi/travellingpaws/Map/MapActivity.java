package com.nailiqi.travellingpaws.Map;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nailiqi.travellingpaws.R;
import com.nailiqi.travellingpaws.Utils.BottomNavbarHelper;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: showing map now");
        mMap = googleMap;
        Toast.makeText(this, "Showing Where You Are Now!", Toast.LENGTH_SHORT).show();

        if(mLocationPermissionGranted) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            setupWidgets();

            //check intent
            Intent intent = getIntent();
            if(intent.hasExtra("photo_latitude")){

                Bundle bundle = intent.getExtras();

                if(bundle!=null)
                {
                    double photo_latitude =(double) bundle.get("photo_latitude");
                    double photo_longitude =(double) bundle.get("photo_longitude");

                    //move camera
                    moveCamera(new LatLng(photo_latitude, photo_longitude), DEFAULT_ZOOM, "This is where the photo is taken!");
                    Toast.makeText(this, "This is where the photo is taken!", Toast.LENGTH_SHORT).show();

                }
            } else {
                getDeviceLocation();
            }

        }
    }

    private static final String TAG = "MapActivity";
    private static final int ACTIVITY_NUM = 1;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private static final float DEFAULT_ZOOM = 15f;
    private static final double DEFAULT_LAT = 42.349634;
    private static final double DEFAULT_LNG = -71.099688;
    private Context mContext = MapActivity.this;

    //permissions
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;

    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationProviderClient;

    private EditText mSearchText;
    private ImageView mGpsIcon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "onCreate: starting");
        mSearchText = (EditText) findViewById(R.id.et_search);
        mGpsIcon = (ImageView) findViewById(R.id.ic_gps);

        setupBottomNavbar();

        if(checkServices()){
            getLocationPermission();
        }
    }

    /**
     * set up widgets
     */
    private void setupWidgets(){
        Log.d(TAG, "setupWidgets: ");

        //overwrite enter click
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //search location
                    searchLocation();
                }

                return false;
            }
        });

        //setup gps icon to return to current location
        mGpsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: click gps icon");
                getDeviceLocation();
            }
        });

        //hide keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * get location based on search
     */
    private void searchLocation(){
        Log.d(TAG, "searchLocation: ");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(mContext);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "searchLocation: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "searchLocation: location is: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    /**
     * get device location
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting current location");

        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted) {
                Log.d(TAG, "getDeviceLocation: permission granted");
                Task location = mLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: current location found");

                            Log.d(TAG, "onComplete: task result is: " + task.getResult());
                            Location curLocation = (Location) task.getResult();
                            if(curLocation == null){
                                //set default gps for emulator
                                moveCamera(new LatLng(DEFAULT_LAT, DEFAULT_LNG), DEFAULT_ZOOM, "My Location");
                            } else {
                                moveCamera(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                            }


                        } else {
                            Log.d(TAG, "onComplete: current location not found");
                            Toast.makeText(mContext, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }


        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: Security Exception: " + e.getMessage() );
        }
    }

    /**
     * move camera
     */
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving camera to lat: " + latLng.latitude + " lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //drop marker
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_paw_blue));

        mMap.addMarker(options);

        //hide keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * initialize map
     */
    private void initMap() {
        Log.d(TAG, "initMap: init map now");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }


    /**
     * check location permissions
     */
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: permission granted");
                mLocationPermissionGranted = true;
                //init google map
                initMap();

            } else {
                //request permissions
                ActivityCompat.requestPermissions( this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            //request permissions
            ActivityCompat.requestPermissions( this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: request permission");
        mLocationPermissionGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE : {
                if(grantResults.length > 0 ) {
                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;

                    //init google map
                    initMap();
                }
            }
        }
    }

    /**
     * check google services version
     */
    public boolean checkServices(){
        Log.d(TAG, "checkServices: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "checkServices: services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "checkServices: error occurred");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Unable to make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * get imcoming Intent
     */
    private void getIncomingIntent(){
        Intent intent = getIntent();
        if(intent.hasExtra("photo_latitude")){

            Bundle bundle = intent.getExtras();

            if(bundle!=null)
            {
                double photo_latitude =(double) bundle.get("photo_latitude");
                double photo_longitude =(double) bundle.get("photo_longitude");

                //move camera
                moveCamera(new LatLng(photo_latitude, photo_longitude), DEFAULT_ZOOM, "This is where the photo is taken!");

            }
        }
    }

    /**
     * Bottom navbar setup
     */
    private void setupBottomNavbar() {
        Log.d(TAG, "setupBottomNavbar: Setting up Bottom navigation bar");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavbar);
        //helper function to set up nav bar
        BottomNavbarHelper.setupBottomNavbar(bottomNavigationViewEx);
        //helper function to enable navigation
        BottomNavbarHelper.enableNavigation(mContext, bottomNavigationViewEx );

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


}
