package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.account.Currency;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.exceptions.DatabaseConnectionRefusedException;
import com.software.modsen.drivermicroservice.repositories.DriverAccountRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Recover;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.BAD_CONNECTION_TO_DATABASE_MESSAGE;
import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.CANNOT_UPDATE_DATA_MESSAGE;

@AllArgsConstructor
public class DriverAccountObserver implements DriverObserver {
    private DriverRepository driverRepository;
    private DriverAccountRepository driverAccountRepository;

    @Override
    @Transactional
    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    public void updateDriverInfo(long driverId) {
        Optional<Driver> driverFromDb = driverRepository.findById(driverId);

        DriverAccount newDriverAccount = new DriverAccount();

        newDriverAccount.setDriver(driverFromDb.get());
        newDriverAccount.setBalance(0.0f);
        newDriverAccount.setCurrency(Currency.BYN);

        driverAccountRepository.save(newDriverAccount);
    }

    @Recover
    public void fallbackPostgresHandle(Throwable throwable) {
        if (throwable instanceof DataIntegrityViolationException) {
            throw (DataIntegrityViolationException) throwable;
        }

        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_UPDATE_DATA_MESSAGE);
    }
}