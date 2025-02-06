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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.*;

@Service
@AllArgsConstructor
public class DriverService {
    private DriverRepository driverRepository;
    private CarRepository carRepository;
    private DriverSubject driverSubject;
    private RedisService redisService;

    private static final int TTL = 10;

    public List<Driver> getAllDrivers(boolean includeDeleted, String name) {
        if (name != null) {
            return List.of(getDriverByName(name));
        } else if (includeDeleted) {
            return driverRepository.findAll();
        } else {
            return driverRepository.findAll().stream()
                    .filter(driver -> !driver.isDeleted())
                    .collect(Collectors.toList());
        }
    }

    public Driver getDriverByName(String name) {
        Object cachedDriver = redisService.getFromCache("driver:" + name);

        if (cachedDriver != null) {
            return (Driver) cachedDriver;
        }

        Optional<Driver> driverFromDb = driverRepository.findByName(name);

        if (driverFromDb.isPresent()) {
            redisService.saveToCache("driver:" + name, driverFromDb.get(), TTL, TimeUnit.MINUTES);

            return driverFromDb.get();
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Transactional
    public Driver getDriverById(long id) {
        Object cachedDriver = redisService.getFromCache("driver:" + id);

        if (cachedDriver != null) {
            return (Driver) cachedDriver;
        }

        Optional<Driver> driverFromDb = driverRepository.findById(id);

        if (driverFromDb.isPresent()) {
            redisService.saveToCache("driver:" + id, driverFromDb.get(), TTL, TimeUnit.MINUTES);

            return driverFromDb.get();
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Transactional
    public Driver saveDriver(Long carId, Driver newDriver) {
        Optional<Car> carFromDb = carRepository.findById(carId);

        if (carFromDb.isPresent()) {

            newDriver.setCar(carFromDb.get());
            Driver driverFromDb = driverRepository.save(newDriver);

            driverSubject.notifyDriverObservers(driverFromDb.getId());

            redisService.saveToCache("driver:" + driverFromDb.getId(), driverFromDb, TTL, TimeUnit.MINUTES);
            redisService.saveToCache("driver:" + driverFromDb.getName(), driverFromDb, TTL, TimeUnit.MINUTES);

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
                redisService.invalidateCache("driver:" + id);

                if (!driverFromDb.get().isDeleted()) {
                    updatingDriver.setId(id);
                    updatingDriver.setCar(carFromDb.get());

                    redisService.saveToCache("driver:" + id, updatingDriver, TTL, TimeUnit.MINUTES);

                    return driverRepository.save(updatingDriver);
                }

                throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
            }

            throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

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

                redisService.invalidateCache("driver:" + id);
                redisService.saveToCache("driver:" + id, updatingDriver, TTL, TimeUnit.MINUTES);

                return driverRepository.save(updatingDriver);
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Transactional
    public Driver softDeleteDriverById(long id) {
        redisService.invalidateCache("driver:" + id);

        Optional<Driver> driverFromDb = driverRepository.findById(id);

        return driverFromDb
                .map(driver -> {
                    driver.setDeleted(true);
                    return driverRepository.save(driver);
                })
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE));
    }

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