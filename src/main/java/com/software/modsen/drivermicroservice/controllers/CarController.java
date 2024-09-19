package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarDto;
import com.software.modsen.drivermicroservice.entities.car.CarPatchDto;
import com.software.modsen.drivermicroservice.services.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/car", produces = "application/json")
public class CarController {
    @Autowired
    private CarService carService;

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable("id") long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<Car> saveCar(@Valid
                                       @RequestBody CarDto carDto) {
        return ResponseEntity.ok(carService.saveCar(carDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCarById(@PathVariable("id") long id,
            @Valid @RequestBody CarDto carDto) {
        return ResponseEntity.ok(carService.updateCar(id, carDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Car> patchCarById(@PathVariable("id") long id,
                                            @Valid @RequestBody CarPatchDto carPatchDto) {
        return ResponseEntity.ok(carService.patchCar(id, carPatchDto));
    }

    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<Car> softDeleteCarByUd(@PathVariable("id") long id) {
        return ResponseEntity.ok(carService.softDeleteCarById(id));
    }
}
