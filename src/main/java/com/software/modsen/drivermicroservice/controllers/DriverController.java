package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.DriverPatchDto;
import com.software.modsen.drivermicroservice.services.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/driver", produces = "application/json")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @GetMapping
    public ResponseEntity<List<Driver>> getAllDriver() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable("id") long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PostMapping
    public ResponseEntity<Driver> saveDriver(@Valid
                                       @RequestBody DriverDto driverDto) {
        return ResponseEntity.ok(driverService.saveDriver(driverDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriverById(@PathVariable("id") long id,
                                             @Valid @RequestBody DriverDto driverDto) {
        return ResponseEntity.ok(driverService.updateDriver(id, driverDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Driver> patchDriverById(@PathVariable("id") long id,
                                            @Valid @RequestBody DriverPatchDto driverPatchDto) {
        return ResponseEntity.ok(driverService.patchDriver(id, driverPatchDto));
    }

    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<Driver> softDeleteDriverByUd(@PathVariable("id") long id) {
        return ResponseEntity.ok(driverService.softDeleteDriverById(id));
    }
}