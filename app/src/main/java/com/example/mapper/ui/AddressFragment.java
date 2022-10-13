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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.activity.DayActivity;
import com.example.mapper.activity.SearchAddressActivity;
import com.example.mapper.helper.AlertDialogHelper;
import com.example.mapper.model.Address;
import com.example.mapper.model.Event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class controls the address fragment shown to the user. The user is able to view and delete all their current
 * addresses. It will also allow the user to add a new address.
 * @author Rhane Mercado
 */
public class AddressFragment extends Fragment {

    String tag = "HelloWorld";

    private EditText editText;
    private ListView listView;
    private Button addressButton;
    private List<Intent> intents = new ArrayList<Intent>();

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
            //get the address selected
            Address address = (Address) parent.getItemAtPosition(position);
            //build and show the address as a alert dialog
            AlertDialog.Builder builder = AlertDialogHelper.getBuilder(getContext(), "Address " + address.getId(), address.viewAddress());
                    builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //build a new alert dialog to inform the user what will happen if they delete the address they have selected
                            AlertDialog.Builder builder = AlertDialogHelper.getBuilder(getContext(), "Delete " + address.getAddressName(), "If you delete this address the corresponding schedules will need to be updated.");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //the user has allowed the address to be deleted
                                    deleteAddress(address);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //do not delete the address
                                    dialogInterface.cancel();
                                }
                            }).create().show();
                        }
                    })//allow the user to update the name of their address
                    .setPositiveButton("Update Name", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Show a new alert dialog allowing the user to update the name of their address
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            //create a space for the user to enter a new address name
                            EditText addressName = new EditText(getContext());
                            addressName.setHint("eg: Home");
                            builder.setView(addressName);
                            //set the components of the dialog
                            builder.setTitle("Update Address Name");
                            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //check if the new name the user has provided has not been used
                                    boolean present = Address.checkAddressName(addressName.getText().toString());
                                    if(!present){
                                        //get the address from the database and update the name
                                        address.setAddressName(addressName.getText().toString());
                                        BaseActivity.CD.updateAddress(address);
                                        //display the change to the user
                                        viewAddress();
                                    }
                                }
                            }).create().show();
                        }
                    })
                    .create().show();
        }
    };

    /**
     * This method inflates the corresponding fragment and initialises views that will be used.
     * @param inflater The object that can be used to inflate all the view in the fragment.
     * @param container Parent of the UI is attached to.
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     * @return View
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_address, container, false);

        addressButton = root.findViewById(R.id.button_search);
        editText = root.findViewById(R.id.edit_address_name);
        listView = root.findViewById(R.id.list_address);

        return root;
    }

    /**
     * Sets listeners to corresponding views and display the user's addresses
     * @param view The view returned by onCreateView
     * @param savedInstanceState Allows fragment to be re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAddress();
            }
        });

        viewAddress();
    }

    /**
     * This method clears the EditText view and displays the user's addresses
     */
    @Override
    public void onStart() {
        super.onStart();
        if(editText != null){
            editText.getText().clear();
        }
        if(BaseActivity.CD.getAddresses().size() > 0 && listView != null){
            viewAddress();
        }
    }

    /**
     * This method allows the user to add an address if the name of the address provided is not already used.
     * It will start an activity allowing the user to search their desired address.
     */
    private void addAddress(){
        String addressName = editText.getText().toString();

        //check if the name has been used before or if the address has already been added
        boolean present = Address.checkAddressName(addressName);

        if(!present){
            //disable the error message
            TextView textView = (TextView) getView().findViewById(R.id.text_error_message);
            textView.setVisibility(View.INVISIBLE);

            //create a new intent
            Intent searchIntent = new Intent(getContext(), SearchAddressActivity.class);
            //pass the address name
            searchIntent.putExtra("addressName", addressName);
            startActivity(searchIntent);
        } else {
            //show error message
            TextView textView = (TextView) getView().findViewById(R.id.text_error_message);
            textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method displays all the addresses in a list view.
     */
    private void viewAddress(){
        listView.setAdapter(new ArrayAdapter<Address>(getContext(), android.R.layout.simple_expandable_list_item_1, BaseActivity.CD.getAddresses()));
        //set a listener for the view to allow the user to click an address and delete it
        listView.setOnItemClickListener(listener);
    }

    /**
     * This method loops through all the events and checks if any events have an association with
     * the address to be deleted. If so, a intent will be created. Afterwards the address will be deleted.
     * @param address The address to be deleted
     */
    private void deleteAddress(Address address){
        //delete the address the user has selected
        BaseActivity.CD.deleteAddress(address);
        //get all the events that may have the address just deleted
        List<Event> events = BaseActivity.CD.getEvents();
        Collections.sort(events);
        //go through all the events and check if the address to be delete is apart of the event
        for(int j = 0; j < events.size(); j++){
            if(events.get(j).getStartID() == address.getId()){
                Intent intent = setUpIntent(events.get(j), "updateStart", "endID", events.get(j).getEndID());
                intents.add(intent);
            }
            if(events.get(j).getEndID() == address.getId()){
                Intent intent = setUpIntent(events.get(j), "updateEnd", "startID", events.get(j).getStartID());
                intents.add(intent);
            }
        }
        //if there are events with the address to be deleted, make the user change their schedule
        if(intents.size() > 0){
            Intent intentArray[] = new Intent[intents.size()];
            intents.toArray(intentArray);
            //show a new screen to the user so they are able to change their schedule
            getContext().startActivities(intentArray);
        }
        viewAddress();
    }

    /**
     * This method creates a new intent with the specified fields passed in.
     * @param e The event that has to be updated
     * @param update The name of the field of which id will be changed, eg updateStart or updateEnd
     * @param id The name of the field, "endID" or "startID"
     * @param addressId The int id of the address that will change
     * @return Intent
     */
    private Intent setUpIntent(Event e, String update, String id, int addressId){
        //create a new intent to update the schedule
        Intent intent = new Intent(getContext(), DayActivity.class);
        //put all the event details
        intent.putExtra("day", e.getDayString());
        intent.putExtra("dayInt", e.getDayInt());
        intent.putExtra("hour", e.getStartHour());
        intent.putExtra("minute", e.getStartMinute());
        intent.putExtra("travelMethod", e.getTravelMethod());
        intent.putExtra("interval", e.getInterval());
        intent.putExtra("requestCode", e.getRequestCode());
        //signal which address is going to be updated
        intent.putExtra(update, true);
        intent.putExtra(id, addressId);

        return intent;
    }

}