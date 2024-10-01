package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingMessage;
import com.software.modsen.drivermicroservice.exceptions.CarNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.CarWasDeletedException;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.observer.DriverSubject;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
import com.software.modsen.drivermicroservice.repositories.DriverAccountRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
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
    private DriverAccountRepository driverAccountRepository;

    public Driver getDriverById(long id) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);

        if (driverFromDb.isPresent()) {
            if (!driverFromDb.get().isDeleted()) {
                return driverFromDb.get();
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Driver> getAllNotDeletedDrivers() {
        return driverRepository.findAll().stream()
                .filter(driver -> !driver.isDeleted())
                .collect(Collectors.toList());
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
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

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
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

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
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

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
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

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public Driver softRecoveryDriverById(long id) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);

        if (driverFromDb.isPresent()) {
            Optional<Car> carFromDb = carRepository.findCarByIdAndIsDeleted(
                    driverFromDb.get().getCar().getId(), false);

            if (carFromDb.isPresent()) {
                Driver recoveringDriver = driverFromDb.get();
                recoveringDriver.setDeleted(false);

                return driverRepository.save(recoveringDriver);
            }

            throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }
}