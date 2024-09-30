package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.exceptions.CarNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.CarWasDeletedException;
import com.software.modsen.drivermicroservice.mappers.CarMapper;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
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
public class CarService {
    private CarRepository carRepository;
    private final CarMapper CAR_MAPPER = CarMapper.INSTANCE;

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

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> getAllNotDeletedCars() {
        return carRepository.findAll().stream()
                .filter(car -> !car.isDeleted())
                .collect(Collectors.toList());
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public Car saveCar(Car newCar) {
        return carRepository.save(newCar);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
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

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
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

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public Car softDeleteCarById(long id) {
        Optional<Car> carFromDb = carRepository.findById(id);

        return carFromDb
                .map(car -> {
                    car.setDeleted(true);
                    return carRepository.save(car);
                })
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND_MESSAGE));
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
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
}