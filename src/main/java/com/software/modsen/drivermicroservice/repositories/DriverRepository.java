package com.software.modsen.drivermicroservice.repositories;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
}