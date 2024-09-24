package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPutDto;
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

    @GetMapping
    public ResponseEntity<List<DriverRating>> getAllDriverRatings() {
        return ResponseEntity.ok(driverRatingService.getAllDriverRatings());
    }

    @GetMapping("/not-deleted")
    public ResponseEntity<List<DriverRating>> getAllDriverRatingsAndNotDeleted() {
        return ResponseEntity.ok(driverRatingService.getAllDriverRatingsAndNotDeleted());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverRating> getDriverRatingById(@PathVariable("id") long id) {
        return ResponseEntity.ok(driverRatingService.getDriverRatingById(id));
    }

    @GetMapping("/{driver_id}/not-deleted")
    public ResponseEntity<DriverRating> getDriverRatingByDriverIdAndNotDeleted(@PathVariable("driver_id") long id) {
        return ResponseEntity.ok(driverRatingService.getDriverRatingByIdAndNotDeleted(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverRating> putDriverRatingById(@PathVariable("id") long id,
                                                                  @Valid @RequestBody
                                                                  DriverRatingPutDto driverRatingPutDto) {
        return ResponseEntity.ok(driverRatingService.putDriverRatingById(id, driverRatingPutDto));
    }

    @PatchMapping
    public ResponseEntity<DriverRating> updateDriverRating(@Valid
                                                                 @RequestBody DriverRatingDto driverRatingDto) {
        return ResponseEntity.ok(driverRatingService.updateDriverRating(driverRatingDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DriverRating> patchDriverRatingById(@PathVariable("id") long id,
                                                                    @Valid @RequestBody
                                                                    DriverRatingPatchDto driverRatingPatchDto) {
        return ResponseEntity.ok(driverRatingService.patchDriverRatingById(id, driverRatingPatchDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriverRatingById(@PathVariable("id") long id) {
        driverRatingService.deleteDriverRatingById(id);
        return ResponseEntity.ok("Driver rating was successfully deleted by id " + id);
    }
}
