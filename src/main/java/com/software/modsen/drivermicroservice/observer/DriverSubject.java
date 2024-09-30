package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingMessage;

import java.util.ArrayList;
import java.util.List;

public class DriverSubject {
    private List<DriverObserver> driverObservers = new ArrayList<>();

    public void addDriverObserver(DriverObserver driverObserver) {
        driverObservers.add(driverObserver);
    }

    public void removeDriverObserver(DriverObserver driverObserver) {
        driverObservers.remove(driverObserver);
    }

    public void notifyDriverObservers(DriverRatingMessage driverRatingMessage) {
        for (DriverObserver passengerObserver: driverObservers) {
            passengerObserver.saveDriverRating(driverRatingMessage);
        }
    }
}