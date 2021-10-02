package com.example.a4sureweather.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.a4sureweather.R;
import com.example.a4sureweather.ui.weather.DaysWeatherDetailsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment  implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private String TAG = "HomeFragment";

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private boolean isLocationUpdated = false;
    private HomeViewModel homeViewModel;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    protected LocationManager locationManager;
    private LocationCallback mLocationCallback;
    private GoogleMap gMap;
    public static double latitude, longitude;
    private Marker marker;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        init();

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        supportMapFragment.getMapAsync(this);

        checkLocationUpdate();
        return root;
    }


    private void init() {
        //Allows me to work with OpenAPI weather without any security issues or being blocked
       /* StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

    }


    private void checkLocationUpdate() {
        try {
            assert locationManager != null;
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        //startLocationUpdates here to get regular updates on the map
        if (!gps_enabled && !network_enabled) {
            Toast.makeText(getContext(), "Please enable the gps", Toast.LENGTH_LONG).show();
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        gMap = googleMap;
        if (gMap != null) {
            getLastLocation();
            setupGoogleMapScreenSettings(googleMap);
            startLocationUpdates();
            gMap.setOnMarkerClickListener(this);
        }

    }

    public void getLastLocation() {

        FusedLocationProviderClient locationClient = mFusedLocationClient;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        locationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        onLocationChanged(location);
                        changeMap(location);
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }


    private void changeMap(Location location) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        // check if map is created successfully or not
        if (gMap != null) {

            if (marker != null) marker.remove();

            LatLng latLong;
            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            Log.d(TAG, "LAT: "+latitude+" LONG: "+longitude);

            String streetName = getUserAddress(latitude, longitude);
            //saving this address for history logic later
            saveStreetName(streetName);

            marker = gMap.addMarker(new MarkerOptions().position(latLong).
                    title("Your Location").snippet(streetName));
            assert marker != null;
            marker.showInfoWindow();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong)
                    .zoom(11)
                    .build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            gMap.setMyLocationEnabled(true);

            setupGoogleMapScreenSettings(gMap);


        } else {
            Toast.makeText(getContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    //to save the address under history later on
    private void saveStreetName(String streetName) {
    }


    public String getUserAddress(double lat, double longi) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, longi, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            return "No address name found.";
        }
    }

    protected void startLocationUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                onLocationChanged(locationResult.getLastLocation());
                changeMap(locationResult.getLastLocation());

            }
        };
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        /*mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

    }


    @Override
    public boolean onMarkerClick(@NonNull Marker googleMaker) {
        Log.d(TAG,"Address clicked: "+googleMaker.getTitle());

        Intent intent = new Intent(getContext(), DaysWeatherDetailsActivity.class);
        //to pass my address here
        startActivity(intent);
        //go to more days weather activity

        return false;
    }
}