package com.software.modsen.drivermicroservice.entities.driver.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Range;

@Schema(description = "Driver rating entity.")
public record DriverRatingPatchDto(
        @Range(min = 0, max = 5, message = "Rating value must be between 0 and 5.")
        @JsonProperty("ratingValue")
        @Schema(minimum = "0", maximum = "5")
        Float ratingValue,

        @JsonProperty("numberOfRatings")
        Integer numberOfRatings
) {
}
