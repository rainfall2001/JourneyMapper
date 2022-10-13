package com.example.mapper.location;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.mapper.BaseActivity;
import com.example.mapper.activity.MapsActivity;
import com.example.mapper.helper.NotificationHelper;
import com.example.mapper.helper.RequestListener;
import com.example.mapper.model.Event;
import com.example.mapper.time.AlarmTracking;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is responsible for running the foreground service. It will show a notification informing the user a foreground service
 * is running. It will continue to run the service until the timer runs out or the user selects for the service to stop.
 * @author Rhane Mercado
  */
public class LocationUpdateService extends Service {

    private TimerTask myTask = new TimerTask(){

        @Override
        public void run() {
            stopSelf();
        }
    };
    private Timer myTimer = new Timer();
    private long interval;
    private Commute commute;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This method creates the service and checks if the device Location Services is turned on. If it is not
     * it will inform the user by a notification.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //if locations is not turned on then notify the user
        if(!GPS.getInstance().isGPSEnabled(getApplicationContext())){
            Intent gpsIntent = new Intent(getApplicationContext(), BaseActivity.class);
            String channelId = "locations";
            String title = "Turn on Locations";
            String content = "Locations must be turned on.";
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext(), channelId);
            notificationHelper.getNotificationIntent(gpsIntent, title, content, 2);
        }
    }

    /**
     * This method is called when the service is destroyed by either the service itself or a different class.
     * It will stop the location update.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        GPS.getInstance().stopCurrentLocation(getApplicationContext());
    }

    /**
     * This method is called after onCreate. It will turn on tracking.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //get the intent that started the service
        String data = intent.getStringExtra("data");
        //check what type of commute it is
        if(data != null){
            //get the commute that triggered the service
            getCommute(data);
        } else {
            //if there was no data passed in then the commute is a one off commute
            commute = new Commute(getApplicationContext());
        }
        //tracking will run on a new thread.
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        GPS.getInstance().getCurrentLocation(getApplicationContext(), commute);
                    }
                }
        ).start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //get the notification as the user must be informed if there is a foreground service running.
            Intent locationIntent = new Intent(getApplicationContext(), MapsActivity.class);
            String channelId = "serviceID";
            String title = "Currently Tracking";
            String content = "Tap if you want to stop tracking.";
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext(), channelId);
            startForeground(1, notificationHelper.getNotification(locationIntent, title, content, 1));
        }
        return START_NOT_STICKY;
    }

    /**
     * This method checks which commute is to be created. Either a Commute or an EventCommute.
     * If it is an EventCommute a get request for the duration will be created and the alarm will be
     * reset.
     */
    private void getCommute(String data){
        //create a new requestListener for the directionRequest
        RequestListener listener = new RequestListener() {
            @Override
            public void success() {
                //set the interval to the greater value
                if(EventCommute.getInterval() > DirectionRequest.getInterval()){
                    interval = EventCommute.getInterval();
                } else {
                    interval = DirectionRequest.getInterval();
                }
                interval = interval + 3600000;
                myTimer.schedule(myTask, interval);
            }
            @Override
            public void failed() {
                //get the interval and set it as the timer
                interval = EventCommute.getInterval();
                myTimer.schedule(myTask, interval);
            }
        };
        //get the event for that day and time
        Event e = EventCommute.getEvent(getApplicationContext(), data);
        //get the interval for that commute
        DirectionRequest directionRequest = new DirectionRequest(getApplicationContext(), EventCommute.getStartAddress(), EventCommute.getEndAddress(), EventCommute.getTravelMethod());
        directionRequest.setListener(listener);
        //create the new commute
        commute = new EventCommute(getApplicationContext());

        //reset the alarm for next week
        AlarmTracking a = new AlarmTracking(getApplicationContext());
        a.setAlarm(e.getRequestCode(), e.getDayInt(), e.getStartHour(), e.getStartMinute(), e.getId());
    }
}
