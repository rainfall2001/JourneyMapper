package com.example.mapper.model;

import android.content.Context;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Coordinate;
import com.amplifyframework.datastore.generated.model.Coordinates;
import com.amplifyframework.datastore.generated.model.TravelMethod;
import com.amplifyframework.datastore.generated.model.Trip;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/***
 * The DataManager class provides a simple Java interface between the Amplify AWS NoSQL database
 * and android.
 * NOTE: no testing required as simply using boilerplate code from Amplify Documentation
 * i.e. relying on AWS to have tested their functions.
 * @author Jessica Turner
 */
public class DataManager {

    //A formatter which ensures dates are in the correct database format
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * The constructor initialises a connection to the database using the Amplify libraries.
     * @param applicationContext The context of the android application.
     */
    public DataManager(Context applicationContext){
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(applicationContext);
            System.out.println("Initialized Amplify");
        } catch (AmplifyException failure) {
            System.err.println("Could not initialize Amplify: " + failure);
        }

        //Code for observing changes in the database
       /* Amplify.DataStore.observe(Trip.class,
                started -> System.out.println("Trip observation began."),
                change -> System.out.println(change.item().toString()),
                failure -> System.err.println("Trip observation failed. " + failure),
                () -> System.out.println("Trip observation complete.")
        );

        Amplify.DataStore.observe(Coordinates.class,
                started -> System.out.println("Coordinates observation began."),
                change -> System.out.println(change.item().toString()),
                failure -> System.err.println("Coordinates observation failed. " + failure),
                () -> System.out.println("Coordinates observation complete.")
        );*/

    }

    /**
     * A method which allows us to add a Trip to the database.
     * @param start The start location of the trip.
     * @param end The end location of the trip.
     * @param method The travel method of the trip using the TravelMethod enumerator.
     * @return The trip object.
     */
    public Trip addTrip(String start, String end, TravelMethod method){
        Trip item = Trip.builder()
                .startLocation(start)
                .endLocation(end)
                .travelMethod(method)
                .build();
        Amplify.DataStore.save(
                item,
                success -> System.out.println("Saved Trip item: " + success.item().getId()),
                error -> System.err.println("Could not save Trip item to DataStore. " + error)
        );

        return item;
    }

    /**
     * A method which adds a coordinate to the database for a specific trip.
     * @param lat The latitude of the coordinate (using WGS84 coordinate system).
     * @param lon The longitude of the coordinate (using WGS84 coordinate system).
     * @param trip The trip object that this coordinate belongs to. A coordinate cannot be added
     *             if it does not belong to a specified trip.
     * @return The Coordinates object.
     */
    public Coordinates addCoordinate(double lat, double lon, Trip trip, String d){
        //System.out.println("d=" + d);
        //System.out.println("temporal=" + new Temporal.DateTime(d).toString());
        //Temporal.DateTime td = new Temporal.DateTime(d);
        Coordinates item = Coordinates.builder()
                .coord(new Coordinate(lat,lon))
                .timestamp(new Temporal.DateTime(d))
                .tripId(trip.getId())
                .build();
        Amplify.DataStore.save(
                item,
                success -> System.out.println("Saved Coordinates item: " + success.item().getId() + success.item().toString()),
                error -> System.err.println("Could not save Coordinates item to DataStore. " + error)
        );

        return item;
    }

    /**
     * A method which deletes a trip and it's associated coordinates.
     * @param item The trip object to be deleted.
     */
    public void deleteTrip(Trip item){
        System.out.println("DELETE!!!!");
        Amplify.DataStore.delete(item,
                deleted -> System.out.println("Trip deleted"),
                failure -> failure.printStackTrace());
    }

    /**
     * A method which updates a trip's details.
     * NOTE: Recorded coordinates cannot be modified.
     * @param tripID The ID of the trip to update.
     * @param start The new start location.
     * @param end The new end location.
     * @param method The new travel method.
     */
    public void updateTrip(String tripID, String start, String end, TravelMethod method){
        Amplify.DataStore.query(Trip.class, Where.id(tripID),
                matches ->{
                    if(matches.hasNext()){
                        Trip original = matches.next();
                        Trip updatedTrip = original.copyOfBuilder()
                                .startLocation(start)
                                .endLocation(end)
                                .travelMethod(method)
                                .build();
                        Amplify.DataStore.save(updatedTrip,
                                updated -> System.out.println("Updated the trip."),
                                failure -> System.err.println("Failed to update the trip."));
                    }
                },
                failure -> System.err.println("Update Trip query failed." + failure));
    }

}