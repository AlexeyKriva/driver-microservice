package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingMessage;
import com.software.modsen.drivermicroservice.mappers.DriverRatingMapper;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class DriverRatingObserver implements DriverObserver {
    private DriverRatingRepository driverRatingRepository;
    private DriverRepository driverRepository;
    private final DriverRatingMapper Driver_RATING_MAPPER = DriverRatingMapper.INSTANCE;

    @Override
    public void saveDriverRating(DriverRatingMessage driverRatingMessage) {
        DriverRating newDriverRating = Driver_RATING_MAPPER
                .fromDriverRatingDtoToDriverRating(driverRatingMessage);
        Optional<Driver> driverFromDb = driverRepository.findById(driverRatingMessage.getDriverId());
        newDriverRating.setDriver(driverFromDb.get());
        newDriverRating.setNumberOfRatings(0);

        driverRatingRepository.save(newDriverRating);
    }
}