package com.example.mapper.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mapper.R;

/**
 * This class creates notifications. It can either create and return a notification or create and show the notification.
 * @author Rhane Mercado
 */
public class NotificationHelper {

    private Context context;
    private String channelID;
    private NotificationCompat.Builder builder;

    public NotificationHelper(Context context, String channelID){
        this.context = context;
        this.channelID = channelID;
        builder = new NotificationCompat.Builder(context, channelID);
    }

    /**
     * This method creates the notification channel where the notification will be put.
     */
    private void createNotificationChannel(){
        // Create the NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelID, NotificationManager.IMPORTANCE_LOW);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * This method creates the notification with the specific contents passed in and the intent which will be
     * started once the notification is selected.
     * @param title String of the title
     * @param content String of the message the user will see
     * @param intent The intent that will be executed
     * @param id Int of the id
     */
    private void getBuilderIntent(String title, String content, Intent intent, int id){
        //depending which version of android, a notification channel must be created
        createNotificationChannel();

        //create the pending intent with the intent passed in which will be fired when the user selects the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        //build the notification
        builder.setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    /**
     * This method creates a Notification
     * @param intent The intent that will be fired
     * @param title String of the title
     * @param content String of the message of the notification
     * @param id int of the id
     * @return The notification created
     */
    public Notification getNotification(Intent intent, String title, String content, int id){
        getBuilderIntent(title, content, intent, id);
        return builder.build();
    }

    /**
     * This method creates a Notification and shows it to the user
     * @param intent The intent that will be fired
     * @param title String of the title
     * @param content String of the message of the notification
     * @param id int of the id
     */
    public void getNotificationIntent(Intent intent, String title, String content, int id){
        getBuilderIntent(title, content, intent, id);
        builder.setAutoCancel(true);

        //show the user the notification
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(id, builder.build());
    }

    /**
     * Removes the notification with the id 111
     * @param context The context of the class
     */
    public static void removeNotification(Context context){
        NotificationManagerCompat.from(context).cancel(111);
    }
}
