package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
public class DriverRatingObserver implements DriverObserver {
    private DriverRatingRepository driverRatingRepository;
    private DriverRepository driverRepository;

    @Override
    @Transactional
    public void updateDriverInfo(long driverId) {
        Optional<Driver> driverFromDb = driverRepository.findById(driverId);

        DriverRating newDriverRating = new DriverRating();

        newDriverRating.setDriver(driverFromDb.get());
        newDriverRating.setNumberOfRatings(0);
        newDriverRating.setRatingValue(0.0f);

        driverRatingRepository.save(newDriverRating);
    }
}