package com.example.mapper.model;

import com.example.mapper.BaseActivity;

import java.util.List;
import java.util.Locale;

/**
 * This class is a blue print for the Event object and implements the Comparable.
 * The requestCode for each event is assigned once the event has been added to the database.
 * @author Rhane Mercado
 */
public class Event implements Comparable<Event>{

    private String dayString;
    private String id;
    private int dayInt;
    private int startHour;
    private int startMinute;
    private int interval;
    private int requestCode;
    private TravelMethod travelMethod;
    private int startID;
    private int endID;

    /**
     * Create a new Event object
     * @param dayName The day of the event eg Monday
     * @param day The int for that day
     * @param startHour The start hour for the event
     * @param startMinute The minute for the event
     * @param startID The id of the start address
     * @param endID The id of the end address
     * @param travelMethod The travel method for the event
     * @param interval The length for the commute
     */
    public Event(String dayName, int day, int startHour, int startMinute, int startID, int endID, TravelMethod travelMethod, int interval) {
        this.dayString = dayName;
        this.dayInt = day;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.interval = interval;
        this.startID = startID;
        this.endID = endID;
        this.travelMethod = travelMethod;
        id = dayName + startHour + startMinute;
    }

    /**
     * This method returns the start time of each event on a new line
     * @return String
     */
    @Override
    public String toString() {
        String time = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);
        return "Start\nat\n" + time;
    }

    /**
     * This method returns the day, hour, minute, start and end addresses, length of commute and travel
     * method.
     * @return String
     */
    public String viewEvent(){
        String start = "";
        String end = "";
        List<Address> addressList = BaseActivity.CD.getAddresses();
        for(int i = 0; i < addressList.size(); i++){
            if(startID == addressList.get(i).getId()){
                start = addressList.get(i).getAddressName();
            } else if(endID == addressList.get(i).getId()){
                end = addressList.get(i).getAddressName();
            }
        }
        return dayString + " at " + startHour + ":" + startMinute + "\n"
                + "From " + start + " to " + end + "\n"
                + "Length of Commute: " + interval + " minutes" + "\n"
                + "Travel Method: " + travelMethod;
    }

    /**
     * This method compares the dayInt and the start hour.
     * If the dayInt is the same it will compare the start hour.
     * @param e Event that will be compared with this
     * @return int
     */
    @Override
    public int compareTo(Event e) {
        int result = e.dayInt - this.dayInt;
        if(result == 0){
            return Integer.valueOf(startHour).compareTo(e.startHour);
        } else {
            return result;
        }
    }

    //////////////////////////////////////////////
    //Getters and setters
    /////////////////////////////////////////////

    public int getDayInt() {
        return dayInt;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public String getId() {
        return id;
    }

    public String getDayString() {
        return dayString;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public TravelMethod getTravelMethod() {
        return travelMethod;
    }

    public int getStartID() {
        return startID;
    }

    public int getEndID() {
        return endID;
    }

    public int getInterval() {
        return interval;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setStartID(int startID) {
        this.startID = startID;
    }

    public void setEndID(int endID) {
        this.endID = endID;
    }
}
