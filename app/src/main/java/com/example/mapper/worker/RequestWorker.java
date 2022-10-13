package com.example.mapper.worker;

import android.content.Context;
import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/**
 * This class is responsible for creating the work request.
 * The work will be uploading trips to the server. The upload work request can also be cancelled.
 */
public class RequestWorker {

    /**
     * This class creates a periodic work request for the RequestWorker class.
     * @param context Context of the class
     */
    public static void uploadRequest(Context context){
        //create constraints so that the work only executes if all these constraints have been meet.
        //This makes sure that the battery of the phone is not low and that the user has a network connection
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        //create the periodic request
        //the uploads will happen once a week, starting when the user selected passive uploads.
        //if something went work, the work will execute again.
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(UploadWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MINUTES
                )
                .build();
        //enqueue the work will a unique id. This allows the system to cancel the work with the same id.
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("uploadTrips", ExistingPeriodicWorkPolicy.KEEP, request);
    }

    /**
     * This method cancels the periodic work request for uploading the trips
     * @param context Context of the class
     */
    public static void cancelUploads(Context context){
        WorkManager.getInstance(context).cancelUniqueWork("uploadTrips");
    }
}

