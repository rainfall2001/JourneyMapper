package com.example.mapper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mapper.BaseActivity;
import com.example.mapper.R;
import com.example.mapper.model.Address;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

import com.example.mapper.model.CommuteDatabase;

/**
 * From https://developers.google.com/maps/documentation/places/android-sdk/autocomplete#option_1_embed_an_autocompletesupportfragment
 * This class implements the autocomplete search bar by Google.
 * @author Rhane Mercado
 */
public class SearchAddressActivity extends AppCompatActivity {

    private String addressName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);

        //get the address name that the user has provided
        Intent intent = getIntent();
        addressName = intent.getStringExtra("addressName");

        if(addressName != null){
            searchAddress();
        }
    }

    /**
     * This method sets up the autocomplete search bar. The specified country is New Zealand.
     * There are two return values, the coordinate of the address searched and the physical address.
     */
    private void searchAddress(){
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setCountries("NZ");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS))
                .setCountries("NZ");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                double lat = place.getLatLng().latitude;
                double lon = place.getLatLng().longitude;
                String addressInfo = place.getAddress();

                Address address = new Address(addressName, addressInfo, lat, lon);
                BaseActivity.CD.addAddress(address);
                //go back to the previous
                finish();
            }

            @Override
            public void onError(@NonNull Status status) {
                //Signal to the user that there was an error when searching their address.
                Toast.makeText(getApplicationContext(), R.string.google_auto_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}