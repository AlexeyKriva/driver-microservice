package com.software.modsen.drivermicroservice.observer;

import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void notifyDriverObservers(long driverId) {
        for (DriverObserver passengerObserver: driverObservers) {
            passengerObserver.updateDriverInfo(driverId);
        }
    }
}