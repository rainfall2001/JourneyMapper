package com.example.mapper.model;

/**
 * All the possible travel method that the user may use.
 * @author Rhane Mercado
 */
public enum TravelMethod {
    CAR(3000, 2000),
    ESCOOTER(3000, 2000),
    CARPOOL(3000, 2000),
    BIKE(3000, 2000),
    EBIKE(3000, 2000),
    BUS(3000, 2000),
    FERRY(3000, 2000),
    TRAIN(3000, 2000),
    WALK(3000, 2000),
    SCOOTER(3000, 2000),
    MOTORCYCLE(3000, 2000);

    private int interval;
    private int fastInterval;

    TravelMethod(int interval, int fastInterval) {
        this.interval  = interval;
        this.fastInterval = fastInterval;
    }

    //////////////////////////////////////////////
    //Getters
    /////////////////////////////////////////////

    public int getInterval() {
        return interval;
    }

    public int getFastInterval() {
        return fastInterval;
    }
}


