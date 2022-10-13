package com.example.mapper.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mapper.model.CommuteDatabase;
import com.example.mapper.model.Event;

import java.util.List;

/**
 * This class is responsible for resetting all the alarm after the phone restarts.
 * @author Rhane Mercado
 */
public class AlarmBroadcast extends BroadcastReceiver {

    /**
     * This method resets all the alarms for each event
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //check if the phone has restarted
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //get the events in the database
            CommuteDatabase c = new CommuteDatabase(context);
            List<Event> eventList = c.getEvents();
            AlarmTracking alarm = new AlarmTracking(context);
            Event e = null;
            //loop through all the events and set an alarm for all of them
            for(int i = 0; i < eventList.size(); i++){
                e = eventList.get(i);
                alarm.setAlarm(e.getRequestCode(), e.getDayInt(), e.getStartHour(), e.getStartMinute(), e.getId());
            }
        }
    }
}
