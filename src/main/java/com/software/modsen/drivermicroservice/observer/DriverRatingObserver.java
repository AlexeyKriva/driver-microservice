package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.exceptions.DatabaseConnectionRefusedException;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Recover;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.BAD_CONNECTION_TO_DATABASE_MESSAGE;
import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.CANNOT_UPDATE_DATA_MESSAGE;

@AllArgsConstructor
public class DriverRatingObserver implements DriverObserver {
    private DriverRatingRepository driverRatingRepository;
    private DriverRepository driverRepository;

    @Override
    @Transactional
    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    public void updateDriverInfo(long driverId) {
        Optional<Driver> driverFromDb = driverRepository.findById(driverId);

        DriverRating newDriverRating = new DriverRating();

        newDriverRating.setDriver(driverFromDb.get());
        newDriverRating.setNumberOfRatings(0);
        newDriverRating.setRatingValue(0.0f);

        driverRatingRepository.save(newDriverRating);
    }

    @Recover
    public void fallbackPostgresHandle(Throwable throwable) {
        if (throwable instanceof DataIntegrityViolationException) {
            throw (DataIntegrityViolationException) throwable;
        }

        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_UPDATE_DATA_MESSAGE);
    }
}