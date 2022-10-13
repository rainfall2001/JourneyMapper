package com.example.mapper.location;

import android.content.Context;
import android.content.Intent;

import com.example.mapper.BaseActivity;
import com.example.mapper.helper.NotificationHelper;
import com.example.mapper.model.Address;
import com.example.mapper.model.CommuteDatabase;
import com.example.mapper.model.Event;
import com.example.mapper.model.Trip;
import com.example.mapper.model.Uploaded;

import java.util.Calendar;
import java.util.List;

/**
 * This class is responsible for creating commutes from the events the user has provided. This class inherits
 * from the Commute class. It will get the current event and start recording the trip. The trip will be recorded once the user
 * has left their start location and stop once the user has reached their end location. If the user did not start at their start location
 * but ended at their end location, the trip will be recorded. If the user started at their start location but did not end at their end
 * location the trip will also be record. Both these instances, a notification will be shown to the user, informing them
 * there is no valid address. All other variations of a trip will be deleted.
 * @author Rhane Mercado
 */
public class EventCommute extends Commute{

    private boolean started;
    private boolean startedAtStart;
    static private Event event;
    private static long interval;


    public EventCommute(Context context){
        super(context);
        started = false;
        startedAtStart = false;
    }

    public static long getInterval() {
        return interval;
    }

    /**
     * This method gets the current Event for the current day and time. It will set all the necessary variables.
     * @param context The context of the class
     * @return Event
     */
    public static Event getEvent(Context context, String data){
        CommuteDatabase c = new CommuteDatabase(context);
        List<Event> eventList = c.getEvents();
        //get the current time and day
        Calendar calendar = Calendar.getInstance();
        //loop through all the events in the database comparing which event is the valid one
        for(int i = 0; i < eventList.size(); i++){
            Event e = eventList.get(i);
            //check which intent triggered the event by checking the data
            if(e.getId().compareTo(data) == 0){
                event = e;
                travelMethod = event.getTravelMethod();
                startAddress = c.getAddress(event.getStartID());
                endAddress = c.getAddress(event.getEndID());
                interval = e.getInterval() * 60000;
                return event;
            }
        }
        return null;
    }

    //check the starting location
    //don't know what will happen if the user adds an address while tracking

    /**
     * This method checks the starting location of the user.
     * @param lat Latitude of the current location
     * @param lon Longitude of the current location
     */
    private void checkStart(double lat, double lon){
        if(checkArea(startAddress.getLatitude(), startAddress.getLongitude(), lat, lon)){
            //the first location is inside the start address
            //only start tracking once the user has left the start address
            startedAtStart = true;
        } else {
            //the user is not at their start location, create a new address with the current latitude and longitude of the current location
            startAddress = new Address("?", "?", lat, lon);
        }
        started = true;
    }

    /**
     * This method starts an event commute.
     * @param lat Latitude of the current location
     * @param lon Longitude of the current location
     */
    @Override
    public void startCommute(double lat, double lon) {
        boolean insideStart = false;
        if(!started){
            //get the event
            checkStart(lat, lon);
        }
        if(startedAtStart){
            //check if the current location is inside the starting area
            insideStart = checkArea(startAddress.getLatitude(), startAddress.getLongitude(), lat, lon);
        }
        checkLocation(lat, lon, insideStart);
    }

    /**
     * This method checks if the current trip is valid. If it is, it will be recorded. If it is not
     * the trip will be deleted.
     */
    private void checkTrip(){
        //create a new intent that will be used for the notification
        Intent updateIntent = new Intent(context, BaseActivity.class);

        //delete the trip as the user did not start at their start address and did not end at their end address
        if(!insideFinish && !startedAtStart){
            commuteDatabase.deleteTrip(trip);
        } else if(!startedAtStart){
            //the user did not start at their intended start location and is at their end address
            //notify the user that they must add their start location
            NotificationHelper n = new NotificationHelper(context, "noStartAddress");
            n.getNotificationIntent(updateIntent, "No Start Address", "No start address for current trip.", 111);
        } else if(!insideFinish & startedAtStart){
            //they are not at the end address but they started in their start address
            //notify the users
            NotificationHelper n = new NotificationHelper(context, "noEndAddress");
            n.getNotificationIntent(updateIntent, "No End Address", "No end address for current trip.", 111);
        }
    }

    /**
     * This method stops the tracking by stopping the service and calls a method to check the validity of the current trip.
     */
    @Override
    protected void stopService() {
        super.stopService();
        checkTrip();
    }
}
