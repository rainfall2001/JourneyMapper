package com.example.mapper.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mapper.BaseActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
 * This is class is responsible for the local database on the device. It creates the four tables. The trip, coordinate, address and event
 * table. Trips, coordinates, addresses and events can be added and deleted. Trips, addresses and events can also be updated. Each table
 * excluding the coordinate table can be retrieve.
 * @author Rhane Mercado
 */
public class CommuteDatabase extends SQLiteOpenHelper {

    String tag = "HelloWorld";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Commute.db";

    /**
     * Create the database
     * @param context The context of the class
     */
    public CommuteDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String TABLE_NAME_TRIP = "TRIP_TABLE";
    private static final String COLUMN_TRIP_ID = "TRIP_ID";
    private static final String COLUMN_START_LOCATION = "START_LOCATION";
    private static final String COLUMN_END_LOCATION = "END_LOCATION";
    private static final String COLUMN_TRAVEL_METHOD = "TRAVEL_METHOD";
    private static final String COLUMN_UPLOADED = "UPLOADED";
    private static final String COLUMN_CAN_UPLOAD = "CAN_UPLOAD";

    private final static String CREATE_TRIP_ENTRIES =
            "CREATE TABLE " + TABLE_NAME_TRIP + " (" +
                    COLUMN_TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_START_LOCATION + " TEXT," +
                    COLUMN_END_LOCATION + " TEXT," +
                    COLUMN_TRAVEL_METHOD + " TEXT," +
                    COLUMN_UPLOADED + " INTEGER," +
                    COLUMN_CAN_UPLOAD + " INTEGER)";

    private static final String TABLE_NAME_COORDINATES = "COORDINATES_TABLE";
    private static final String COLUMN_COORDINATE_ID = "COORDINATE_ID";
    private static final String COLUMN_LATITUDE = "LATITUDE";
    private static final String COLUMN_LONGITUDE = "LONGITUDE";
    private static final String COLUMN_DATE = "DATE";
    private static final String COLUMN_FOREIGN_TRIP_ID = "FOREIGN_TRIP_ID";

    private final static String CREATE_COORDINATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME_COORDINATES + " (" +
                    COLUMN_COORDINATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_LATITUDE + " REAL," +
                    COLUMN_LONGITUDE + " REAL," +
                    COLUMN_DATE + " TEXT," +
                    COLUMN_FOREIGN_TRIP_ID + " TEXT," +
                    " FOREIGN KEY (" + COLUMN_FOREIGN_TRIP_ID + ") REFERENCES " +
                    TABLE_NAME_TRIP + "(" + COLUMN_TRIP_ID + "))";

    private static final String TABLE_NAME_ADDRESSES = "ADDRESS_TABLE";
    private static final String COLUMN_ADDRESS_ID = "ADDRESS_ID";
    private static final String COLUMN_ADDRESS_NAME = "ADDRESS_NAME";
    private static final String COLUMN_ADDRESS_INFO = "ADDRESS_INFO";
    private static final String COLUMN_ADDRESS_LATITUDE = "ADDRESS_LATITUDE";
    private static final String COLUMN_ADDRESS_LONGITUDE = "ADDRESS_LONGITUDE";

    private final static String CREATE_ADDRESSES_ENTRIES =
            "CREATE TABLE " + TABLE_NAME_ADDRESSES + " (" +
                    COLUMN_ADDRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_ADDRESS_NAME + " TEXT," +
                    COLUMN_ADDRESS_INFO + " TEXT," +
                    COLUMN_ADDRESS_LATITUDE + " REAL," +
                    COLUMN_ADDRESS_LONGITUDE + " REAL)";

    private static final String TABLE_NAME_EVENT = "EVENT_TABLE";
    private static final String COLUMN_EVENT_ID = "ID";
    private static final String COLUMN_DAY_STRING = "DAY_STRING";
    private static final String COLUMN_DAY_INT = "DAY_INT";
    private static final String COLUMN_HOUR = "HOUR";
    private static final String COLUMN_MINUTE = "MINUTE";
    private static final String COLUMN_START = "START_ID";
    private static final String COLUMN_END = "END_ID";
    private static final String COLUMN_REQUEST = "REQUEST_CODE";
    private static final String COLUMN_INTERVAL = "INTERVAL";

    private final static String CREATE_EVENT_ENTRIES =
            "CREATE TABLE " + TABLE_NAME_EVENT + " (" +
                    COLUMN_EVENT_ID + " TEXT," +
                    COLUMN_DAY_STRING + " TEXT," +
                    COLUMN_DAY_INT + " INTEGER," +
                    COLUMN_HOUR + " INTEGER," +
                    COLUMN_MINUTE + " INTEGER," +
                    COLUMN_START + " INT," +
                    COLUMN_END + " INT," +
                    COLUMN_TRAVEL_METHOD + " TEXT," +
                    COLUMN_REQUEST + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_INTERVAL + " INT)";

    private final static String DELETE_TRIP_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME_TRIP;
    private final static String DELETE_COORDINATE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME_COORDINATES;
    private final static String DELETE_ADDRESSES_ENTRIES = "DROP TABLE IF EXISTS "  + TABLE_NAME_ADDRESSES;
    private final static String DELETE_EVENT_ENTRIES = "DROP TABLE IF EXISTS "  + TABLE_NAME_EVENT;

    /**
     * This method is called when the database is created for the first time, ie the file
     * does not exist yet, otherwise it does not call this method. It initialises
     * the creation and population of the tables.
     * @param db The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRIP_ENTRIES);
        db.execSQL(CREATE_COORDINATE_ENTRIES);
        db.execSQL(CREATE_ADDRESSES_ENTRIES);
        db.execSQL(CREATE_EVENT_ENTRIES);
    }

    /**
     * This method is only called when the database file exist but the version number that is
     * stored is lower than requested in the constructor, ie an extra column is added to the table.
     * @param db The database
     * @param versionOld The version number of the old database
     * @param versionNew The version number of the new database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionOld, int versionNew) {
        db.execSQL(DELETE_TRIP_ENTRIES);
        db.execSQL(DELETE_COORDINATE_ENTRIES);
        db.execSQL(DELETE_ADDRESSES_ENTRIES);
        db.execSQL(DELETE_EVENT_ENTRIES);
        onCreate(db);
    }

    /**
     * This method is called when the database connection is being configured. It does not
     * modify the database. In this instance the feature of foreign key support has been set
     * to true. This will make sure that no trips are deleted if there are still corresponding trips
     * and no coordinates will be added if the trip id is not already in the table.
     * @param db The database
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * This method deletes the trip that was passed in. It will also delete all corresponding
     * coordinates of that trip.
     * @param trip The trip that will be deleted
     */
    public void deleteTrip(Trip trip){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_COORDINATES, COLUMN_FOREIGN_TRIP_ID + "=?", new String[] {trip.getId()});
        db.delete(TABLE_NAME_TRIP, COLUMN_TRIP_ID + "=?", new String[] {trip.getId()});

    }

    /**
     * This method deletes the address that was passed in.
     * @param address The address that will be deleted
     */
    public void deleteAddress(Address address){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_ADDRESSES, COLUMN_ADDRESS_NAME + "=?", new String[] {address.getAddressName()});
    }

    /**
     * This method deletes the event that was passed in
     * @param event The event that will be deleted
     */
    public void deleteEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_EVENT, COLUMN_EVENT_ID + "=?", new String[] {event.getId()});
    }

    /**
     * This method updates the end location, start location and the uploaded column of the trip passed in.
     * @param trip The trip to be updated
     */
    public void updateTrip(Trip trip){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getTripValues(trip);
        db.update(TABLE_NAME_TRIP, values, COLUMN_TRIP_ID + "=?", new String[] {trip.getId()});
    }

    /**
     * This method updates the address name of the address passed in.
     * @param a The address to be be updated
     */
    public void updateAddress(Address a){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getAddressValues(a);
        db.update(TABLE_NAME_ADDRESSES, values, COLUMN_ADDRESS_ID + "=?", new String[] {String.valueOf(a.getId())});
    }

    /**
     * This method updates start id, end id of the event passed in.
     * @param e The event to be updated
     */
    public void updateEvent(Event e){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getEventValues(e);
        db.update(TABLE_NAME_EVENT, values, COLUMN_EVENT_ID + "=?", new String[] {e.getId()});
    }

    /**
     * This method adds a trip to the TRIP_TABLE. The id of the trip is assigned by the database,
     * it auto increments.
     * @param trip The trip that will be added
     */
    public void addTrip(Trip trip){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getTripValues(trip);
        //set the id of the trip passed in
        trip.setId(String.valueOf(db.insert(TABLE_NAME_TRIP, null, values)));
    }

    /**
     * This method adds a coordinate to the database
     * @param c The Coordinate that will be added to the database
     */
    public void addCoordinates(Coordinate c){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, c.getLatitude());
        values.put(COLUMN_LONGITUDE, c.getLongitude());
        values.put(COLUMN_DATE, c.getDate());
        values.put(COLUMN_FOREIGN_TRIP_ID, c.getTripId());
        db.insert(TABLE_NAME_COORDINATES, null, values);
    }

    /**
     * This methods adds an address to the database
     * @param a The address which will be added to the table
     */
    public void addAddress(Address a){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getAddressValues(a);
        //set the address id using the auto increment value after the address is inserted
        a.setId((int) db.insert(TABLE_NAME_ADDRESSES, null, values));
    }

    /**
     * This method adds an event to the database
     * @param e The event which will be added to the event table
     */
    public void addEvent(Event e){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getEventValues(e);
        //set the event id using the auto increment value after the event is inserted
        e.setRequestCode((int) db.insert(TABLE_NAME_EVENT, null, values));
    }

    /**
     * This method gets the time, in a form of a String, when the trip started by getting the
     * first coordinate of the specified trip
     * @param trip The Trip object
     * @return String of the time
     */
    public String getTime(Trip trip){
        //create a query to the coordinate table to get the row with the specified trip id
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_NAME_COORDINATES + " WHERE " + COLUMN_FOREIGN_TRIP_ID + " = " + trip.getId();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            //get the time
            String time = cursor.getString(3);
            return time;
        }
        return null;
    }


    /**
     * This method gets the trip with the id passed in.
     * @param id The id of the trip
     * @return The Trip with the same id passed in
     */
    public Trip getTrip(int id){
        //create a query to the database to get a trip with the same id passed in
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_NAME_TRIP + " WHERE " + COLUMN_TRIP_ID + " = " + id;
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            return createTrip(cursor);
        }
        return null;
    }

    /**
     * This method gets an address in the table with the specified id.
     * @param id The id of the address in the database
     * @return An address with with the id passed in
     */
    public Address getAddress(int id){
        //create a query to get the address with the id passed in
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_NAME_ADDRESSES + " WHERE " + COLUMN_ADDRESS_ID + " = " + id;
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            return createAddress(cursor);
        }
        return null;
    }

    /**
     * This method gets an event with the specific requestCode passed in.
     * @param code The requestCode of the Event
     * @return An Event with the same requestCode passed in.
     */
    public Event getEvent(int code){
        //create a query to get the address with the requestCode passed in
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_NAME_EVENT + " WHERE " + COLUMN_REQUEST + " = " + code;
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            return createEvent(cursor);
        }
        return null;
    }

    /**
     * This method gets all the trips in the database
     * @return List of Trips in the table
     */
    public List<Trip> getTrips(){
        String queryString = "SELECT * FROM " + TABLE_NAME_TRIP;
        return createTripList(queryString);
    }

    /**
     * This method gets a list of trips that have yet to be uploaded.
     * @return A list of trips to be uploaded
     */
    public List<Trip> getUploads(){
        String queryString = "SELECT * FROM " + TABLE_NAME_TRIP + " WHERE " + COLUMN_UPLOADED + " = " + Uploaded.FALSE.ordinal();
        return createTripList(queryString);
    }

    /**
     * This method gets a list of trips that can be uploaded passively.
     * @return A list of trips that can be uploaded passively
     */
    public List<Trip> getUploadsPassively(){
        String queryString = "SELECT * FROM " + TABLE_NAME_TRIP + " WHERE " + COLUMN_UPLOADED + " = " + Uploaded.FALSE.ordinal() + " AND " + COLUMN_CAN_UPLOAD + " = " + Uploaded.Allow.ordinal();
        return createTripList(queryString);
    }

    /**
     * This method gets all the addresses in the database.
     * It will get all the data for each address and create a new Address object and set
     * the id. This address will then be added to the list.
     * @return A List of addresses
     */
    public List<Address> getAddresses(){
        List<Address> addressList = new ArrayList<>();
        //create a query for the address table
        String queryString = "SELECT * FROM " + TABLE_NAME_ADDRESSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            //get all the data from the table and create a new Address object
            do{
                addressList.add(createAddress(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return addressList;
    }

    public List<Coordinate> getCoordinates(Trip trip){
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        //create a query to the coordinate table to get the row with the specified trip id
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TABLE_NAME_COORDINATES + " WHERE " + COLUMN_FOREIGN_TRIP_ID + " = " + trip.getId();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do{
                double lat = cursor.getDouble(1);
                double lon = cursor.getDouble(2);
                String date = cursor.getString(3);
                Coordinate c = new Coordinate(lat, lon, trip, date);
                coordinates.add(c);
            }while(cursor.moveToNext());
        }

        return coordinates;
    }

    /**
     * This method gets all the events in the database
     * @return A List of events
     */
    public List<Event> getEvents(){
        List<Event> eventList = new ArrayList<>();
        //create a query for the entire event table
        String queryString = "SELECT * FROM " + TABLE_NAME_EVENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do{
                //get the event and add it to the list
                eventList.add(createEvent(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.sort(eventList);
        return eventList;
    }

    /**
     * This method queries the database and gets all the data
     * @param queryString The query to the database
     * @return List of trips
     */
    private List<Trip> createTripList(String queryString){
        List<Trip> tripList = new ArrayList<>();
        //query the database with the String passed in
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        //get the data
        if(cursor.moveToFirst()){
            do{
                tripList.add(createTrip(cursor));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        //sort list
        Collections.sort(tripList);
        return tripList;
    }

    /**
     * This method gets all the data for one Trip and creates a new trip object.
     * @param cursor Results of the query, one line
     * @return Trip object created
     */
    private Trip createTrip(Cursor cursor){
        //get all the data from the table and create a new Trip object while also setting the trip id
        Integer id = cursor.getInt(0);
        String tripID = String.valueOf(id);
        String startLocation = cursor.getString(1);
        String endLocation = cursor.getString(2);
        String tM = cursor.getString(3);
        TravelMethod travelMethod = TravelMethod.valueOf(tM);
        Uploaded uploaded = Uploaded.values()[cursor.getInt(4)];
        Uploaded canUpload = Uploaded.values()[cursor.getInt(5)];

        //create a new trip
        Trip trip = new Trip(startLocation, endLocation, travelMethod,uploaded, canUpload);
        //set the id to the auto incremented value that the database assigned
        trip.setId(tripID);
        //get the time that the trip had started
        trip.setTime(this.getTime(trip));
        return trip;
    }

    /**
     * This method gets all the data for one Address and creates a new Address object
     * @param cursor Result of the query, one line
     * @return Address object created
     */
    private Address createAddress(Cursor cursor){
        Integer id = cursor.getInt(0);
        String addressName = cursor.getString(1);
        String addressInfo = cursor.getString(2);
        Double latitude = cursor.getDouble(3);
        Double longitude = cursor.getDouble(4);

        //create a new address object and set the id
        Address address = new Address(addressName, addressInfo, latitude, longitude);
        //set the id to the auto incremented value that the database assigned
        address.setId(id);
        return address;
    }

    /**
     * This method gets all the data for one Event and creates a new Event object
     * @param cursor Result of the query, one line
     * @return Event object created
     */
    private Event createEvent(Cursor cursor){
        //get all the data from the event
        String dayString = cursor.getString(1);
        int dayInt = cursor.getInt(2);
        int hour = cursor.getInt(3);
        int minute = cursor.getInt(4);
        int startID = cursor.getInt(5);
        int endID = cursor.getInt(6);
        String tM = cursor.getString(7);
        TravelMethod travelMethod = TravelMethod.valueOf(tM);
        int requestCode = cursor.getInt(8);
        int interval = cursor.getInt(9);

        //create a new event with all the info from the database and add it to the list
        Event event = new Event(dayString, dayInt, hour, minute, startID, endID, travelMethod, interval);
        //set the requestCode to the auto incremented value that the database assigned
        event.setRequestCode(requestCode);
        return event;
    }

    /**
     * Creates a new ContentValues object. Values of the trip will be put into the
     * ContentValues object.
     * @param trip Trip
     * @return ContentValues with the Trip data
     */
    private ContentValues getTripValues(Trip trip){
        ContentValues values = new ContentValues();
        //get all the trip values
        values.put(COLUMN_START_LOCATION, trip.getStartLocation());
        values.put(COLUMN_END_LOCATION, trip.getEndLocation());
        String travelMethod = String.valueOf(trip.getTravelMethod());
        values.put(COLUMN_TRAVEL_METHOD, travelMethod);
        values.put(COLUMN_UPLOADED, trip.getUploaded().ordinal());
        values.put(COLUMN_CAN_UPLOAD, trip.getCanUpload().ordinal());
        return values;
    }

    /**
     * Creates a new ContentValues object. Values of the trip will be put into the
     * ContentValues object.
     * @param a Address
     * @return ContentValues with the Address data
     */
    private ContentValues getAddressValues(Address a){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS_NAME, a.getAddressName());
        values.put(COLUMN_ADDRESS_INFO, a.getAddressInfo());
        values.put(COLUMN_ADDRESS_LATITUDE, a.getLatitude());
        values.put(COLUMN_ADDRESS_LONGITUDE, a.getLongitude());
        return values;
    }

    /**
     * Creates a new ContentValues object. Values of the trip will be put into the
     * ContentValues object
     * @param e Event
     * @return ContentValues with Event data
     */
    private ContentValues getEventValues(Event e){
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_ID, e.getId());
        values.put(COLUMN_DAY_STRING, e.getDayString());
        values.put(COLUMN_DAY_INT, e.getDayInt());
        values.put(COLUMN_HOUR, e.getStartHour());
        values.put(COLUMN_MINUTE, e.getStartMinute());
        values.put(COLUMN_START, e.getStartID());
        values.put(COLUMN_END, e.getEndID());
        String travelMethod = String.valueOf(e.getTravelMethod());
        values.put(COLUMN_TRAVEL_METHOD, travelMethod);
        values.put(COLUMN_INTERVAL, e.getInterval());
        return values;
    }
}
