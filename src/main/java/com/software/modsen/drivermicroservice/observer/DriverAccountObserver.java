package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.account.Currency;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.repositories.DriverAccountRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
public class DriverAccountObserver implements DriverObserver {
    private DriverRepository driverRepository;
    private DriverAccountRepository driverAccountRepository;

    @Override
    @Transactional
    public void updateDriverInfo(long driverId) {
        Optional<Driver> driverFromDb = driverRepository.findById(driverId);

        DriverAccount newDriverAccount = new DriverAccount();

        newDriverAccount.setDriver(driverFromDb.get());
        newDriverAccount.setBalance(0.0f);
        newDriverAccount.setCurrency(Currency.BYN);

        driverAccountRepository.save(newDriverAccount);
    }
}