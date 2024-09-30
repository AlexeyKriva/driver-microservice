package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarDto;
import com.software.modsen.drivermicroservice.entities.car.CarPatchDto;
import com.software.modsen.drivermicroservice.mappers.CarMapper;
import com.software.modsen.drivermicroservice.services.CarService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/car", produces = "application/json")
@AllArgsConstructor
public class CarController {
    private CarService carService;
    private final CarMapper CAR_MAPPER = CarMapper.INSTANCE;

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/not-deleted")
    public ResponseEntity<List<Car>> getAllNotDeletedCars() {
        return ResponseEntity.ok(carService.getAllNotDeletedCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable("id") long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<Car> saveCar(@Valid
                                       @RequestBody CarDto carDto) {
        return ResponseEntity.ok(carService.saveCar(CAR_MAPPER.fromCarDtoToCar(carDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCarById(@PathVariable("id") long id,
            @Valid @RequestBody CarDto carDto) {
        return ResponseEntity.ok(carService.updateCar(id, CAR_MAPPER.fromCarDtoToCar(carDto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Car> patchCarById(@PathVariable("id") long id,
                                            @Valid @RequestBody CarPatchDto carPatchDto) {
        return ResponseEntity.ok(carService.patchCar(id, CAR_MAPPER.fromCarPatchDtoToCar(carPatchDto)));
    }

    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<Car> softDeleteCarByUd(@PathVariable("id") long id) {
        return ResponseEntity.ok(carService.softDeleteCarById(id));
    }

    @PatchMapping("/{id}/soft-recovery")
    public ResponseEntity<Car> softRecoveryCarByUd(@PathVariable("id") long id) {
        return ResponseEntity.ok(carService.softRecoveryCarById(id));
    }
}