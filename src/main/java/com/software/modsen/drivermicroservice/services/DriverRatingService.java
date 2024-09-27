package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPutDto;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverRatingNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.mappers.DriverRatingMapper;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
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
    private final DriverRatingMapper DRIVER_RATING_MAPPER = DriverRatingMapper.INSTANCE;

    public List<DriverRating> getAllDriverRatings() {
        return driverRatingRepository.findAll();
    }

    public List<DriverRating> getAllDriverRatingsAndNotDeleted() {
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


    public DriverRating getDriverRatingById(long driverId) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findByDriverId(driverId);

        if (driverRatingFromDb.isPresent()) {
            return driverRatingFromDb.get();
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

    @KafkaListener(topics = "driver-rating", groupId = "driver-group")
    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public DriverRating updateDriverRating(DriverRatingDto driverRatingDto) {
        Optional<Driver> driverFromDb = driverRepository.findById(driverRatingDto.getDriverId());

        if (driverFromDb.isPresent()) {
            if (!driverFromDb.get().isDeleted()) {
                Optional<DriverRating> driverRatingFromDb = driverRatingRepository
                        .findByDriverId(driverRatingDto.getDriverId());

                if (driverRatingFromDb.isPresent()) {
                    DriverRating updatingDriverRating = driverRatingFromDb.get();
                    DRIVER_RATING_MAPPER
                            .updateDriverRatingFromDriverRatingDto(driverRatingDto, updatingDriverRating);

                    return driverRatingRepository.save(updatingDriverRating);
                }
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public DriverRating putDriverRatingById(long id, DriverRatingPutDto driverRatingPutDto) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        if (driverRatingFromDb.isPresent()) {
            DriverRating updatingDriverRating = DRIVER_RATING_MAPPER
                    .fromDriverRatingPutDtoToDriverRating(driverRatingPutDto);
            updatingDriverRating.setId(id);

            Optional<Driver> driverFromDb = driverRepository.findById(driverRatingFromDb.get().getId());

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
    public DriverRating patchDriverRatingById(long id, DriverRatingPatchDto driverRatingPatchDto) {
        Optional<DriverRating> driverRatingFromDb = driverRatingRepository.findById(id);

        if (driverRatingFromDb.isPresent()) {
            DriverRating updatingDriverRating = driverRatingFromDb.get();
            DRIVER_RATING_MAPPER.updateDriverRatingFromDriverRatingPatchDto(driverRatingPatchDto,
                    updatingDriverRating);

            if (driverRatingPatchDto.getDriverId() != null) {
                Optional<Driver> driverFromDb = driverRepository
                        .findById(driverRatingPatchDto.getDriverId());

                if (driverFromDb.isPresent()) {
                    if (!driverFromDb.get().isDeleted()) {
                        updatingDriverRating.setDriver(driverFromDb.get());
                    } else {
                        throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
                    }
                } else {
                    throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
                }
            }

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

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForUpdate(DataAccessException exception,
                                                                      DriverRatingDto driverRatingDto) {
        return new ResponseEntity<>(CANNOT_UPDATE_DRIVER_RATING_MESSAGE + driverRatingDto.toString(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForPut(DataAccessException exception,
                                                                   DriverRatingPutDto driverRatingPutDto) {
        return new ResponseEntity<>(CANNOT_PUT_DRIVER_RATING_MESSAGE + driverRatingPutDto.toString(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForPatch(DataAccessException exception,
                                                                     DriverRatingPatchDto driverRatingPatchDto) {
        return new ResponseEntity<>(CANNOT_PATCH_DRIVER_RATING_MESSAGE + driverRatingPatchDto.toString(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForDelete(DataAccessException exception,
                                                                      long id) {
        return new ResponseEntity<>(CANNOT_DELETE_DRIVER_RATING_MESSAGE + id,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}