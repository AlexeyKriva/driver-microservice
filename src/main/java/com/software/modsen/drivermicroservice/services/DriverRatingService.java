package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.exceptions.DatabaseConnectionRefusedException;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverRatingNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.*;

@Service
@AllArgsConstructor
public class DriverRatingService {
    private DriverRatingRepository driverRatingRepository;
    private DriverRepository driverRepository;

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public List<DriverRating> getAllDriverRatings(boolean includeDeleted) {
        if (includeDeleted) {
            return driverRatingRepository.findAll();
        } else {
            List<DriverRating> driverRatingsFromDb = driverRatingRepository.findAll();
            List<DriverRating> driverRatingsAndNotDeleted = new ArrayList<>();

            for (DriverRating driverRatingFromDb : driverRatingsFromDb) {
                Optional<Driver> driverFromDb = driverRepository
                        .findDriverByIdAndIsDeleted(driverRatingFromDb.getDriver().getId(), false);

                if (driverFromDb.isPresent()) {
                    driverRatingsAndNotDeleted.add(driverRatingFromDb);
                }
            }

            if (driverRatingsAndNotDeleted.isEmpty()) {
                throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
            }

            return driverRatingsAndNotDeleted;
        }
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public DriverRating getDriverRatingById(long id) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        if (driverRatingFromDb.isPresent()) {
            return driverRatingFromDb.get();
        }

        throw new DriverNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public DriverRating getDriverRatingByDriverId(long driverId) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findByDriverId(driverId);

        if (driverRatingFromDb.isPresent()) {
            if (!driverRatingFromDb.get().getDriver().isDeleted()) {
                return driverRatingFromDb.get();
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public DriverRating putDriverRatingById(long id, DriverRating updatingDriverRating) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        if (driverRatingFromDb.isPresent()) {
            updatingDriverRating.setId(id);

            if (!driverRatingFromDb.get().getDriver().isDeleted()) {
                updatingDriverRating.setDriver(driverRatingFromDb.get().getDriver());
            } else {
                throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
            }

            return driverRatingRepository.save(updatingDriverRating);
        }

        throw new DriverRatingNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public DriverRating patchDriverRatingById(long id,
                                              DriverRating updatingDriverRating) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        if (driverRatingFromDb.isPresent()) {
            if (!driverRatingFromDb.get().getDriver().isDeleted()) {
                updatingDriverRating.setDriver(driverRatingFromDb.get().getDriver());
            } else {
                throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
            }

            if (updatingDriverRating.getRatingValue() == null) {
                updatingDriverRating.setRatingValue(driverRatingFromDb.get().getRatingValue());
            }

            if (updatingDriverRating.getNumberOfRatings() == null) {
                updatingDriverRating.setNumberOfRatings(driverRatingFromDb.get().getNumberOfRatings());
            }

            updatingDriverRating.setId(id);

            return driverRatingRepository.save(updatingDriverRating);
        }

        throw new DriverRatingNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE);
    }

    @Recover
    public DriverRating fallbackPostgresHandle(Throwable throwable) {
        if (throwable instanceof DataIntegrityViolationException) {
            throw (DataIntegrityViolationException) throwable;
        }

        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_UPDATE_DATA_MESSAGE);
    }

    @Recover
    public List<DriverRating> recoverToPSQLException(Throwable throwable) {
        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_GET_DATA_MESSAGE);
    }
}