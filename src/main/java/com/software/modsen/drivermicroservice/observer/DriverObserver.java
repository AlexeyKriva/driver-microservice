package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingDto;

public interface DriverObserver {
    void saveDriverRating(DriverRatingDto driverRatingDto);
}