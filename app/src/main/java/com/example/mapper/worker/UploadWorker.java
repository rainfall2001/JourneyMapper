package com.example.mapper.worker;

import android.content.Context;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.amplifyframework.datastore.generated.model.TravelMethod;
import com.example.mapper.BaseActivity;
import com.example.mapper.helper.RequestListener;
import com.example.mapper.model.CommuteDatabase;
import com.example.mapper.model.Coordinate;
import com.example.mapper.model.DataManager;
import com.example.mapper.model.Trip;
import com.example.mapper.model.Uploaded;
import com.example.mapper.ui.UploadFragment;

import java.util.List;

/**
 * This class defines what do to when a work request is set.
 * It will inform the whether the work was successful or not.
 * @author Rhane Mercado
 */
public class UploadWorker extends Worker {

    /**
     * Create a new UploadWorker
     * @param context Context of the application
     * @param params Parameters for the work
     */
    public UploadWorker(Context context, WorkerParameters params) {
        super(context, params);
    }


    /**
     * This class executes when the work request is triggered.
     * It will call a method to upload all the trips which can be uploaded passively
     * @return boolean
     */
    @Override
    public Result doWork() {
        //upload the trips
        uploadTrips(new CommuteDatabase(getApplicationContext()).getUploadsPassively(), getApplicationContext());
        return Result.success();
    }

    /**
     * This method uploads the trips list passed in to the server.
     * If the any trips could not be uploaded, upload was not successful which is indicated by a boolean variable.
     * @param trips List of trips to be uploaded
     * @param context The context of the class
     */
    public static void uploadTrips(List<Trip> trips, Context context){
        //the DataManager is used to add Trips and Coordinates to the external database
        DataManager dataManager = new DataManager(context);

        //get the local database
        CommuteDatabase commuteDatabase = new CommuteDatabase(context);

        //upload all the trips
        for(int i = 0; i < trips.size(); i++){

            Trip trip = trips.get(i);

            if(trip != null){
                //Upload to the AWS database
                TravelMethod method = TravelMethod.values()[trip.getTravelMethod().ordinal()];
                com.amplifyframework.datastore.generated.model.Trip the_trip = dataManager.addTrip(trip.getStartLocation(),trip.getEndLocation(),method);
                List<Coordinate> coordinates = commuteDatabase.getCoordinates(trip);
                for(Coordinate c: coordinates){
                    dataManager.addCoordinate(c.getLatitude(), c.getLongitude(), the_trip, c.getDate());
                }
                //change the status of uploaded
                trip.setUploaded(Uploaded.TRUE);
                //update the local database
                commuteDatabase.updateTrip(trips.get(i));
            }
        }
        trips = null;
    }
}
