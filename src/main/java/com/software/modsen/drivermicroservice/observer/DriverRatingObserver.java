package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingDto;
import com.software.modsen.drivermicroservice.mappers.DriverRatingMapper;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DriverRatingObserver implements DriverObserver {
    private DriverRatingRepository driverRatingRepository;
    private final DriverRatingMapper Driver_RATING_MAPPER = DriverRatingMapper.INSTANCE;

    @Override
    public void saveDriverRating(DriverRatingDto driverRatingDto) {
        DriverRating newPassengerRating = Driver_RATING_MAPPER
                .fromDriverRatingDtoToDriverRating(driverRatingDto);
        driverRatingRepository.save(newPassengerRating);
    }
}