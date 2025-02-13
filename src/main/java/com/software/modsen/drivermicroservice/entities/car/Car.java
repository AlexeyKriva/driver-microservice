package com.software.modsen.drivermicroservice.entities.car;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "car")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Car entity.")
public class Car implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    private CarColor color;

    @Enumerated(EnumType.STRING)
    @Column(name = "brand", nullable = false)
    private CarBrand brand;

    @Column(name = "car_number", nullable = false)
    @Schema(example = "1234AB-1")
    private String carNumber;

    @Column(name = "is_deleted", nullable = false)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private boolean isDeleted;
}