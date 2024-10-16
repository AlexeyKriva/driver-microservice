package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountCancelDto;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountIncreaseDto;
import com.software.modsen.drivermicroservice.mappers.DriverAccountMapper;
import com.software.modsen.drivermicroservice.services.DriverAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/driver/account", produces = "application/json")
@AllArgsConstructor
@Tag(name = "Driver account controller.", description = "Allows to interact with driver accounts.")
public class DriverAccountController {
    private DriverAccountService driverAccountService;
    private final DriverAccountMapper DRIVER_ACCOUNT_MAPPER = DriverAccountMapper.INSTANCE;

    @GetMapping
    @Operation(
            description = "Allows to get all driver accounts."
    )
    public ResponseEntity<List<DriverAccount>> getAllDriverAccounts() {
        return ResponseEntity.ok(driverAccountService.getAllDriverAccounts());
    }

    @GetMapping("/not-deleted")
    @Operation(
            description = "Allows to get all not deleted driver accounts."
    )
    public ResponseEntity<List<DriverAccount>> getAllNotDeletedDriverAccounts() {
        return ResponseEntity.ok(driverAccountService.getAllNotDeletedDriverAccounts());
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Allows to get not deleted driver account by id."
    )
    public ResponseEntity<DriverAccount> getNotDeletedDriverAccountsById(
            @PathVariable("id") @Parameter(description = "Driver account id.")
            long id) {
        return ResponseEntity.ok(driverAccountService.getDriverAccountById(id));
    }

    @GetMapping("/{driver_id}/by-driver")
    @Operation(
            description = "Allows to get not deleted driver account by driver id."
    )
    public ResponseEntity<DriverAccount> getNotDeletedDriverAccountsByDriverId(
            @PathVariable("driver_id") @Parameter(description = "Driver id.") long driverId) {
        return ResponseEntity.ok(driverAccountService.getDriverAccountByDriverId(driverId));
    }

    @PutMapping("/{driver_id}/increase")
    @Operation(
            description = "Allows to increase driver balance by driver id."
    )
    public ResponseEntity<DriverAccount> increaseBalanceByDriverId(
            @PathVariable("driver_id") @Parameter(description = "Driver id.") long driverId,
            @Valid @RequestBody @Parameter(description = "Entity to increase driver balance.")
            DriverAccountIncreaseDto driverAccountIncreaseDto) {
        return ResponseEntity.ok(driverAccountService.increaseBalance(
                driverId,
                DRIVER_ACCOUNT_MAPPER.fromDriverAccountIncreaseDtoToDriverAccount(driverAccountIncreaseDto)));
    }

    @PutMapping("/{driver_id}/cancel")
    @Operation(
            description = "Allows to cancel driver balance by driver id."
    )
    public ResponseEntity<DriverAccount> cancelBalanceByDriverId(
            @PathVariable("driver_id") @Parameter(description = "Driver id.") long driverId,
            @Valid @RequestBody @Parameter(description = "Entity to cancel driver balance.")
            DriverAccountCancelDto driverAccountCancelDto) {
        return ResponseEntity.ok(driverAccountService.cancelBalance(
                driverId,
                DRIVER_ACCOUNT_MAPPER.fromDriverAccountCancelDtoToDriverAccount(driverAccountCancelDto)));
    }
}
