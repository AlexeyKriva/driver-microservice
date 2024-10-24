package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountBalanceDownDto;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountBalanceUpDto;
import com.software.modsen.drivermicroservice.mappers.DriverAccountMapper;
import com.software.modsen.drivermicroservice.services.DriverAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/drivers", produces = "application/json")
@AllArgsConstructor
@Tag(name = "Driver account controller.", description = "Allows to interact with driver accounts.")
public class DriverAccountController {
    private DriverAccountService driverAccountService;
    private final DriverAccountMapper DRIVER_ACCOUNT_MAPPER = DriverAccountMapper.INSTANCE;

    @GetMapping("/accounts")
    @Operation(
            description = "Allows to get all driver accounts."
    )
    public ResponseEntity<List<DriverAccount>> getAllDriverAccounts(
            @RequestParam(name = "includeDeleted",
                    required = false, defaultValue = "true")
            boolean includeDeleted
    ) {
        return ResponseEntity.ok(driverAccountService.getAllDriverAccounts(includeDeleted));
    }

    @GetMapping("/accounts/{id}")
    @Operation(
            description = "Allows to get not deleted driver account by id."
    )
    public ResponseEntity<DriverAccount> getNotDeletedDriverAccountsById(
            @PathVariable("id") @Parameter(description = "Driver account id.")
            long id) {
        return ResponseEntity.ok(driverAccountService.getDriverAccountById(id));
    }

    @GetMapping("/{driver_id}/accounts")
    @Operation(
            description = "Allows to get not deleted driver account by driver id."
    )
    public ResponseEntity<DriverAccount> getNotDeletedDriverAccountsByDriverId(
            @PathVariable("driver_id") @Parameter(description = "Driver id.") long driverId) {
        return ResponseEntity.ok(driverAccountService.getDriverAccountByDriverId(driverId));
    }

    @PutMapping("/{driver_id}/accounts/up")
    @Operation(
            description = "Allows to increase driver balance by driver id."
    )
    public ResponseEntity<DriverAccount> increaseBalanceByDriverId(
            @PathVariable("driver_id") @Parameter(description = "Driver id.") long driverId,
            @Valid @RequestBody @Parameter(description = "Entity to increase driver balance.")
            DriverAccountBalanceUpDto driverAccountBalanceUpDto) {
        return ResponseEntity.ok(driverAccountService.increaseBalance(
                driverId,
                DRIVER_ACCOUNT_MAPPER.fromDriverAccountIncreaseDtoToDriverAccount(driverAccountBalanceUpDto)));
    }

    @PutMapping("/{driver_id}/accounts/down")
    @Operation(
            description = "Allows to cancel driver balance by driver id."
    )
    public ResponseEntity<DriverAccount> cancelBalanceByDriverId(
            @PathVariable("driver_id") @Parameter(description = "Driver id.") long driverId,
            @Valid @RequestBody @Parameter(description = "Entity to cancel driver balance.")
            DriverAccountBalanceDownDto driverAccountBalanceDownDto) {
        return ResponseEntity.ok(driverAccountService.cancelBalance(
                driverId,
                DRIVER_ACCOUNT_MAPPER.fromDriverAccountCancelDtoToDriverAccount(driverAccountBalanceDownDto)));
    }
}
