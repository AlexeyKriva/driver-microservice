package com.software.modsen.drivermicroservice.entities.driver.rating;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
@ToString
public class DriverRatingPutDto {
    @NotNull(message = "Driver id cannot be null.")
    @JsonProperty("driver_id")
    private Long driverId;

    @NotNull(message = "Rating value cannot be null.")
    @Range(min = 0, max = 5, message = "Rating value must be between 0 and 5.")
    @JsonProperty("rating_value")
    private Float ratingValue;

    @NotNull(message = "Number of ratings cannot be null.")
    @JsonProperty("number_of_ratings")
    private Integer numberOfRatings;
}