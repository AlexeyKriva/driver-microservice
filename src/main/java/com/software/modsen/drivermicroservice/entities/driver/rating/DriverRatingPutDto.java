package com.software.modsen.drivermicroservice.entities.driver.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@Schema(description = "Driver rating entity.")
public record DriverRatingPutDto(
        @NotNull(message = "Rating value cannot be null.")
        @Range(min = 0, max = 5, message = "Rating value must be between 0 and 5.")
        @JsonProperty("ratingValue")
        @Schema(minimum = "0", maximum = "5")
        Float ratingValue,

        @NotNull(message = "Number of ratings cannot be null.")
        @JsonProperty("numberOfRatings")
        Integer numberOfRatings
) {
}