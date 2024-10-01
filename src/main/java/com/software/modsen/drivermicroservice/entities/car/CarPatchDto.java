package com.software.modsen.drivermicroservice.entities.car;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Schema(description = "Car entity.")
public class CarPatchDto {
    @JsonProperty("color")
    private CarColor color;

    @JsonProperty("brand")
    private CarBrand brand;

    @JsonProperty("car_number")
    @Pattern(regexp = "^[0-9]{4}[A-Z]{2}-[1-7]$|^[A-Z][0-9]{3}[A-Z]{2}-[1-7]$",
            message = "Invalid car number.")
    @Schema(example = "5678ED-3")
    private String carNumber;
}
