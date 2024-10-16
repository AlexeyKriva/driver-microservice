package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarDto;
import com.software.modsen.drivermicroservice.entities.car.CarPatchDto;
import com.software.modsen.drivermicroservice.mappers.CarMapper;
import com.software.modsen.drivermicroservice.services.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/car", produces = "application/json")
@AllArgsConstructor
@Tag(name = "Car controller.", description = "Allows to interact with cars.")
public class CarController {
    private CarService carService;
    private final CarMapper CAR_MAPPER = CarMapper.INSTANCE;

    @GetMapping
    @Operation(
            description = "Allows to get all cars."
    )
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/not-deleted")
    @Operation(
            description = "Allows to get all not deleted cars."
    )
    public ResponseEntity<List<Car>> getAllNotDeletedCars() {
        return ResponseEntity.ok(carService.getAllNotDeletedCars());
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Allows to get car by id."
    )
    public ResponseEntity<Car> getCarById(@PathVariable("id") @Parameter(description = "Car id.") long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    @Operation(
            description = "Allows to save new car."
    )
    public ResponseEntity<Car> saveCar(@Valid
                                       @RequestBody @Parameter(description = "Car entity.")
                                           CarDto carDto) {
        return ResponseEntity.ok(carService.saveCar(CAR_MAPPER.fromCarDtoToCar(carDto)));
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Allows to update car."
    )
    public ResponseEntity<Car> updateCarById(@PathVariable("id") @Parameter(description = "Car id.") long id,
            @Valid @RequestBody @Parameter(description = "Car entity.") CarDto carDto) {
        return ResponseEntity.ok(carService.updateCar(id, CAR_MAPPER.fromCarDtoToCar(carDto)));
    }

    @PatchMapping("/{id}")
    @Operation(
            description = "Allows to update car."
    )
    public ResponseEntity<Car> patchCarById(@PathVariable("id") @Parameter(description = "Car id.")
                                                long id,
                                            @Valid @RequestBody @Parameter(description = "Car entity.")
                                            CarPatchDto carPatchDto) {
        return ResponseEntity.ok(carService.patchCar(id, CAR_MAPPER.fromCarPatchDtoToCar(carPatchDto)));
    }

    @PostMapping("/{id}/soft-delete")
    @Operation(
            description = "Allows to soft delete car."
    )
    public ResponseEntity<Car> softDeleteCarById(@PathVariable("id") @Parameter(description = "Car id.") long id) {
        return ResponseEntity.ok(carService.softDeleteCarById(id));
    }

    @PostMapping("/{id}/soft-recovery")
    @Operation(
            description = "Allows to soft recovery car."
    )
    public ResponseEntity<Car> softRecoveryCarById(@PathVariable("id") @Parameter(description = "Car id.") long id) {
        return ResponseEntity.ok(carService.softRecoveryCarById(id));
    }
}