package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountCancelDto;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountIncreaseDto;
import com.software.modsen.drivermicroservice.mappers.DriverAccountMapper;
import com.software.modsen.drivermicroservice.services.DriverAccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/driver/account", produces = "application/json")
@AllArgsConstructor
public class DriverAccountController {
    private DriverAccountService driverAccountService;
    private final DriverAccountMapper DRIVER_ACCOUNT_MAPPER = DriverAccountMapper.INSTANCE;

    @GetMapping
    public ResponseEntity<List<DriverAccount>> getAllDriverAccounts() {
        return ResponseEntity.ok(driverAccountService.getAllDriverAccounts());
    }

    @GetMapping("/not-deleted")
    public ResponseEntity<List<DriverAccount>> getAllNotDeletedDriverAccounts() {
        return ResponseEntity.ok(driverAccountService.getAllNotDeletedDriverAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverAccount> getNotDeletedDriverAccountsById(@PathVariable("id") long id) {
        return ResponseEntity.ok(driverAccountService.getDriverAccountById(id));
    }

    @GetMapping("/{driver_id}/by-driver")
    public ResponseEntity<DriverAccount> getNotDeletedDriverAccountsByDriverId(
            @PathVariable("driver_id") long driverId) {
        return ResponseEntity.ok(driverAccountService.getDriverAccountByDriverId(driverId));
    }

    @PutMapping("/{driver_id}/increase")
    public ResponseEntity<DriverAccount> increaseBalanceByDriverId(
            @PathVariable("driver_id") long driverId,
            @Valid @RequestBody DriverAccountIncreaseDto driverAccountIncreaseDto) {
        return ResponseEntity.ok(driverAccountService.increaseBalance(
                driverId,
                DRIVER_ACCOUNT_MAPPER.fromDriverAccountIncreaseDtoToDriverAccount(driverAccountIncreaseDto)));
    }

    @PutMapping("/{driver_id}/cancel")
    public ResponseEntity<DriverAccount> cancelBalanceByPassengerId(
            @PathVariable("driver_id") long driverId,
            @Valid @RequestBody DriverAccountCancelDto driverAccountCancelDto) {
        return ResponseEntity.ok(driverAccountService.cancelBalance(
                driverId,
                DRIVER_ACCOUNT_MAPPER.fromDriverAccountCancelDtoToDriverAccount(driverAccountCancelDto)));
    }
}
