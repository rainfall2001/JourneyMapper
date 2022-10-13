package com.example.mapper.activity;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.location.GPS;
import com.example.mapper.location.LocationUpdateService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mapper.databinding.ActivityMapsBinding;

/**
 * This calls controls the functionality of the activity_maps layout. A map of the user's current area
 * is shown and a red point of the user's current location is pinned on the map. It will continue to change
 * if the user changes their current location.
 * @author Rhane Mercado
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static Marker currentMarker;
    private static boolean mapReady = false;
    private static Activity mapActivity;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapActivity = this;
        context = this;
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

        LatLng currentLocation = new LatLng(GPS.getInstance().getLatitude(), GPS.getInstance().getLongitude());
        currentMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        //the map is now ready
        mapReady = true;
    }

    /**
     * onClick method to allow the user to stop tracking
     * @param view Button
     */
    public void stopTracking(View view){
        //create a new intent for the service running and stop the foreground service
        Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        stopService(serviceIntent);
        //take the user back to the home page
        backHome();

    }

    /**
     * This method gets the current location of the user and moves the marker to that current latitude and longitude
     */
    public static void setCurrentLocation(){
        LatLng currentLocation = new LatLng(GPS.getInstance().getLatitude(), GPS.getInstance().getLongitude());
        currentMarker.setPosition(currentLocation);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    /**
     * This method stops the maps activity and starts an activity to go back to the home page.
     */
    public static void backHome(){
        //create a new intent to go back to the home page
        Intent baseIntent = new Intent(context, BaseActivity.class);
        mapReady = false;
        context.startActivity(baseIntent);
        mapActivity.finish();
    }


    //////////////////////////////////////////////
    //Getters
    /////////////////////////////////////////////

    public static Activity getActivityStatus(){
        return mapActivity;
    }

    public static boolean getMapReady(){
        return mapReady && mapActivity != null;
    }
}