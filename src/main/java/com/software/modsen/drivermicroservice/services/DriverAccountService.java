package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.exceptions.DriverAccountNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.exceptions.InsufficientAccountBalanceException;
import com.software.modsen.drivermicroservice.repositories.DriverAccountRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
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

    public List<DriverAccount> getAllDriverAccounts() {
        return driverAccountRepository.findAll();
    }

    public List<DriverAccount> getAllNotDeletedDriverAccounts() {
        return driverAccountRepository.findAll().stream()
                .filter(driverAccount -> driverRepository.existsByIdAndIsDeleted(
                        driverAccount.getDriver().getId(), false))
                .collect(Collectors.toList());
    }

    public DriverAccount getDriverAccountById(long id) {
        Optional<DriverAccount> driverAccountFromDb = driverAccountRepository.findById(id);

        if (driverAccountFromDb.isPresent()) {
            Optional<Driver> driverFromDb = driverRepository.findById(
                    driverAccountFromDb.get().getDriver().getId());

            if (!driverFromDb.get().isDeleted()) {
                return driverAccountFromDb.get();
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverAccountNotFoundException(DRIVER_ACCOUNT_NOT_FOUND_MESSAGE);
    }

    public DriverAccount getDriverAccountByDriverId(long driverId) {
        Optional<DriverAccount> driverAccountFromDb = driverAccountRepository.findByDriverId(driverId);

        if (driverAccountFromDb.isPresent()) {
            Optional<Driver> driverFromDb = driverRepository.findById(driverId);

            if (!driverFromDb.get().isDeleted()) {
                return driverAccountFromDb.get();
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverAccountNotFoundException(DRIVER_ACCOUNT_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public DriverAccount increaseBalance(long driverId, DriverAccount updatingDriverAccount) {
        Optional<DriverAccount> driverAccountFromDb = driverAccountRepository.findByDriverId(driverId);

        if (driverAccountFromDb.isPresent()) {
            updatingDriverAccount.setId(driverAccountFromDb.get().getId());

            Optional<Driver> driverFromDb = driverRepository.findById(driverId);

            if (!driverFromDb.get().isDeleted()) {
                updatingDriverAccount.setDriver(driverFromDb.get());

                Float increasingBalance = updatingDriverAccount.getBalance()
                        + driverAccountFromDb.get().getBalance();
                updatingDriverAccount.setBalance(increasingBalance);

                driverAccountRepository.save(updatingDriverAccount);
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }

    @Retryable(retryFor = {DataAccessException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    @Transactional
    public DriverAccount cancelBalance(long driverId, DriverAccount updatingDriverAccount) {
        Optional<DriverAccount> driverAccountFromDb = driverAccountRepository.findByDriverId(driverId);

        if (driverAccountFromDb.isPresent()) {
            updatingDriverAccount.setId(driverAccountFromDb.get().getId());

            Optional<Driver> driverFromDb = driverRepository.findById(driverId);

            if (!driverFromDb.get().isDeleted()) {
                updatingDriverAccount.setDriver(driverFromDb.get());

                float increasingBalance = driverAccountFromDb.get().getBalance()
                        - updatingDriverAccount.getBalance();
                if (increasingBalance >= 0) {
                    updatingDriverAccount.setBalance(increasingBalance);

                    driverAccountRepository.save(updatingDriverAccount);
                } else {
                    throw new InsufficientAccountBalanceException(INSUFFICIENT_ACCOUNT_BALANCE_EXCEPTION);
                }
            }

            throw new DriverWasDeletedException(DRIVER_WAS_DELETED_MESSAGE);
        }

        throw new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE);
    }
}