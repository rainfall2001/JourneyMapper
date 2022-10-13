package com.example.mapper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mapper.helper.AlertDialogHelper;
import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.location.Commute;
import com.example.mapper.location.LocationUpdateService;
import com.example.mapper.model.Address;
import com.example.mapper.model.TravelMethod;

import org.jetbrains.annotations.NotNull;

/**
 * This class controls the one trip fragment shown to the user. The user is able to start a one off trip
 * by select their start and end address and the travel method.
 * @author Rhane Mercado
 */
public class OneTripFragment extends Fragment {

    private EditText startAddress, endAddress;;
    Button start;
    private Spinner spinnerTravel;
    private ArrayAdapter<Address> adapter;
    private Address startLocation, endLocation;

    /**
     * This method inflates the corresponding fragment and initialises views that will be used.
     * @param inflater The object that can be used to inflate all the view in the fragment.
     * @param container Parent of the UI is attached to.
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     * @return View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_one_trip, container, false);
        start = root.findViewById(R.id.button_start_one);
        startAddress = root.findViewById(R.id.edit_start_address_one);
        endAddress = root.findViewById(R.id.edit_end_address_one);
        spinnerTravel = root.findViewById(R.id.spinner_travel_method_one);

        //set onClick methods for relative views
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BaseActivity.isMyServiceRunning(LocationUpdateService.class, getContext())){
                    Toast.makeText(getContext(), "Service is currently running", Toast.LENGTH_SHORT).show();
                } else if(startLocation != null && endLocation != null){
                    TravelMethod tM = TravelMethod.valueOf(spinnerTravel.getSelectedItem().toString());
                    Commute.setOneOffTrip(startLocation, endLocation, tM);
                    startService();
                } else {
                    Toast.makeText(getContext(), "Please input all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        startAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlertStart();
            }
        });
        endAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlertEnd();
            }
        });
        adapter = new ArrayAdapter<Address>(getContext(), android.R.layout.simple_list_item_1, BaseActivity.CD.getAddresses());
        return root;
    }

    /**
     * This method sets the spinner after the view has been created.
     * @param view The view returned by onCreateView
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinnerTravel.setAdapter(new ArrayAdapter<TravelMethod>(getContext(), android.R.layout.simple_list_item_1, TravelMethod.values()));
    }

    /**
     * This method initialises the adapter to get the new address that was added.
     */
    @Override
    public void onStart() {
        super.onStart();
        adapter = new ArrayAdapter<Address>(getContext(), android.R.layout.simple_list_item_1, BaseActivity.CD.getAddresses());
    }

    /**
     * This method starts the service with an intent.
     */
    private void startService(){
        Intent intent = new Intent(getContext(), LocationUpdateService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(intent);
        }
    }

    /**
     * This method shows a dialog so that the user is able to choose their start address.
     * The selected address will be set as the start address.
     */
    private void setAlertStart(){
        //get the alert dialog builder from the helper
        AlertDialog.Builder builder = AlertDialogHelper.getAddressDialog(getContext());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //set the start address that was selected
                startLocation = BaseActivity.CD.getAddresses().get(i);
                startAddress.setText(startLocation.getAddressName());
            }
        }).create().show();
    }

    /**
     * This method shows a dialog so that the user is able to choose their end address.
     * The selected address will be set as the end address.
     */
    private void setAlertEnd(){
        //get the alert dialog builder from the helper
        AlertDialog.Builder builder = AlertDialogHelper.getAddressDialog(getContext());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //set the end address that was selected
                endLocation = BaseActivity.CD.getAddresses().get(i);
                endAddress.setText(endLocation.getAddressName());
            }
        }).create().show();
    }
}