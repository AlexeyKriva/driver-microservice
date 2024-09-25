package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarDto;
import com.software.modsen.drivermicroservice.entities.car.CarPatchDto;
import com.software.modsen.drivermicroservice.exceptions.CarNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.CarWasDeletedException;
import com.software.modsen.drivermicroservice.mappers.CarMapper;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return carRepository.findAll().stream()
                .filter(car -> !car.isDeleted())
                .collect(Collectors.toList());
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public Car saveCar(CarDto carDto) {
        Car newCar = CAR_MAPPER.fromCarDtoToCar(carDto);

        return carRepository.save(newCar);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public Car updateCar(long id, CarDto carDto) {
        Optional<Car> carFromDb = carRepository.findById(id);

        if (carFromDb.isPresent()) {
            if (!carFromDb.get().isDeleted()) {
                Car updatingCar = CAR_MAPPER.fromCarDtoToCar(carDto);
                updatingCar.setId(id);

                return carRepository.save(updatingCar);
            }

            throw new CarWasDeletedException(CAR_WAS_DELETED_MESSAGE);
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public Car patchCar(long id, CarPatchDto carPatchDto) {
        Optional<Car> carFromDb = carRepository.findById(id);

        if (carFromDb.isPresent()) {
            if (!carFromDb.get().isDeleted()) {
                Car updatingCar = carFromDb.get();
                CAR_MAPPER.updateCarFromCarPatchDto(carPatchDto, updatingCar);

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

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForSaveAndPut(DataAccessException exception,
                                                                          CarDto carDto) {
        return new ResponseEntity<>(CANNOT_SAVE_CAR_MESSAGE + carDto.toString(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForPatch(DataAccessException exception,
                                                                     CarPatchDto carPatchDto) {
        return new ResponseEntity<>(CANNOT_PATCH_CAR_MESSAGE + carPatchDto.toString(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Recover
    public ResponseEntity<String> dataAccessExceptionRecoverForDelete(DataAccessException exception,
                                                                      long id) {
        return new ResponseEntity<>(CANNOT_DELETE_CAR_MESSAGE + id,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}