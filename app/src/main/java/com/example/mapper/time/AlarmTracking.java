package com.example.mapper.time;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.example.mapper.location.LocationUpdateService;

import java.util.Calendar;

/**
 * This class is responsible for creating and deleting alarms.
 * The alarms will be used to set tracking times that the user has specified. When the trigger time has been reached the pending intent
 * will execute which will execute the intent for the LocationUpdateService.
 * @author Rhane Mercado
 */
public class AlarmTracking {

    private AlarmManager alarmManager;
    private Context context;

    /**
     * Create a AlarmTracking object
     * @param c The Context of the class
     */
    public AlarmTracking(Context c){
        context = c;
        alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * This method sets an exact alarm with the specified fields passed in.
     * @param requestCode The unique number to identify the PendingIntent
     * @param day The day of the alarm
     * @param hour The hour of the alarm
     * @param minute The minute of the alarm
     * @param data The id of the event
     */
    public void setAlarm(int requestCode, int day, int hour, int minute, String data){
        //create a new intent for the service and set the data with the data passed in
        Intent alarmIntent = new Intent(context, LocationUpdateService.class);
        //put String extra for the intent, this will be used to identify which event triggered the LocationUpdateService
        alarmIntent.putExtra("data", data);
        //set data so that the intent is unique, won't be overwritten
        alarmIntent.setData(Uri.parse(data));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create a pending intent to schedule
            PendingIntent pendingIntent = PendingIntent.getForegroundService(context, requestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

            //configure the time of the alarm
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            //get the current calendar which will be used to compare the time set
            long before = calendar.getTimeInMillis();

            //set alarm 5 minutes earlier to account for delays
            if(minute <= 4){
                hour = hour - 1;
                minute = minute + 55;
            } else {
                minute = minute - 5;
            }

            //set the calendar to the day and time passed in
            calendar.set(Calendar.DAY_OF_WEEK, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            //get the time set
            long time = calendar.getTimeInMillis();

            if(time <= before){
                //the time set has been passed, set the alarm for the next week
                time += (86400000 * 7);
            }

            //Fires the pending intent at the specified time using the set alarm clock
            //It implies RTC_WAKEUP. Wakes up the device to fire the pending intent at the specified time
            //a small icon will be seen by the user to inform them that there is an exact alarm set
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(time, null);
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        }
    }

    /**
     * This method cancels the pending intent with the same request code and the same intent
     * @param requestCode The request code of the pending intent
     * @param data The id of the event
     */
    public void cancelAlarm(int requestCode, String data){
        //create the same intent with the same data passed in
        Intent cancelIntent = new Intent(context, LocationUpdateService.class);
        cancelIntent.setData(Uri.parse(data));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //create the same pendingIntent that will be cancelled
            PendingIntent cancelPendingIntent = PendingIntent.getForegroundService(context, requestCode, cancelIntent, PendingIntent.FLAG_IMMUTABLE);
            //to cancel the alarm the pending intent must be cancelled first and then the alarm
            cancelPendingIntent.cancel();
            //cancel the alarm
            alarmManager.cancel(cancelPendingIntent);
        }
    }
}
