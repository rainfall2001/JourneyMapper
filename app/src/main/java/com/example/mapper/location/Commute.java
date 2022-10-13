package com.example.mapper.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.example.mapper.activity.MapsActivity;
import com.example.mapper.model.Address;
import com.example.mapper.model.CommuteDatabase;
import com.example.mapper.model.Coordinate;
import com.example.mapper.model.TravelMethod;
import com.example.mapper.model.Trip;
import com.example.mapper.model.Uploaded;

/**
 * This class is responsible for creating a valid trip. It will create a trip once the user has left their start location.
 * It will continue to collect data if the user is not at their start location and not at their end location. When the user
 * has reached their end location it will stop recording and stop the service.
 * @author Rhane Mercado
 */
public class Commute {

    protected CommuteDatabase commuteDatabase;
    protected Context context;
    protected Trip trip;
    protected static Address startAddress;
    protected static Address endAddress;
    protected static TravelMethod travelMethod;
    protected boolean insideFinish;

    public Commute(Context context){
        this.context = context;
        commuteDatabase = new CommuteDatabase(context);
    }

    public static Address getStartAddress() {
        return startAddress;
    }

    public static Address getEndAddress() {
        return endAddress;
    }

    public static TravelMethod getTravelMethod(){
        return travelMethod;
    }

    /**
     * This method starts a one off commute. It will show to the user their current location
     * on a map.
     * @param lat Latitude of the current location
     * @param lon Longitude of the current location
     */
    public void startCommute(double lat, double lon){
        //create an intent if the MapsActivity has not yet started
        if(!MapsActivity.getMapReady()){
            Intent intent = new Intent(context, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        //check if the user has left their start location
        boolean insideStart = checkArea(startAddress.getLatitude(), startAddress.getLongitude(), lat, lon);
        //check the commute
        checkLocation(lat, lon, insideStart);
    }

    /**
     * This method checks if the commute has started or currently going or has finished.
     * @param lat The user's current latitude
     * @param lon The user's current longitude
     * @param insideStart boolean value if the user has left their starting location
     */
    protected void checkLocation(double lat, double lon, boolean insideStart){
        //get status: if they are at their end location
        insideFinish = checkArea(endAddress.getLatitude(), endAddress.getLongitude(), lat, lon);

        //check if the user has left their start location
        if((!insideStart) && trip == null){
            //no longer in the starting area, create a new Trip
            trip = new Trip(startAddress.getAddressName(), endAddress.getAddressName(), travelMethod, Uploaded.FALSE, Uploaded.Allow);
            commuteDatabase.addTrip(trip);
            addCoordinate(lat, lon);
        } else if((!insideStart) && (!insideFinish) && trip != null){
            //trip is currently ongoing, add the coordinates
            addCoordinate(lat, lon);
        } else if(insideFinish && trip != null){
            //the user has reached their end location
            addCoordinate(lat, lon);
            //stop tracking
            stopService();
        }
    }

    /**
     * This method checks if one point is inside another points 150 metre radius.
     * @param centreLat Latitude destination
     * @param centreLon Longitude destination
     * @param testLat Latitude of current location
     * @param testLon Longitude of current location
     * @return boolean if point is within radius
     */
    protected boolean checkArea(double centreLat, double centreLon, double testLat, double testLon){
        float[] results = new float[1];
        Location.distanceBetween(centreLat, centreLon, testLat, testLon, results);
        return results[0] < 150;
    }

    /**
     * This method creates a new coordinate object and adds it to the database.
     * @param lat Latitude
     * @param lon Longitude
     */
    protected void addCoordinate(double lat, double lon){
        Coordinate c = new Coordinate(lat, lon, trip);
        commuteDatabase.addCoordinates(c);
    }

    /**
     * This methods stops the tracking and closes the map shown to the user
     */
    protected void stopService(){
        Intent stopServiceIntent = new Intent(context, LocationUpdateService.class);
        context.stopService(stopServiceIntent);
        if(MapsActivity.getActivityStatus() != null){
            MapsActivity.backHome();
        }
    }

    /**
     * Set all the needed variables for the one off commute
     * @param s Start address
     * @param e End address
     * @param tM Travel method of that commute
     */
    public static void setOneOffTrip(Address s, Address e, TravelMethod tM){
        startAddress = s;
        endAddress = e;
        travelMethod = tM;
    }

    /**
     * Resets all the variables for the one off commute
     */
    public static void resetOneOffTrip(){
        startAddress = null;
        endAddress = null;
        travelMethod = null;
    }
}
