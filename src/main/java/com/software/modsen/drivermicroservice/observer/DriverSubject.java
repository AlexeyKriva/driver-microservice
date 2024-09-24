package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingDto;

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

    public void notifyDriverObservers(DriverRatingDto driverRatingDto) {
        for (DriverObserver passengerObserver: driverObservers) {
            passengerObserver.saveDriverRating(driverRatingDto);
        }
    }
}
