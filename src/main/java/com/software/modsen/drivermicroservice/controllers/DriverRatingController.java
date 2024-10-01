package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPutDto;
import com.software.modsen.drivermicroservice.mappers.DriverRatingMapper;
import com.software.modsen.drivermicroservice.services.DriverRatingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/driver/rating", produces = "application/json")
@AllArgsConstructor
public class DriverRatingController {
    private DriverRatingService driverRatingService;
    private final DriverRatingMapper DRIVER_RATING_MAPPER = DriverRatingMapper.INSTANCE;

    @GetMapping
    public ResponseEntity<List<DriverRating>> getAllDriverRatings() {
        return ResponseEntity.ok(driverRatingService.getAllDriverRatings());
    }

    @GetMapping("/not-deleted")
    public ResponseEntity<List<DriverRating>> getAllNotDeletedDriverRatings() {
        return ResponseEntity.ok(driverRatingService.getAllNotDeletedDriverRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverRating> getDriverRatingById(@PathVariable("id") long driverId) {
        return ResponseEntity.ok(driverRatingService.getDriverRatingById(driverId));
    }

    @GetMapping("/{driver_id}/by-driver")
    public ResponseEntity<DriverRating> getDriverRatingByDriverId(@PathVariable("driver_id") long driverId) {
        return ResponseEntity.ok(driverRatingService.getDriverRatingByDriverId(driverId));
    }

    @GetMapping("/{driver_id}/not-deleted")
    public ResponseEntity<DriverRating> getDriverRatingByDriverIdAndNotDeleted(@PathVariable("driver_id") long id) {
        return ResponseEntity.ok(driverRatingService.getDriverRatingByIdAndNotDeleted(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverRating> putDriverRatingById(@PathVariable("id") long id,
                                                                  @Valid @RequestBody
                                                                  DriverRatingPutDto driverRatingPutDto) {
        return ResponseEntity.ok(driverRatingService.putDriverRatingById(
                id,
                DRIVER_RATING_MAPPER.fromDriverRatingPutDtoToDriverRating(driverRatingPutDto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DriverRating> patchDriverRatingById(@PathVariable("id") long id,
                                                                    @Valid @RequestBody
                                                                    DriverRatingPatchDto driverRatingPatchDto) {
        return ResponseEntity.ok(driverRatingService.patchDriverRatingById(
                id,
                driverRatingPatchDto.getDriverId(),
                DRIVER_RATING_MAPPER.fromDriverRatingPatchDtoToDriverRating(driverRatingPatchDto)));
    }
}
