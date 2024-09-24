package com.software.modsen.drivermicroservice.repositories;

import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRatingRepository extends JpaRepository<DriverRating, Long> {
    Optional<DriverRating> findByDriverId(long driverId);
}
