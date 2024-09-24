package com.software.modsen.drivermicroservice.entities.driver.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
public class DriverRatingPatchDto {
    @JsonProperty("driver_id")
    private Long driverId;

    @Range(min = 1, max = 5, message = "Rating value must be between 1 and 5.")
    @JsonProperty("rating_value")
    private Integer ratingValue;

    @JsonProperty("number_of_ratings")
    private Integer numberOfRatings;
}