package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverRatingNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
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

    public List<DriverRating> getAllDriverRatings() {
        return driverRatingRepository.findAll();
    }

    public List<DriverRating> getAllNotDeletedDriverRatings() {
        List<DriverRating> driverRatingsFromDb = driverRatingRepository.findAll();
        List<DriverRating> driverRatingsAndNotDeleted = new ArrayList<>();

        for (DriverRating driverRatingFromDb: driverRatingsFromDb) {
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

    public DriverRating getDriverRatingById(long id) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        if (driverRatingFromDb.isPresent()) {
            Optional<Driver> driverFromDb = driverRepository.findDriverByIdAndIsDeleted(
                    driverRatingFromDb.get().getDriver().getId(), false);

            if (driverFromDb.isPresent()) {
                return driverRatingFromDb.get();
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE);
    }

    public DriverRating getDriverRatingByDriverId(long driverId) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findByDriverId(driverId);

        if (driverRatingFromDb.isPresent()) {
            Optional<Driver> driverFromDb = driverRepository.findDriverByIdAndIsDeleted(
                    driverId, false);

            if (driverFromDb.isPresent()) {
                return driverRatingFromDb.get();
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE);
    }

    public DriverRating getDriverRatingByIdAndNotDeleted(long driverId) {
        Optional<Driver> driverFromDb = driverRepository
                .findDriverByIdAndIsDeleted(driverId, false);

        if (driverFromDb.isPresent()) {
            Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findByDriverId(driverId);
            return driverRatingFromDb.get();
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public DriverRating putDriverRatingById(long id, DriverRating updatingDriverRating) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        if (driverRatingFromDb.isPresent()) {
            updatingDriverRating.setId(id);

            Optional<Driver> driverFromDb = driverRepository.findById(driverRatingFromDb.get().getDriver().getId());

            if (!driverFromDb.get().isDeleted()) {
                updatingDriverRating.setDriver(driverFromDb.get());
            } else {
                throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
            }

            return driverRatingRepository.save(updatingDriverRating);
        }

        throw new DriverRatingNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public DriverRating patchDriverRatingById(long id, Long driverId,
                                              DriverRating updatingDriverRating) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        if (driverRatingFromDb.isPresent()) {
            Optional<Driver> driverFromDb;

            if (driverId == null) {
                driverFromDb = driverRepository.findById(
                        driverRatingFromDb.get().getDriver().getId());
            } else {
                driverFromDb = driverRepository.findById(driverId);

                if (driverFromDb.isEmpty()) {
                    throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
                }
            }

            if (!driverFromDb.get().isDeleted()) {
                updatingDriverRating.setDriver(driverFromDb.get());
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

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public void deleteDriverRatingById(long id) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        driverRatingFromDb.ifPresentOrElse(
                driverRating -> driverRatingRepository.deleteById(id),
                () -> {throw new DriverRatingNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE);}
        );
    }
}