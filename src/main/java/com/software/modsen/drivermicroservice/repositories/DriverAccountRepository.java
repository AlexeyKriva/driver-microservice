package com.software.modsen.drivermicroservice.repositories;

import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverAccountRepository extends JpaRepository<DriverAccount, Long> {
    Optional<DriverAccount> findByDriverId(long driverId);

}