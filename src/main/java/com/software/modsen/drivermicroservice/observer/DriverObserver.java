package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingMessage;

public interface DriverObserver {
    void saveDriverRating(DriverRatingMessage driverRatingMessage);
}