package com.software.modsen.drivermicroservice.entities.car;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

@Schema(description = "Car entity.")
public record CarDto (
        @JsonProperty("color")
        @NotNull(message = "Color cannot be null.")
        CarColor color,

        @JsonProperty("brand")
        @NotNull(message = "Brand cannot be null.")
        CarBrand brand,

        @JsonProperty("carNumber")
        @Pattern(regexp = "^[0-9]{4}[A-Z]{2}-[1-7]$|^[A-Z][0-9]{3}[A-Z]{2}-[1-7]$", message = "Invalid car number.")
        @NotNull(message = "Car number cannot be blank.")
        @Schema(example = "A123BC-2")
        String carNumber
) {
}
