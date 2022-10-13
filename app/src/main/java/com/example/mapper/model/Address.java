package com.example.mapper.model;

import com.example.mapper.BaseActivity;

import java.util.List;

/**
 * This class is a blue print for the an Address object. To create an address the address name, physical address, latitude and longitude
 * of the address needs to be provided. The id of the address is set when it is added to the database.
 * @author Rhane Mercado
 */
public class Address {
    static String tag = "HelloWorld";
    private String addressName;
    private String addressInfo;
    private double latitude;
    private double longitude;
    private int id;

    /**
     * Create a new address
     * @param addressName The address name eg Home
     * @param addressInfo The physical address of the address
     * @param latitude The latitude of the address
     * @param longitude The longitude of the address
     */
    public Address(String addressName, String addressInfo, double latitude, double longitude) {
        this.addressName = addressName;
        this.addressInfo = addressInfo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * This method returns the address name and address info on a new line each.
     * @return String
     */
    @Override
    public String toString() {
        return addressName + ": "  + addressInfo;
    }

    /**
     * This method returns the address id, address name and address info on a new line each.
     * @return String
     */
    public String viewAddress(){
        return "Name: " + addressName + "\nInfo: " + addressInfo;
    }

    /**
     * This method checks if the address name provided has already been used
     * @param name The name of the address
     * @return boolean
     */
    public static boolean checkAddressName(String name){
        List<Address> addressList = BaseActivity.CD.getAddresses();
        for(int i = 0; i < addressList.size(); i++){
            if(addressList.get(i).addressName.compareToIgnoreCase(name) == 0){
                return true;
            }
        }
        return false;
    }

    //////////////////////////////////////////////
    //Getters and setters
    /////////////////////////////////////////////

    public String getAddressName() {
        return addressName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }
}
