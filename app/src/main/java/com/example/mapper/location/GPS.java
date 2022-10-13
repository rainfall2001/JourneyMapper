package com.example.mapper.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.mapper.activity.MapsActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * The GPS class is a Singleton as it will be used by multiple different classes. The class is responsible for getting the
 * current location of the device and checking whether the necessary location services have been turned on.
 * @author Rhane Mercado
 */
public class GPS {

    private double latitude, longitude;
    private int interval = 60000;
    private int intervalFast = 45000;
    private Commute commute;
    private static GPS gps = new GPS();

    private LocationRequest locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(interval)
            .setFastestInterval(intervalFast);

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLocations().size() > 0) {
                //get the current location
                int index = locationResult.getLocations().size() - 1;
                latitude = locationResult.getLocations().get(index).getLatitude();
                longitude = locationResult.getLocations().get(index).getLongitude();

                //record the commute
                commute.startCommute(latitude, longitude);

                if(MapsActivity.getMapReady()){
                    //map is ready to add the current location of the user
                    MapsActivity.setCurrentLocation();
                }
            }
        }
    };

    private GPS(){}

    /**
     * The method gets the one instance of the class
     * @return GPS object
     */
    public static GPS getInstance(){
        return gps;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * This method changes the interval for retrieving the coordinates depending what the
     * travel method is for the commute.
     */
    private void changeInterval(){
        interval = Commute.getTravelMethod().getInterval();
        intervalFast = Commute.getTravelMethod().getFastInterval();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(interval)
                .setFastestInterval(intervalFast);
    }

    /**
     * The method checks if the GPS is enabled as this is needed for location updates.
     * @param context The context of the class
     * @return The state of the GPS, boolean
     */
    public boolean isGPSEnabled(Context context){
        //get the location service of the phone
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //return if the GPS is either on or off
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    /**
     * The methods calls the callback function of location. This will enabled the application to
     * continuously get the users current location based on the specification of the
     * location request.
     * @param context The context of the class
     * @param commute Commute object
     */
    public void getCurrentLocation(Context context, Commute commute){
        //set the commute object
        this.commute = commute;
        //change the interval to the current travel method
        changeInterval();

        //check the validity of the Location Service of the device
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //as everything required is ok/enabled, proceed to finding the user's current location
            LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    /**
     * This method stops the location callback by removing the location updates. The users current location will
     * no longer be tracked.
     * @param context The context of the class
     */
    public void stopCurrentLocation(Context context){
        //check the validity of the Location Service of the device
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //remove the tracking
            LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback);
            commute = null;
            //reset all the commute
            Commute.resetOneOffTrip();
        }
    }


    /**
     * https://github.com/Pritish-git/get-Current-Location/blob/main/MainActivity.java
     * https://developer.android.com/training/location/change-location-settings
     *
     * This method prompts the user to enable the GPS on their device if it is not turned on.
     * @param activity The Activity of the class
     */
    public void turnOnGPS(Activity activity){
        //Log.d(tag, "turnOnGPS 1");
        //configure the location request which will be used as the request for the builder
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        //check that the location setting is the same as the one created in createLocationRequest()
        //once the task has been executed, the location settings can be checked by looking at the status code from the LocationSettingResponse object
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>(){
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task){
                //if the GPS is not enabled, the task will throw an exception
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                }catch (ApiException e){
                    switch (e.getStatusCode()){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try{
                                //enable GPS
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(activity,2);
                                //Log.d(tag, "hello world");
                            } catch (IntentSender.SendIntentException ex){
                                ex.printStackTrace();
                            }break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //cannot change the settings
                            break;
                    }
                }
            }
        });
    }
}