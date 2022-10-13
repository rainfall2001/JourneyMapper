package com.example.mapper.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;

import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.model.Trip;
import com.example.mapper.worker.RequestWorker;
import com.example.mapper.worker.UploadWorker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class controls the functionality of the upload fragment shown to the user.
 * The user is able to view the trips that have yet to be uploaded, which includes the trips that cannot be uploaded
 * passively. Uploads can also be set to be passive.
 * @author Rhane Mercado
 */
public class UploadFragment extends Fragment {

    private Button upload;
    private ListView listView;
    private List<Trip> uploadTrips, tripList;
    private RadioButton all, days, month;
    private Switch autoSwitch;

    /**
     * This method inflates the corresponding fragment and initialises views that will be used.
     * onClick listeners are also set.
     * @param inflater The object that can be used to inflate all the view in the fragment.
     * @param container Parent of the UI is attached to.
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     * @return View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_upload, container, false);
        //get all the necessary vies from the fragment layout
        upload = (Button) root.findViewById(R.id.button_upload);
        listView = (ListView) root.findViewById(R.id.list_trips_uploaded);
        all = (RadioButton) root.findViewById(R.id.radio_all_upload);
        days = (RadioButton) root.findViewById(R.id.radio_days_upload);
        month = (RadioButton) root.findViewById(R.id.radio_month_upload);

        //get the switch and set the value to the one saved
        autoSwitch = (Switch) root.findViewById(R.id.switch_upload);
        autoSwitch.setChecked(getAuto());

        uploadTrips = new ArrayList<Trip>();

        //set all the onClick listeners
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedItems();
            }
        });
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
        autoSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSwitch();
            }
        });
        return root;
    }

    /**
     * This method displays the trips corresponding to the radio button checked.
     * @param view The view returned by onCreateView
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        radioChecked();
    }

    /**
     * This method checks if the switch is turn on or off.
     * After checking the state it will save it so the next time the user opens the app
     * it will be visible to them whether passive uploads is on or not.
     */
    private void checkSwitch(){
        //check if passive uploads is turned on
        if(autoSwitch.isChecked()){
            //save the state
            setAuto(true);
            //create a request for the passive work
            RequestWorker.uploadRequest(getContext());
            upload.setVisibility(View.INVISIBLE);
        } else {
            //save the state
            setAuto(false);
            //cancel the request for the passive work
            RequestWorker.cancelUploads(getContext());
            //make the button visible
            upload.setVisibility(View.VISIBLE);
            //reload the view
            radioChecked();
        }
    }

    /**
     * This method gets the shared preferences file and writes the value passed in.
     * The autoSwitch value will be set to the boolean value passed in.
     * @param auto The boolean value that will be associated with the autoSwitch
     */
    private void setAuto(boolean auto){
        //get the file
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //set the value
        editor.putBoolean(getString(R.string.auto_switch), auto);
        editor.apply();
    }

    /**
     * This method gets the value of autoSwitch.
     * @return boolean
     */
    private boolean getAuto(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean autoB = sharedPref.getBoolean(getString(R.string.auto_switch), false);
        if(autoB){
            //passive uploads is turned on, make the button invisible so that the user cannot upload their trips themselves
            upload.setVisibility(View.INVISIBLE);
        }
        return autoB;
    }

    /**
     * This method gets all the trips that the user has select and uploads
     * those trips to the server.
     */
    private void getSelectedItems(){
        //the list of boolean values for the listView
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        //loop through all the values checking which value has been selected
        for(int i = 0; i < checked.size(); i++){
            if(checked.valueAt(i)){
                Trip trip = (Trip) listView.getItemAtPosition(checked.keyAt(i));
                //the trip was selected to be uploaded
                uploadTrips.add(trip);
            }
        }
        //upload all the trips in the list
        UploadWorker.uploadTrips(uploadTrips, getContext());
        //reload trips to be uploaded
        radioChecked();
    }
    /**
     * This method displays a number of trips to be uploaded depending on which radio button was selected.
     */
    private void radioChecked(){
        //get the trips to be uploaded from the database
        tripList = BaseActivity.CD.getUploads();
        //check which radio button was selected
        if(days.isChecked() && tripList.size() > 7){
            tripList = tripList.subList(0, 7);
            //tripList = BaseActivity.CD.getUploads().subList(0, 7);
        }
        if(month.isChecked() && tripList.size() > 31){
            tripList = BaseActivity.CD.getUploads().subList(0, 31);
        }
        //reset the adapter
        listView.setAdapter(new ArrayAdapter<Trip>(getContext(), android.R.layout.simple_list_item_multiple_choice, tripList));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
}