package com.software.modsen.drivermicroservice.entities.driver.rating;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "driver_rating")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(name = "rating_value", nullable = false)
    private Float ratingValue;

    @Column(name = "number_of_ratings", nullable = false)
    private Integer numberOfRatings;
}