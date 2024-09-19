package com.software.modsen.drivermicroservice.entities.car;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "car")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "color", nullable = false)
    private CarColor color;
    @Column(name = "brand", nullable = false)
    private CarBrand brand;
    @Column(name = "car_number", nullable = false)
    private String carNumber;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}