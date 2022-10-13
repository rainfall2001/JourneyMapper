package com.example.mapper.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mapper.helper.AlertDialogHelper;
import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.model.Address;
import com.example.mapper.model.Event;
import com.example.mapper.model.TravelMethod;
import com.example.mapper.time.AlarmTracking;

import java.util.Locale;

/**
 * This class controls the functionality of the activity_day layout. When all the necessary fields have been
 * filled out and requirements are met, the event specified will be added.
 * @author Rhane Mercado
 */
public class DayActivity extends AppCompatActivity {

    private EditText editTime, editStart, editEnd;
    private TextView day;
    private Spinner spinnerTravel, spinnerHour, spinnerMinute;
    private ArrayAdapter<Address> adapter;
    private int startHour = -1, startMinute = -1, dayInt, startID = -1, endID = -1, interval, requestCode;;
    private String dayName;
    private boolean updateStart, updateEnd;
    private Integer[] hours = {0,1,2,3,4,5,6,7,8,9};
    private Integer[] minutes = {5,10,15,20,25,30,35,40,45,50,55};

    private TimePickerDialog.OnTimeSetListener onTimeSetListenerStart = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            //get the selected time
            startHour = selectedHour;
            startMinute = selectedMinute;
            //show the selected time to the user
            editTime.setText(String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        //get the extras that was passed in
        Intent intent = getIntent();
        //used to inform the user what day they have selected
        dayName = intent.getStringExtra("day");
        dayInt = intent.getIntExtra("dayInt", -1);
        //will be used to check if the event is a copy
        startHour = intent.getIntExtra("hour", -1);
        //will be used to check if the event needs to be updated
        updateStart = intent.getBooleanExtra("updateStart", false);
        updateEnd = intent.getBooleanExtra("updateEnd", false);

        //set the day the user has selected
        day = findViewById(R.id.textDayOfWeek);
        day.setText(dayName);

        //set all the necessary elements of the layout
        editTime = (EditText) findViewById(R.id.edit_time);
        editStart = (EditText) findViewById(R.id.edit_start_address);
        editEnd = (EditText) findViewById(R.id.edit_end_address);

        spinnerTravel = (Spinner) findViewById(R.id.spinnerTravelMethod);
        spinnerHour = (Spinner) findViewById(R.id.spinner_hour);
        spinnerMinute = (Spinner) findViewById(R.id.spinner_minute);

        spinnerTravel.setAdapter(new ArrayAdapter<TravelMethod>(this, android.R.layout.simple_list_item_1, TravelMethod.values()));
        spinnerHour.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, hours));
        spinnerMinute.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, minutes));

        adapter = new ArrayAdapter<Address>(this, android.R.layout.simple_list_item_1, BaseActivity.CD.getAddresses());

        //check if the commute being created is copied from another commute
        if(startHour > -1){
            Bundle bundle = intent.getExtras();
            startMinute = bundle.getInt("minute");
            startID = bundle.getInt("startID");
            endID = bundle.getInt("endID");
            TravelMethod tM = (TravelMethod) bundle.get("travelMethod");
            interval = bundle.getInt("interval");
            requestCode = bundle.getInt("requestCode");
            setCopy(tM);
        }
    }

    /**
     * This method is called after the view is created and resets the adapter so that the new address added can be seen
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter = new ArrayAdapter<Address>(this, android.R.layout.simple_list_item_1, BaseActivity.CD.getAddresses());
    }

    /**
     * onClick method for the EditText to select the time.
     * It will create a new TimePickerDialog which will allow the user to pick a time for their commute.
     * @param view EditText
     */
    public void selectTime(View view){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListenerStart, startHour, startMinute, true);
        timePickerDialog.setTitle("Select Start Time");
        timePickerDialog.show();
    }

    /**
     * This method sets the necessary elements in the layout. This includes the time, length of commute,
     * travel method, start and end address of the commute that was copied over.
     * @param travelMethod The travel method of the commute that was copied
     */
    private void setCopy(TravelMethod travelMethod){
        editTime.setText(String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute));
        //get the position of the hour spinner
        int hourPosition = (interval - 10) / 60; //minus the 10 minutes added and then int divide by 60 to get the number of hours
        //get the minutes to minus off the interval by getting the number of hours and then converting it to hours
        int minusMinutes = hourPosition * 60;
        //get the hour position by removing the 10 minutes added and then minutes the hours added and then divide by 5 to get the minute the user has selected and then minus one to get the index/position of the spinner
        int minutePosition = (((interval - 10) - minusMinutes) / 5) - 1;
        spinnerHour.setSelection(hourPosition);
        spinnerMinute.setSelection(minutePosition);
        spinnerTravel.setSelection(travelMethod.ordinal());

        //check if the address is to be updated, if it isn't fill in the fields
        if(!updateStart){
            editStart.setText(BaseActivity.CD.getAddress(startID).getAddressName());
        }
        if(!updateEnd){
            editEnd.setText(BaseActivity.CD.getAddress(endID).getAddressName());
        }

    }


    /**
     * This method checks if the schedule that will be added is valid by comparing all the events
     * in the database
     * @return boolean
     */
    private boolean validSchedule(){
        int count = 0;
        for(int i = 0; i < BaseActivity.CD.getEvents().size(); i++){
            //check the number of schedules on the day selected
            if(BaseActivity.CD.getEvents().get(i).getDayInt() == dayInt){
                count++;
            }
            //check if the schedule is on the same day and time as any other schedule
            if(BaseActivity.CD.getEvents().get(i).getDayInt() == dayInt && BaseActivity.CD.getEvents().get(i).getStartHour() == startHour && BaseActivity.CD.getEvents().get(i).getStartMinute() == startMinute){
                //this event cannot be added, inform the user
                Toast.makeText(this, "There is already a commute on the same day and time.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //the schedule is not valid if there are already two events on the same day
        if(count == 4){
            Toast.makeText(this, "There are already four schedule set.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * onClick method for the add schedule button. This will add a new schedule to the database and show it to the user
     * if the event is valid otherwise it will inform the user that the schedule was not valid.
     * @param view Button
     */
    public void addSchedule(View view){
        //check if the current event is to be updated because an address was deleted and that the update is valid
        if((updateStart || updateEnd) && startID != endID){
            //get the event to be updated
            Event e = BaseActivity.CD.getEvent(requestCode);
            //set the necessary fields
            e.setStartID(startID);
            e.setEndID(endID);
            BaseActivity.CD.updateEvent(e);
            finish();
        }
        //make sure that the user has not entered the same address for start and stop
        else if(startID == endID){
            Toast.makeText(this, "Invalid, start and end address are the same.", Toast.LENGTH_SHORT).show();
        }
        //check if all the necessary fields have been added
        else if(startHour > -1 && startMinute > -1 && startID > -1 && endID > -1){
            //check if the schedule that will be added is a valid one
            if(!validSchedule()){
                this.finish();
                return;
            }
            TravelMethod tM = TravelMethod.valueOf(spinnerTravel.getSelectedItem().toString());
            //get the interval and change it to millis and add 10 minutes
            int hour = Integer.parseInt(spinnerHour.getSelectedItem().toString());
            int minute = Integer.parseInt(spinnerMinute.getSelectedItem().toString());
            interval = (hour * 60) + minute + 10;
            //create a new event
            Event event = new Event(dayName, dayInt, startHour, startMinute, startID, endID, tM, interval);
            //add to the database
            BaseActivity.CD.addEvent(event);
            //create a new alarm
            AlarmTracking alarm = new AlarmTracking(this);
            alarm.setAlarm(event.getRequestCode(), dayInt, startHour, startMinute, event.getId());
            finish();
        } else {
            Toast.makeText(this, "Must input all fields.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * onClick method for the EditText used to select the start address.
     * @param view EditText
     */
    public void selectStartAddress(View view){
        AlertDialog.Builder builder = AlertDialogHelper.getAddressDialog(this);
        builder.setTitle("Select Start Address")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //get the id of the start address selected
                        startID = BaseActivity.CD.getAddresses().get(i).getId();
                        //show the user their selected address
                        editStart.setText(BaseActivity.CD.getAddress(startID).getAddressName());
                    }
                }).create().show();
    }

    /**
     * onClick method for the EditText used to select the end address.
     * @param view EditText
     */
    public void selectEndAddress(View view){
        AlertDialog.Builder builder = AlertDialogHelper.getAddressDialog(this);
        builder.setTitle("Select End Address")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //get the id of the end address selected
                        endID = BaseActivity.CD.getAddresses().get(i).getId();
                        //show the user their selected end address
                        editEnd.setText(BaseActivity.CD.getAddress(endID).getAddressName());
                    }
                }).create().show();
    }
}