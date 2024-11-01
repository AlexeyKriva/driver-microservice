package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.exceptions.*;
import com.software.modsen.drivermicroservice.repositories.DriverAccountRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.*;

@Service
@AllArgsConstructor
public class DriverAccountService {
    private DriverAccountRepository driverAccountRepository;
    private DriverRepository driverRepository;

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public List<DriverAccount> getAllDriverAccounts(boolean includeDeleted) {
        if (includeDeleted) {
            return driverAccountRepository.findAll();
        } else {
            return driverAccountRepository.findAll().stream()
                    .filter(driverAccount -> driverRepository.existsByIdAndIsDeleted(
                            driverAccount.getDriver().getId(), false))
                    .collect(Collectors.toList());
        }
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public DriverAccount getDriverAccountById(long id) {
        Optional<DriverAccount> driverAccountFromDb = driverAccountRepository.findById(id);

        if (driverAccountFromDb.isPresent()) {
                return driverAccountFromDb.get();
        }

        throw new DriverAccountNotFoundException(DRIVER_ACCOUNT_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {PSQLException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public DriverAccount getDriverAccountByDriverId(long driverId) {
        Optional<DriverAccount> driverAccountFromDb = driverAccountRepository.findByDriverId(driverId);

        if (driverAccountFromDb.isPresent()) {
            if (!driverAccountFromDb.get().getDriver().isDeleted()) {
                return driverAccountFromDb.get();
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverAccountNotFoundException(DRIVER_ACCOUNT_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public DriverAccount increaseBalance(long driverId, DriverAccount updatingDriverAccount) {
        Optional<DriverAccount> driverAccountFromDb = driverAccountRepository.findByDriverId(driverId);

        if (driverAccountFromDb.isPresent()) {
            updatingDriverAccount.setId(driverAccountFromDb.get().getId());

            if (!driverAccountFromDb.get().getDriver().isDeleted()) {
                updatingDriverAccount.setDriver(driverAccountFromDb.get().getDriver());

                Float increasingBalance = updatingDriverAccount.getBalance()
                        + driverAccountFromDb.get().getBalance();
                updatingDriverAccount.setBalance(increasingBalance);

                return driverAccountRepository.save(updatingDriverAccount);
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @CircuitBreaker(name = "simpleCircuitBreaker", fallbackMethod = "fallbackPostgresHandle")
    @Transactional
    public DriverAccount cancelBalance(long driverId, DriverAccount updatingDriverAccount) {
        Optional<DriverAccount> driverAccountFromDb = driverAccountRepository.findByDriverId(driverId);

        if (driverAccountFromDb.isPresent()) {
            updatingDriverAccount.setId(driverAccountFromDb.get().getId());

            if (!driverAccountFromDb.get().getDriver().isDeleted()) {
                updatingDriverAccount.setDriver(driverAccountFromDb.get().getDriver());

                float increasingBalance = driverAccountFromDb.get().getBalance()
                        - updatingDriverAccount.getBalance();
                if (increasingBalance >= 0) {
                    updatingDriverAccount.setBalance(increasingBalance);

                    return driverAccountRepository.save(updatingDriverAccount);
                } else {
                    throw new InsufficientAccountBalanceException(INSUFFICIENT_ACCOUNT_BALANCE_EXCEPTION);
                }
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Recover
    public DriverAccount fallbackPostgresHandle(Throwable throwable) {
        if (throwable instanceof DataIntegrityViolationException) {
            throw (DataIntegrityViolationException) throwable;
        }

        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_UPDATE_DATA_MESSAGE);
    }

    @Recover
    public List<DriverAccount> recoverToPSQLException(Throwable throwable) {
        throw new DatabaseConnectionRefusedException(BAD_CONNECTION_TO_DATABASE_MESSAGE + CANNOT_GET_DATA_MESSAGE);
    }
}