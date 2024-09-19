package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarDto;
import com.software.modsen.drivermicroservice.entities.car.CarPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.DriverPatchDto;

import java.util.List;

public interface DriverService {
    Driver getDriverById(long id);
    List<Driver> getAllDrivers();
    Driver saveDriver(DriverDto driverDto);
    Driver updateDriver(long id, DriverDto driverDto);
    Driver patchDriver(long id, DriverPatchDto driverPatchDto);
    Driver softDeleteDriverById(long id);
}
