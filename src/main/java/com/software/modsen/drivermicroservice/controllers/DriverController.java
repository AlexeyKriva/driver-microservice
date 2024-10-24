package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.DriverPatchDto;
import com.software.modsen.drivermicroservice.mappers.DriverMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/drivers", produces = "application/json")
@AllArgsConstructor
@Tag(name = "Driver controller.", description = "Allows to interact with drivers.")
public class DriverController {
    private com.software.modsen.drivermicroservice.services.DriverService driverService;
    private final DriverMapper DRIVER_MAPPER = DriverMapper.INSTANCE;

    @GetMapping
    @Operation(
            description = "Allows to get all drivers."
    )
    public ResponseEntity<List<Driver>> getAllDrivers(
            @RequestParam(name = "includeDeleted", required = false,
                    defaultValue = "true")
            boolean includeDeleted
    ) {
        return ResponseEntity.ok(driverService.getAllDrivers(includeDeleted));
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Allows to get drivers by id."
    )
    public ResponseEntity<Driver> getDriverById(@PathVariable("id")
                                                @Parameter(description = "Driver id.") long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PostMapping
    @Operation(
            description = "Allows to save new driver."
    )
    public ResponseEntity<Driver> saveDriver(@Valid
                                             @RequestBody @Parameter(description = "Driver entity.")
                                             DriverDto driverDto) {
        return ResponseEntity.ok(driverService.saveDriver(
                driverDto.carId(),
                DRIVER_MAPPER.fromDriverDtoToDriver(driverDto)));
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Allows to update driver by id."
    )
    public ResponseEntity<Driver> updateDriverById(@PathVariable("id") @Parameter(description = "Driver id.") long id,
                                                   @Valid @RequestBody @Parameter(description = "Driver entity.")
                                                   DriverDto driverDto) {
        return ResponseEntity.ok(driverService.updateDriver(
                id,
                driverDto.carId(),
                DRIVER_MAPPER.fromDriverDtoToDriver(driverDto)));
    }

    @PatchMapping("/{id}")
    @Operation(
            description = "Allows to update driver by id."
    )
    public ResponseEntity<Driver> patchDriverById(@PathVariable("id") @Parameter(description = "Driver id.") long id,
                                                  @Valid @RequestBody @Parameter(description = "Driver entity.")
                                                  DriverPatchDto driverPatchDto) {
        return ResponseEntity.ok(driverService.patchDriver(
                id,
                driverPatchDto.carId(),
                DRIVER_MAPPER.fromDriverPatchDtoToDriver(driverPatchDto)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Allows to soft delete driver by id."
    )
    public ResponseEntity<Driver> softDeleteDriverById(@PathVariable("id") @Parameter(description = "Driver id.")
                                                       long id) {
        return ResponseEntity.ok(driverService.softDeleteDriverById(id));
    }

    @PostMapping("/{id}/restore")
    @Operation(
            description = "Allows to soft delete driver by id."
    )
    public ResponseEntity<Driver> softRecoveryDriverById(@PathVariable("id") @Parameter(description = "Driver id.")
                                                         long id) {
        return ResponseEntity.ok(driverService.softRecoveryDriverById(id));
    }
}