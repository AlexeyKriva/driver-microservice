package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.DriverPatchDto;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.mappers.DriverMapper;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.*;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverRepository driverRepository;
    private final DriverMapper DRIVER_MAPPER = DriverMapper.INSTANCE;
    @Override
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

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll().stream()
                .filter(driver -> !driver.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Driver saveDriver(DriverDto driverDto) {
        Driver newDriver = DRIVER_MAPPER.fromDriverDtoToDriver(driverDto);
        return driverRepository.save(newDriver);
    }

    @Override
    public Driver updateDriver(long id, DriverDto driverDto) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);
        if (driverFromDb.isPresent()) {
            if (!driverFromDb.get().isDeleted()) {
                Driver updatingDriver = DRIVER_MAPPER.fromDriverDtoToDriver(driverDto);
                updatingDriver.setId(id);

                return updatingDriver;
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Override
    public Driver patchDriver(long id, DriverPatchDto driverPatchDto) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);
        if (driverFromDb.isPresent()) {
            if (!driverFromDb.get().isDeleted()) {
                Driver updatingDriver = driverFromDb.get();
                DRIVER_MAPPER.updateDriverFromDriverPatchDto(driverPatchDto, updatingDriver);

                return driverRepository.save(updatingDriver);
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Override
    public Driver softDeleteDriverById(long id) {
        Optional<Driver> driverFromDb = driverRepository.findById(id);
        if (driverFromDb.isPresent()) {
            if (!driverFromDb.get().isDeleted()) {
                Driver deletingDriver = driverFromDb.get();
                deletingDriver.setDeleted(true);

                return driverRepository.save(deletingDriver);
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }
}
