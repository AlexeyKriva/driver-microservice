package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPutDto;
import com.software.modsen.drivermicroservice.mappers.DriverRatingMapper;
import com.software.modsen.drivermicroservice.services.DriverRatingService;
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
@Tag(name = "Driver rating controller.", description = "Allows to interact with driver ratings.")
public class DriverRatingController {
    private DriverRatingService driverRatingService;
    private final DriverRatingMapper DRIVER_RATING_MAPPER = DriverRatingMapper.INSTANCE;

    @GetMapping("/ratings")
    @Operation(
            description = "Allows to get all driver ratings."
    )
    public ResponseEntity<List<DriverRating>> getAllDriverRatings(
            @RequestParam(name = "includeDeleted",
                    required = false, defaultValue = "true")
            boolean includeDeleted
    ) {
        return ResponseEntity.ok(driverRatingService.getAllDriverRatings(includeDeleted));
    }

    @GetMapping("/ratings/{id}")
    @Operation(
            description = "Allows to get driver rating by id."
    )
    public ResponseEntity<DriverRating> getDriverRatingById(@PathVariable("id")
                                                                @Parameter(description = "Driver rating id.")
                                                                long id) {
        return ResponseEntity.ok(driverRatingService.getDriverRatingById(id));
    }

    @GetMapping("/{driver_id}/ratings")
    @Operation(
            description = "Allows to get driver rating by driver id."
    )
    public ResponseEntity<DriverRating> getDriverRatingByDriverId(@PathVariable("driver_id")
                                                                      @Parameter(description = "Driver id.")
                                                                      long driverId) {
        return ResponseEntity.ok(driverRatingService.getDriverRatingByDriverId(driverId));
    }

    @PutMapping("/ratings/{id}")
    @Operation(
            description = "Allows to update driver rating by id."
    )
    public ResponseEntity<DriverRating> putDriverRatingById(
            @PathVariable("id")
            @Parameter(description = "Driver rating id.")
            long id,
            @Valid
            @RequestBody
            @Parameter(description = "Driver rating entity")
            DriverRatingPutDto driverRatingPutDto) {
        return ResponseEntity.ok(driverRatingService.putDriverRatingById(
                id,
                DRIVER_RATING_MAPPER.fromDriverRatingPutDtoToDriverRating(driverRatingPutDto)));
    }

    @PatchMapping("/ratings/{id}")
    @Operation(
            description = "Allows to update driver rating by id."
    )
    public ResponseEntity<DriverRating> patchDriverRatingById(@PathVariable("id")
                                                                  @Parameter(description = "Driver rating id.")
                                                                  long id,
                                                                    @Valid @RequestBody
                                                                    @Parameter(description = "Driver rating entity")
                                                                    DriverRatingPatchDto driverRatingPatchDto) {
        return ResponseEntity.ok(driverRatingService.patchDriverRatingById(
                id,
                DRIVER_RATING_MAPPER.fromDriverRatingPatchDtoToDriverRating(driverRatingPatchDto)));
    }
}
