package com.software.modsen.drivermicroservice.entities.driver;

import com.software.modsen.drivermicroservice.entities.car.Car;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "driver")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Driver entity.")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    @Schema(example = "user@gmail.com")
    private String email;

    @Column(name = "phone_number", nullable = false)
    @Schema(example = "+375331234567")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private Sex sex;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id", unique = true)
    private Car car;

    @Column(name = "is_deleted", nullable = false)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private boolean isDeleted;
}