package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarDto;
import com.software.modsen.drivermicroservice.entities.car.CarPatchDto;
import com.software.modsen.drivermicroservice.exceptions.CarNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.CarWasDeletedException;
import com.software.modsen.drivermicroservice.mappers.CarMapper;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.CAR_NOT_FOUND_MESSAGE;
import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.CAR_WAS_DELETED_MESSAGE;

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

    public Car saveCar(CarDto carDto) {
        Car newCar = CAR_MAPPER.fromCarDtoToCar(carDto);

        return carRepository.save(newCar);
    }

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

    public Car softDeleteCarById(long id) {
        Optional<Car> carFromDb = carRepository.findById(id);

        return carFromDb
                .map(car -> {
                    car.setDeleted(true);
                    return carRepository.save(car);
                })
                .orElseThrow(() -> new CarNotFoundException(CAR_NOT_FOUND_MESSAGE));
    }
}