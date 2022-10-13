package com.example.mapper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.activity.UpdateTripActivity;
import com.example.mapper.helper.AlertDialogHelper;
import com.example.mapper.helper.NotificationHelper;
import com.example.mapper.model.Trip;
import com.example.mapper.model.Uploaded;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class controls the home fragment shown to the user. The user is able to view and delete their trips.
 * It will also allow the user to stop tracking.
 * @author Rhane Mercado
 */
public class HomeFragment extends Fragment {

    private ListView listView;
    private RadioButton all, days, month;
    private List<Trip> tripList;

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
            //get the trip selected
            Trip trip = (Trip) parent.getItemAtPosition(position);
            //get the dialog which will show if the user clicks on one of the trips
            AlertDialog.Builder builder = AlertDialogHelper.getBuilder(getContext(), "Trip " + trip.getId(), trip.viewTrip());
            //check if the trip has a missing address
            if(trip.getStartLocation().equals("?")){
                builder.setPositiveButton("Update Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //make an intent which will update the start address
                        Intent updateIntent = new Intent(getContext(), UpdateTripActivity.class);
                        updateIntent.putExtra("location", "start");
                        updateIntent.putExtra("position", 0);
                        updateIntent.putExtra("id", Integer.valueOf(trip.getId()));
                        startActivity(updateIntent);
                    }
                });
            } else if(trip.getEndLocation().equals("?")){
                builder.setPositiveButton("Update End", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //create an intent which will update the end address
                        Intent updateIntent = new Intent(getContext(), UpdateTripActivity.class);
                        updateIntent.putExtra("location", "end");
                        updateIntent.putExtra("position", 1);
                        updateIntent.putExtra("id", Integer.valueOf(trip.getId()));
                        startActivity(updateIntent);
                    }
                });
            } else {
                builder.setNegativeButton("Change Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String message = "By changing the upload status to Allow the trip is allowed to be uploaded passively. " +
                                "By changing the upload status to Deny the trip will never be uploaded to the server passively. " +
                                "However, you are still able to upload the trip at your discretion.";
                        AlertDialog.Builder changeDialog = AlertDialogHelper.getBuilder(getContext(), "Change Upload", message);
                        changeDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                trip.setCanUpload(Uploaded.Allow);
                                BaseActivity.CD.updateTrip(trip);
                            }
                        }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                trip.setCanUpload(Uploaded.Deny);
                                BaseActivity.CD.updateTrip(trip);
                            }
                        }).create().show();
                    }
                });
            }
            builder.setNeutralButton("Delete Trip", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BaseActivity.CD.deleteTrip(trip);
                    radioChecked();
                }
            }).create().show();
        }
    };

    String tag = "HelloWorld";

    /**
     * This method inflates the corresponding fragment and initialises views that will be used.
     * @param inflater The object that can be used to inflate all the view in the fragment.
     * @param container Parent of the UI is attached to.
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     * @return View
     */
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        listView = root.findViewById(R.id.list_trips);
        all = root.findViewById(R.id.radio_all);
        days = root.findViewById(R.id.radio_days);
        month = root.findViewById(R.id.radio_month);
        tripList = BaseActivity.CD.getTrips();

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioChecked();
            }
        });
        days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioChecked();
            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioChecked();
            }
        });
        return root;
    }

    /**
     * Display all the trips to the user
     * @param view The view returned by onCreateView
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NotificationHelper.removeNotification(getContext());
        radioChecked();
    }

    /**
     * Redisplays the trips to the user
     */
    @Override
    public void onStart() {
        super.onStart();
        radioChecked();
    }

    /**
     * This method displays a number of trips depending on which radio button was selected.
     */
    private void radioChecked(){
        //get the trips from the database
        tripList = BaseActivity.CD.getTrips();
        //check which radio button was selected
        if(days.isChecked() && BaseActivity.CD.getTrips().size() > 7){
            tripList = BaseActivity.CD.getTrips().subList(0, 7);
        }
        if(month.isChecked() && BaseActivity.CD.getTrips().size() > 31){
            tripList = BaseActivity.CD.getTrips().subList(0, 31);
        }
        //reset the adapter and listener.
        listView.setAdapter(new ArrayAdapter<Trip>(getContext(), android.R.layout.simple_expandable_list_item_1, tripList));
        listView.setOnItemClickListener(listener);
    }
}