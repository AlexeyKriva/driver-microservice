package com.software.modsen.drivermicroservice.entities.car;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Car entity.")
public record CarPatchDto(
        @JsonProperty("color")
        CarColor color,

        @JsonProperty("brand")
        CarBrand brand,

        @JsonProperty("carNumber")
        @Pattern(regexp = "^[0-9]{4}[A-Z]{2}-[1-7]$|^[A-Z][0-9]{3}[A-Z]{2}-[1-7]$",
                message = "Invalid car number.")
        @Schema(example = "5678ED-3")
        String carNumber
) {
}
