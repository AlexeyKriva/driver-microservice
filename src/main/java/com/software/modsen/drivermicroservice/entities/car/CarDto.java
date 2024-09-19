package com.software.modsen.drivermicroservice.entities.car;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CarDto {
    @JsonProperty("color")
    @NotBlank(message = "Color cannot be empty.")
    private CarColor color;
    @JsonProperty("brand")
    @NotBlank(message = "Brand cannot be empty.")
    private CarBrand brand;
    @JsonProperty("car_number")
    @Pattern(regexp = "^[0-9]{4}[A-Z]{2}-[1-7]$|^[A-Z][0-9]{3}[A-Z]{2}-[1-7]$",
    message = "Invalid car number.")
    @NotBlank(message = "Car number cannot be blank.")
    private String carNumber;
}
