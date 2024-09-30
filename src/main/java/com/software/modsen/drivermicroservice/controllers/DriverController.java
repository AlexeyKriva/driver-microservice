package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.DriverPatchDto;
import com.software.modsen.drivermicroservice.mappers.DriverMapper;
import com.software.modsen.drivermicroservice.services.DriverService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/driver", produces = "application/json")
@AllArgsConstructor
public class DriverController {
    private DriverService driverService;
    private final DriverMapper DRIVER_MAPPER = DriverMapper.INSTANCE;

    @GetMapping
    public ResponseEntity<List<Driver>> getAllDriver() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/not-deleted")
    public ResponseEntity<List<Driver>> getAllNotDeletedDriver() {
        return ResponseEntity.ok(driverService.getAllNotDeletedDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable("id") long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PostMapping
    public ResponseEntity<Driver> saveDriver(@Valid
                                       @RequestBody DriverDto driverDto) {
        return ResponseEntity.ok(driverService.saveDriver(
                driverDto.getCarId(),
                DRIVER_MAPPER.fromDriverDtoToDriver(driverDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriverById(@PathVariable("id") long id,
                                             @Valid @RequestBody DriverDto driverDto) {
        return ResponseEntity.ok(driverService.updateDriver(
                id,
                driverDto.getCarId(),
                DRIVER_MAPPER.fromDriverDtoToDriver(driverDto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Driver> patchDriverById(@PathVariable("id") long id,
                                            @Valid @RequestBody DriverPatchDto driverPatchDto) {
        return ResponseEntity.ok(driverService.patchDriver(
                id,
                driverPatchDto.getCarId(),
                DRIVER_MAPPER.fromDriverPatchDtoToDriver(driverPatchDto)));
    }

    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<Driver> softDeleteDriverByUd(@PathVariable("id") long id) {
        return ResponseEntity.ok(driverService.softDeleteDriverById(id));
    }

    @PatchMapping("/{id}/soft-recovery")
    public ResponseEntity<Driver> softRecoveryDriverByUd(@PathVariable("id") long id) {
        return ResponseEntity.ok(driverService.softRecoveryDriverById(id));
    }
}