package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.exceptions.*;
import com.software.modsen.drivermicroservice.observer.DriverSubject;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.*;

@Service
@AllArgsConstructor
public class DriverService {
    private DriverRepository driverRepository;
    private CarRepository carRepository;
    private DriverSubject driverSubject;

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public Driver getDriverById(long id) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);

        if (driverFromDb.isPresent()) {
            return driverFromDb.get();
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public List<Driver> getAllNotDeletedDrivers() {
        return driverRepository.findAll().stream()
                .filter(driver -> !driver.isDeleted())
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public Driver saveDriver(Long carId, Driver newDriver) {
        Optional<Car> carFromDb = carRepository.findById(carId);

        if (carFromDb.isPresent()) {
            newDriver.setCar(carFromDb.get());
            Driver driverFromDb = driverRepository.save(newDriver);

            driverSubject.notifyDriverObservers(driverFromDb.getId());

            return driverFromDb;
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public Driver updateDriver(long id, Long carId, Driver updatingDriver) {
        Optional<Car> carFromDb = carRepository.findById(carId);

        if (carFromDb.isPresent()) {
            Optional<Driver> driverFromDb = driverRepository.findById(id);

            if (driverFromDb.isPresent()) {
                if (!driverFromDb.get().isDeleted()) {
                    updatingDriver.setId(id);
                    updatingDriver.setCar(carFromDb.get());

                    return driverRepository.save(updatingDriver);
                }

                throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
            }

            throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public Driver patchDriver(long id, Long carId, Driver updatingDriver) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);

        if (driverFromDb.isPresent()) {
            if (!driverFromDb.get().isDeleted()) {
                Optional<Car> carFromDb;
                if (carId == null) {
                    carFromDb = carRepository.findById(
                            driverFromDb.get().getCar().getId());
                } else {
                    carFromDb = carRepository.findById(carId);

                    if (carFromDb.isEmpty()) {
                        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
                    }
                }

                if (!carFromDb.get().isDeleted()) {
                    updatingDriver.setCar(carFromDb.get());
                } else {
                    throw new CarWasDeletedException(CAR_WAS_DELETED_MESSAGE);
                }

                if (updatingDriver.getName() == null) {
                    updatingDriver.setName(driverFromDb.get().getName());
                }
                if (updatingDriver.getEmail() == null) {
                    updatingDriver.setEmail(driverFromDb.get().getEmail());
                }
                if (updatingDriver.getPhoneNumber() == null) {
                    updatingDriver.setPhoneNumber(driverFromDb.get().getPhoneNumber());
                }
                if (updatingDriver.getSex() == null) {
                    updatingDriver.setSex(driverFromDb.get().getSex());
                }

                updatingDriver.setId(id);

                return driverRepository.save(updatingDriver);
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public Driver softDeleteDriverById(long id) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);

        return driverFromDb
                .map(driver -> {
                    driver.setDeleted(true);
                    return driverRepository.save(driver);
                })
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE));
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public Driver softRecoveryDriverById(long id) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);

        if (driverFromDb.isPresent()) {
            Driver recoveringDriver = driverFromDb.get();
            recoveringDriver.setDeleted(false);

            return driverRepository.save(recoveringDriver);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Recover
    public Driver fallbackPostgresHandle(Throwable throwable) {
        if (throwable instanceof DataIntegrityViolationException) {
            throw (DataIntegrityViolationException) throwable;
        }

        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_UPDATE_DATA_MESSAGE);
    }

    @Recover
    public List<Driver> recoverToPSQLException(Throwable throwable) {
        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_GET_DATA_MESSAGE);
    }
}