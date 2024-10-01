package com.software.modsen.drivermicroservice.entities.car;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CarDto {
    @JsonProperty("color")
    @NotNull(message = "Color cannot be null.")
    private CarColor color;

    @JsonProperty("brand")
    @NotNull(message = "Brand cannot be null.")
    private CarBrand brand;

    @JsonProperty("car_number")
    @Pattern(regexp = "^[0-9]{4}[A-Z]{2}-[1-7]$|^[A-Z][0-9]{3}[A-Z]{2}-[1-7]$", message = "Invalid car number.")
    @NotNull(message = "Car number cannot be blank.")
    private String carNumber;
}
