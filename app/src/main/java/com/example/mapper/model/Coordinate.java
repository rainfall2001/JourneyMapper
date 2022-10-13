package com.example.mapper.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class is a blue print for the Coordinate object.
 * The latitude, longitude and the trip must be provided. The time of creation will be done after the coordinate has been
 * created.
 * @author Rhane Mercado
 */
public class Coordinate {
    private final Double latitude;
    private final Double longitude;
    private final String tripID;
    private String date;

    /**
     * Create a new Coordinate object.
     * @param latitude The latitude of the coordinate
     * @param longitude The longitude of the coordinate
     * @param t The trip associated with the coordinate
     */
    public Coordinate(Double latitude, Double longitude, Trip t) {
        this.latitude = latitude;
        this.longitude = longitude;
        tripID = t.getId();
        setDate();
    }

    public Coordinate(Double latitude, Double longitude, Trip t, String date){
        this.latitude = latitude;
        this.longitude = longitude;
        tripID = getTripId();
        this.date = date;
    }

    /**
     * A method which will get the current date and time that the coordinate was made.
     * It is a private method as the date and time should only be set once, when the coordinate was made.
     * Therefore it should not be modified outside the class.
     * The date of the coordinate is a String.
     */
    private void setDate(){
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        date = dateFormat.format(calender.getTime());
    }

    //////////////////////////////////////////////
    //Getters and setters
    /////////////////////////////////////////////

    public String getTripId() {
        return tripID;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getDate() {
        return date;
    }

    public Date getDateObject(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date d = dateFormat.parse(date);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
