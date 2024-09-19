package com.software.modsen.drivermicroservice.repositories;

import com.software.modsen.drivermicroservice.entities.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
}