package com.software.modsen.drivermicroservice.services.listeners;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingMessage;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.mappers.DriverRatingMapper;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.DRIVER_NOT_FOUND_MESSAGE;
import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.DRIVER_WAS_DELETED_MESSAGE;

@Component
@AllArgsConstructor
public class KafkaMessageConsumer {
    private DriverRepository driverRepository;
    private DriverRatingRepository driverRatingRepository;
    private final DriverRatingMapper DRIVER_RATING_MAPPER = DriverRatingMapper.INSTANCE;

    @KafkaListener(topics = "driver-rating")
    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public DriverRating updateDriverRating(DriverRatingMessage driverRatingMessage) {
        Optional<Driver> driverFromDb = driverRepository.findById(driverRatingMessage.getDriverId());

        if (driverFromDb.isPresent()) {
            if (!driverFromDb.get().isDeleted()) {
                Optional<DriverRating> driverRatingFromDb = driverRatingRepository
                        .findByDriverId(driverRatingMessage.getDriverId());

                if (driverRatingFromDb.isPresent()) {
                    DriverRating updatingDriverRating = driverRatingFromDb.get();
                    DRIVER_RATING_MAPPER
                            .updateDriverRatingFromDriverRatingDto(driverRatingMessage, updatingDriverRating);

                    return driverRatingRepository.save(updatingDriverRating);
                }
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }
}
