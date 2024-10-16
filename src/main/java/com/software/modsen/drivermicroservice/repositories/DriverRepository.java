package com.software.modsen.drivermicroservice.repositories;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findDriverByIdAndIsDeleted(long id, boolean isDeleted);

    boolean existsByIdAndIsDeleted(long id, boolean isDeleted);
}