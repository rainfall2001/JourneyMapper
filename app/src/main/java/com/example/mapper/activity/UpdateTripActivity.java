package com.example.mapper.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapper.helper.AlertDialogHelper;
import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.model.Address;
import com.example.mapper.model.Trip;

/**
 * This class controls the functionality of the activity_update_trip layout. This class allows either the start or end address
 * of a trip to be updated. The address may be already be stored or can be searched by the user.
 * @author Rhane Mercado
 */
public class UpdateTripActivity extends AppCompatActivity {

    private TextView textTitle;
    private EditText editAddress;
    private int position, id;
    private ArrayAdapter<Address> adapter;
    private Trip trip;

    /**
     * This method creates the layout of the activity and gets the intent extras.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_trip);

        //get the extras passed in
        Bundle bundle = getIntent().getExtras();
        String location = bundle.getString("location");
        position = bundle.getInt("position", -1);
        id = bundle.getInt("id", -1);

        //get the elements which is needed
        textTitle = (TextView) findViewById(R.id.text_trip_update);
        editAddress = (EditText) findViewById(R.id.edit_address_trip);
        adapter = new ArrayAdapter<Address>(getApplicationContext(), android.R.layout.simple_list_item_1, BaseActivity.CD.getAddresses());
        //show the user what address they are updating
        textTitle.setText("Update " + location + " location");

    }

    /**
     * This method gets a new ArrayAdapter to show the new address just added
     */
    @Override
    protected void onStart() {
        super.onStart();
        adapter = new ArrayAdapter<Address>(getApplicationContext(), android.R.layout.simple_list_item_1, BaseActivity.CD.getAddresses());
    }

    /**
     * onClick method for the EditText allow the user to select their intent address
     * @param view EditText
     */
    public void updateAddress(View view){
        //get a new dialog which will be shown to the user when the edit text is selected
        AlertDialog.Builder builder = AlertDialogHelper.getAddressDialog(this);
        builder.setTitle("Select Start Address")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //get the id of the address selected
                        trip = BaseActivity.CD.getTrip(id);
                        //check which address must be edited
                        if(position == 0){
                            trip.setStartLocation(BaseActivity.CD.getAddresses().get(i).getAddressName());
                            editAddress.setText(trip.getStartLocation());
                        } else {
                            trip.setEndLocation(BaseActivity.CD.getAddresses().get(i).getAddressName());
                            editAddress.setText(trip.getEndLocation());
                        }
                    }
                }).create().show();
    }

    /**
     * onClick method for the button. Once it is clicked the trip will be updated and a new activity, BaseActivity,
     * will be started.
     * @param view Button
     */
    public void setAddress(View view){
        if(trip == null){
            Toast.makeText(this, "Please select an address.", Toast.LENGTH_SHORT).show();
        } else {
            BaseActivity.CD.updateTrip(trip);

            Intent intent = new Intent(this, BaseActivity.class);
            //do not allow the user to go back to this activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}