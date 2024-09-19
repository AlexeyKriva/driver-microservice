package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarDto;
import com.software.modsen.drivermicroservice.entities.car.CarPatchDto;

import java.util.List;

public interface CarService {
    Car getCarById(long id);
    List<Car> getAllCars();
    Car saveCar(CarDto carDto);
    Car updateCar(long id, CarDto carDto);
    Car patchCar(long id, CarPatchDto carPatchDto);
    Car softDeleteCarById(long id);
}