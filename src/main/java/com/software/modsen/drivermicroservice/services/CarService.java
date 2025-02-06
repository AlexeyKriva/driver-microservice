package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.exceptions.CarNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.CarWasDeletedException;
import com.software.modsen.drivermicroservice.exceptions.DatabaseConnectionRefusedException;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
public class CarService {
    private CarRepository carRepository;

    @Cacheable(value = "CarService::getCarById", key = "#id")
    public Car getCarById(long id) {
        Optional<Car> carFromDb = carRepository.findById(id);

        if (carFromDb.isPresent()) {
            if (!carFromDb.get().isDeleted()) {
                return carFromDb.get();
            }

            throw new CarWasDeletedException(CAR_WAS_DELETED_MESSAGE);
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

    public List<Car> getAllCars(boolean includeDeleted) {
        if (includeDeleted) {
            return carRepository.findAll();
        } else {
            return carRepository.findAll().stream()
                    .filter(car -> !car.isDeleted())
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @CachePut(value = "CarService::getCarById", key = "#newCar.id")
    public Car saveCar(Car newCar) {
        return carRepository.save(newCar);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    @CachePut(value = "CarService::getCarById", key = "#id")
    public Car updateCar(long id, Car updatingCar) {
        Optional<Car> carFromDb = carRepository.findById(id);

        if (carFromDb.isPresent()) {
            if (!carFromDb.get().isDeleted()) {
                updatingCar.setId(id);

                return carRepository.save(updatingCar);
            }

            throw new CarWasDeletedException(CAR_WAS_DELETED_MESSAGE);
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

    @Transactional
    @CachePut(value = "CarService::getCarById", key = "#id")
    public Car patchCar(long id, Car updatingCar) {
        Optional<Car> carFromDb = carRepository.findById(id);

        if (carFromDb.isPresent()) {
            if (!carFromDb.get().isDeleted()) {
                if (updatingCar.getColor() == null) {
                    updatingCar.setColor(carFromDb.get().getColor());
                }
                if (updatingCar.getBrand() == null) {
                    updatingCar.setBrand(carFromDb.get().getBrand());
                }
                if (updatingCar.getCarNumber() == null) {
                    updatingCar.setCarNumber(carFromDb.get().getCarNumber());
                }
                updatingCar.setId(id);

                return carRepository.save(updatingCar);
            }

            throw new CarWasDeletedException(CAR_WAS_DELETED_MESSAGE);
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

    @Transactional
    @CacheEvict(value = "CarService::getCarById", key = "#id")
    public Car softDeleteCarById(long id) {
        Optional<Car> carFromDb = carRepository.findById(id);

        return carFromDb
                .map(car -> {
                    car.setDeleted(true);
                    return carRepository.save(car);
                })
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND_MESSAGE));
    }

    @Transactional
    public Car softRecoveryCarById(long id) {
        Optional<Car> carFromDb = carRepository.findById(id);

        return carFromDb
                .map(car -> {
                    car.setDeleted(false);
                    return carRepository.save(car);
                })
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND_MESSAGE));
    }

    @Recover
    public Car fallbackPostgresHandle(Throwable throwable) {
        if (throwable instanceof DataIntegrityViolationException) {
            throw (DataIntegrityViolationException) throwable;
        }

        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_UPDATE_DATA_MESSAGE);
    }

    @Recover
    public List<Car> recoverToPSQLException(Throwable throwable) {
        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_GET_DATA_MESSAGE);
    }
}