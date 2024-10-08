package com.software.modsen.drivermicroservice.entities.driver;

import com.software.modsen.drivermicroservice.entities.car.Car;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "driver")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private Sex sex;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id", unique = true)
    private Car car;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}