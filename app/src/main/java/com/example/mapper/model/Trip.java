package com.example.mapper.model;

import com.example.mapper.BaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is blue print for the Trip object.
 * The trip id is assigned once the trip is added to the database
 * @author Rhane Mercado
 */
public class Trip implements Comparable<Trip>{
    private String id;
    private String startLocation;
    private String endLocation;
    private TravelMethod travelMethod;
    private Uploaded uploaded;
    private Uploaded canUpload;
    private String time;
    private String timeDisplay;

    /**
     * Create a new Trip
     * @param startLocation The start location of the trip
     * @param endLocation The end location of the trip
     * @param travelMethod The travel method fo the trip
     * @param uploaded The status of the upload
     */
    public Trip(String startLocation, String endLocation, TravelMethod travelMethod, Uploaded uploaded, Uploaded canUpload) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.travelMethod = travelMethod;
        this.uploaded = uploaded;
        this.canUpload = canUpload;
    }

    /**
     * The method returns the start location and end location with the time the trip started
     * @return String
     */
    @Override
    public String toString() {
        return "ID: " + id + " " + startLocation + " to " + endLocation + " @ " + timeDisplay;
    }

    /**
     * The method returns the id, travel method, start and end location, time and the status
     * if the trip has been uploaded.
     * @return String
     */
    public String viewTrip(){
        return "From " + startLocation + " to " + endLocation +
                "\nAt " + timeDisplay +
                "\nMode: " + travelMethod.toString() +
                "\nUploaded: " + uploaded.toString() +
                "\nUpload to the server: " + canUpload.toString();
    }

    /**
     * This method compares the id of the trip. If the trip id of the passed in object is
     * greater, that trip will be ahead of this trip.
     * @param trip
     * @return
     */
    @Override
    public int compareTo(Trip trip) {
        return Integer.valueOf(trip.getId()).compareTo(Integer.valueOf(id));
    }

    /**
     * This method sets the time and formats the time to be displayed to the user.
     * @param time String of the time of the first Coordinate
     */
    public void setTime(String time) {
        this.time = time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //the format to display the time to the user
        SimpleDateFormat displayTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            //parse time to date
            Date d = dateFormat.parse(time);
            //format date to be displayed
            timeDisplay = displayTimeFormat.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////
    //Getters and setters
    /////////////////////////////////////////////

    public void setId(String id) {
        this.id = id;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setUploaded(Uploaded uploaded) {
        this.uploaded = uploaded;
    }

    public void setCanUpload(Uploaded canUpload) {
        this.canUpload = canUpload;
    }

    public String getId(){
        return id;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public TravelMethod getTravelMethod() {
        return travelMethod;
    }

    public String getTime() {
        return time;
    }

    public Uploaded getUploaded() {
        return uploaded;
    }

    public Uploaded getCanUpload() {
        return canUpload;
    }

}
