package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.DriverPatchDto;
import com.software.modsen.drivermicroservice.exceptions.CarNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.mappers.DriverMapper;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.*;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private CarRepository carRepository;
    private final DriverMapper DRIVER_MAPPER = DriverMapper.INSTANCE;

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
        return driverRepository.findAll().stream()
                .filter(driver -> !driver.isDeleted())
                .collect(Collectors.toList());
    }

    public Driver saveDriver(DriverDto driverDto) {
        Optional<Car> carFromDb = carRepository.findById(driverDto.getCarId());
        if (carFromDb.isPresent()) {
            Driver newDriver = DRIVER_MAPPER.fromDriverDtoToDriver(driverDto);
            newDriver.setCar(carFromDb.get());
            return driverRepository.save(newDriver);
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

    public Driver updateDriver(long id, DriverDto driverDto) {
        Optional<Car> carFromDb = carRepository.findById(driverDto.getCarId());
        if (carFromDb.isPresent()) {
            Optional<Driver> driverFromDb = driverRepository.findById(id);
            if (driverFromDb.isPresent()) {
                if (!driverFromDb.get().isDeleted()) {
                    Driver updatingDriver = DRIVER_MAPPER.fromDriverDtoToDriver(driverDto);
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

    public Driver patchDriver(long id, DriverPatchDto driverPatchDto) {
        Optional<Car> carFromDb = Optional.of(new Car());
        if (driverPatchDto.getCarId() != null) {
            carFromDb = carRepository.findById(driverPatchDto.getCarId());
        }
        if (driverPatchDto.getCarId() == null || (carFromDb.isPresent())) {
            Optional<Driver> driverFromDb = driverRepository.findById(id);
            if (driverFromDb.isPresent()) {
                if (!driverFromDb.get().isDeleted()) {
                    Driver updatingDriver = driverFromDb.get();
                    DRIVER_MAPPER.updateDriverFromDriverPatchDto(driverPatchDto, updatingDriver);
                    if (driverPatchDto.getCarId() != null) {
                        updatingDriver.setCar(carFromDb.get());
                    }

                    return driverRepository.save(updatingDriver);
                }

                throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
            }

            throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
        }

        throw new CarNotFoundException(CAR_NOT_FOUND_MESSAGE);
    }

    public Driver softDeleteDriverById(long id) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);
        return driverFromDb
                .filter(driver -> !driver.isDeleted())
                        .map(driver -> {
                            driver.setDeleted(true);
                            return driverRepository.save(driver);
                        })
                .orElseThrow(() -> new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE));
    }
}
