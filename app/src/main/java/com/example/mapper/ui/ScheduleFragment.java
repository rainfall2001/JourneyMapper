package com.example.mapper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.activity.DayActivity;
import com.example.mapper.helper.AlertDialogHelper;
import com.example.mapper.model.Event;
import com.example.mapper.time.AlarmTracking;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

/**
 * This class controls the schedule fragment shown to the user. The schedule for each day is a button. Each schedule
 * can be viewed by the user and delete or copied to a different day.
 * @author Rhane Mercado
 */
public class ScheduleFragment extends Fragment {

    String tag = "HelloWorld";

    private Button addSchedule;
    private GridLayout mon, tue, wed, thu, fri;
    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    private final int[] daysInt = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY};

    /**
     * This method inflates the corresponding fragment and initialises views that will be used.
     * @param inflater The object that can be used to inflate all the view in the fragment.
     * @param container Parent of the UI is attached to.
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     * @return View
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        addSchedule = root.findViewById(R.id.buttonAddSchedule);
        mon = root.findViewById(R.id.gridMon);
        tue = root.findViewById(R.id.gridTue);
        wed = root.findViewById(R.id.gridWed);
        thu = root.findViewById(R.id.gridThu);
        fri = root.findViewById(R.id.gridFri);

        return root;
    }

    /**
     * This method sets the onClick listener for the button and shows the user their current schedule.
     * @param view The view returned by onCreateView
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //the intent that will be used if the user clicks on the button
        Intent dayIntent = new Intent(getContext(), DayActivity.class);

        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create a new dialog and show it to the user
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Day");
                builder.setItems(days, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //put extra contents depending on which day the user has entered
                        dayIntent.putExtra("day", days[i]);
                        dayIntent.putExtra("dayInt", daysInt[i]);
                        //take the user to a new activity to allow them to create a new event
                        startActivity(dayIntent);
                    }
                }).create().show();
            }
        });

        viewSchedule();
    }

    /**
     * Reload the view after the view has been created.
     */
    @Override
    public void onStart() {
        super.onStart();
        reloadView();
    }

    /**
     * This method removes all the events and displays all the events with the new one added or deleted.
     */
    private void reloadView(){
        if(BaseActivity.CD.getEvents().size() > -1){
            mon.removeAllViews();
            tue.removeAllViews();
            wed.removeAllViews();
            thu.removeAllViews();
            fri.removeAllViews();
            viewSchedule();
        }
    }

    /**
     * This method loops through all the events in the database and makes the schedule visible to the user.
     */
    private void viewSchedule(){
        //get the events and sort them in order
        List<Event> events = BaseActivity.CD.getEvents();

        for(int i = 0; i < events.size(); i++){
            //check which grid the event is to be added to
            if(events.get(i).getDayInt() == daysInt[0]){
                viewDay(mon, events.get(i));
            }
            if(events.get(i).getDayInt() == daysInt[1]){
                viewDay(tue, events.get(i));
            }
            if(events.get(i).getDayInt() == daysInt[2]){
                viewDay(wed, events.get(i));
            }
            if(events.get(i).getDayInt() == daysInt[3]){
                viewDay(thu, events.get(i));
            }
            if(events.get(i).getDayInt() == daysInt[4]){
                viewDay(fri, events.get(i));
            }
        }
    }

    /**
     * This method creates a button for the event and adds it the the grid passed in.
     * It will create an onClick listener for each button which will allow the user to
     * delete the event or copy the event to a different day.
     * @param grid The grid view that the event is to be added to
     * @param event The event to be added to the grid
     */
    private void viewDay(GridLayout grid, Event event){
        //create a new button with the event details as the text
        Button button = new Button(getContext());
        button.setText(event.toString());

        //set an onClick listener that will show an AlertDialog to the user.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = AlertDialogHelper.getBuilder(getContext(), "Commute", event.viewEvent());
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close the dialog
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //delete the event
                        AlarmTracking alarm = new AlarmTracking(getContext());
                        //cancel the pending intent and cancel the alarm
                        alarm.cancelAlarm(event.getRequestCode(), event.getId());
                        BaseActivity.CD.deleteEvent(event);
                        //redisplay the view to the user
                        reloadView();
                    }
                });
                builder.setNeutralButton("Copy Commute", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //create a new alert dialog to allow the user to pick which day they want the event to be copied to
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Select Day");
                        builder.setItems(days, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //create a new intent with the new day the user has selected
                                Intent dayIntent = new Intent(getContext(), DayActivity.class);
                                dayIntent.putExtra("day", days[i]);
                                dayIntent.putExtra("dayInt", daysInt[i]);
                                //put extras of the event to the intent
                                dayIntent.putExtra("hour", event.getStartHour());
                                dayIntent.putExtra("minute", event.getStartMinute());
                                dayIntent.putExtra("startID", event.getStartID());
                                dayIntent.putExtra("endID", event.getEndID());
                                dayIntent.putExtra("travelMethod", event.getTravelMethod());
                                dayIntent.putExtra("interval", event.getInterval());
                                //take the user to the DayActivity to allow them to change any fields they wish to
                                getContext().startActivity(dayIntent);
                            }
                        }).create().show();
                    }
                }).create().show();
            }
        });
        grid.addView(button);
    }


}