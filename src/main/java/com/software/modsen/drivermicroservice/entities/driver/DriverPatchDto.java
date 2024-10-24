package com.software.modsen.drivermicroservice.entities.driver;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Driver entity.")
public record DriverPatchDto(
        @JsonProperty("name")
        @Size(min = 4, max = 55, message = "The number of characters in the name" +
                " must be at least 4 and not exceed 55.")
        String name,

        @JsonProperty("email")
        @Email(message = "Invalid email format.")
        @Schema(example = "user@gmail.com")
        String email,

        @JsonProperty("phoneNumber")
        @Pattern(regexp = "^(?:\\+375|375|80)(?:25|29|33|44|17)\\d{7}$", message = "Invalid phone" +
                " number format.")
        @Schema(example = "+375331234567")
        String phoneNumber,

        @JsonProperty("sex")
        Sex sex,

        @JsonProperty("carId")
        Long carId
) {
}