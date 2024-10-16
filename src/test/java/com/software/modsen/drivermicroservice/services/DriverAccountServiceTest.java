package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.entities.driver.account.Currency;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.exceptions.DriverAccountNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.exceptions.InsufficientAccountBalanceException;
import com.software.modsen.drivermicroservice.repositories.DriverAccountRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class DriverAccountServiceTest {
    @Mock
    DriverAccountRepository driverAccountRepository;

    @Mock
    DriverRepository driverRepository;

    @InjectMocks
    DriverAccountService driverAccountService;

    private List<DriverAccount> initDriverAccounts() {
        return List.of(
                new DriverAccount(1, new Driver(1, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                        100f, Currency.BYN),
                new DriverAccount(2, new Driver(2, "Ivan", "ivan@gmail.com",
                        "+375332929293", Sex.MALE, new Car(2, CarColor.GREEN, CarBrand.ASTON_MARTIN,
                        "A123BC-2", false), true),
                        100f, Currency.BYN));
    }

    private Driver driverWithIsDeleted(Boolean isDeleted) {
        return new Driver(1, "Alex", "alex@gmail.com",
                "+375299999999",Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false), isDeleted);
    }

    @Test
    @DisplayName("Getting all driver accounts.")
    void getAllDriverAccountsTest_ReturnDriverAccounts() {
        //given
        List<DriverAccount> driverAccounts = initDriverAccounts();
        doReturn(driverAccounts).when(driverAccountRepository).findAll();

        //when
        List<DriverAccount> driversAccountsFromDb = driverAccountService.getAllDriverAccounts();

        //then
        assertNotNull(driversAccountsFromDb);
        assertEquals(driverAccounts, driversAccountsFromDb);
    }

    @Test
    @DisplayName("Getting all not deleted driver accounts.")
    void getAllNotDeletedDriverAccountsTest_ReturnsValidDriverAccounts() {
        //given
        List<DriverAccount> driverAccounts = initDriverAccounts();
        List<DriverAccount> notDeletedDriverAccounts = List.of(driverAccounts.get(0));
        doReturn(notDeletedDriverAccounts).when(this.driverAccountRepository).findAll();
        doReturn(true).when(this.driverRepository)
                .existsByIdAndIsDeleted(notDeletedDriverAccounts.get(0).getDriver().getId(),
                        false);

        //when
        List<DriverAccount> driverAccountsFromDb = driverAccountService.getAllNotDeletedDriverAccounts();

        //then
        assertNotNull(driverAccountsFromDb);
        assertEquals(notDeletedDriverAccounts, driverAccountsFromDb);
    }

    @Test
    @DisplayName("Getting driver account by id.")
    void getDriverAccountByIdTest_WithoutExceptions_ReturnsValidDriverAccount() {
        //given
        long driverAccountId = 1;
        Optional<DriverAccount> driverAccount = Optional.of(new DriverAccount(1,
                driverWithIsDeleted(false),
                100f, Currency.BYN));
        doReturn(driverAccount).when(this.driverAccountRepository).findById(driverAccountId);

        //when
        DriverAccount driverAccountFromDb = driverAccountService.getDriverAccountById(driverAccountId);

        //then
        assertNotNull(driverAccountFromDb);
        assertNotEquals(driverAccountFromDb.getId(), 0);
        assertEquals(driverAccount.get().getDriver(), driverAccountFromDb.getDriver());
        assertEquals(driverAccount.get().getBalance(), driverAccountFromDb.getBalance());
        assertEquals(driverAccount.get().getCurrency(), driverAccountFromDb.getCurrency());
    }

    @Test
    @DisplayName("Getting non-existing driver account by id.")
    void getDriverAccountByIdTest_WithDriverAccountNotFoundException_ReturnsException() {
        //given
        long driverAccountId = 1;
        doThrow(new DriverAccountNotFoundException(DRIVER_ACCOUNT_NOT_FOUND_MESSAGE))
                .when(this.driverAccountRepository).findById(driverAccountId);

        //when
        DriverAccountNotFoundException exception = assertThrows(DriverAccountNotFoundException.class,
                () -> driverAccountService.getDriverAccountById(driverAccountId));

        //then
        assertEquals(DRIVER_ACCOUNT_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Getting driver account by id.")
    void getDriverAccountByDriverIdTest_WithoutException_ReturnsDriverAccount() {
        //given
        long driverId = 1;
        Optional<DriverAccount> driverAccount = Optional.of(new DriverAccount(1,
                driverWithIsDeleted(false),
                100f, Currency.BYN));
        doReturn(driverAccount).when(this.driverAccountRepository).findByDriverId(driverId);

        //when
        DriverAccount driverAccountFromDb = driverAccountService.getDriverAccountByDriverId(driverId);

        //then
        assertNotNull(driverAccountFromDb);
        assertNotEquals(driverAccountFromDb.getId(), 0);
        assertEquals(driverAccount.get().getDriver(), driverAccountFromDb.getDriver());
        assertEquals(driverAccount.get().getBalance(), driverAccountFromDb.getBalance());
        assertEquals(driverAccount.get().getCurrency(), driverAccountFromDb.getCurrency());
    }

    @Test
    @DisplayName("Getting non-existing driver account by id.")
    void getDriverAccountByDriverIdTest_WithDriverAccountNotFoundException_ReturnsException() {
        //given
        long driverId = 1;
        doThrow(new DriverAccountNotFoundException(DRIVER_ACCOUNT_NOT_FOUND_MESSAGE))
                .when(this.driverAccountRepository).findByDriverId(driverId);

        //when
        DriverAccountNotFoundException exception = assertThrows(DriverAccountNotFoundException.class,
                () -> driverAccountService.getDriverAccountByDriverId(driverId));

        //then
        assertEquals(DRIVER_ACCOUNT_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Getting deleted driver account by driver id.")
    void getDriverAccountByDriverIdTest_WithDriverWasDeletedException_ReturnsException() {
        //given
        long driverId = 1;
        Optional<DriverAccount> driverAccount = Optional.of(new DriverAccount(1,
                driverWithIsDeleted(true),
                100f, Currency.BYN));
        doReturn(driverAccount).when(this.driverAccountRepository).findByDriverId(driverId);

        //when
        DriverWasDeletedException exception = assertThrows(DriverWasDeletedException.class,
                () -> driverAccountService.getDriverAccountByDriverId(driverId));

        //then
        assertTrue(driverAccount.get().getDriver().isDeleted());
        assertEquals(DRIVER_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Increasing driver account balance")
    void increaseBalanceTest_WithoutException_ReturnsDriverAccount() {
        //given
        long driverId = 1;
        DriverAccount addingDriverAccount = new DriverAccount(0, null, 50f, Currency.BYN);
        Optional<DriverAccount> driverAccountOptional = Optional.of(new DriverAccount(
                1, driverWithIsDeleted(false), 100f, Currency.BYN));
        doReturn(driverAccountOptional).when(driverAccountRepository).findByDriverId(driverId);
        DriverAccount finalDriverAccount = new DriverAccount(
                1, driverWithIsDeleted(false), 150f, Currency.BYN);
        doReturn(finalDriverAccount).when(this.driverAccountRepository).save(finalDriverAccount);

        //when
        DriverAccount driverAccountFromDb = driverAccountService.increaseBalance(driverId,
                addingDriverAccount);

        //then
        assertFalse(driverAccountOptional.get().getDriver().isDeleted());
        assertNotNull(driverAccountFromDb);
        assertNotEquals(driverAccountFromDb.getId(), 0);
        assertEquals(finalDriverAccount.getDriver(), driverAccountFromDb.getDriver());
        assertEquals(finalDriverAccount.getBalance(), driverAccountFromDb.getBalance());
        assertEquals(finalDriverAccount.getCurrency(), driverAccountFromDb.getCurrency());
    }

    @Test
    @DisplayName("Increasing non-existing driver account balance")
    void increaseBalanceTest_WithDriverNotFoundException_ReturnsException() {
        //given
        long driverId = 1;
        DriverAccount addingDriverAccount = new DriverAccount(0, null, 50f, Currency.BYN);
        doThrow(new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE))
                .when(driverAccountRepository).findByDriverId(driverId);

        //when
        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverAccountService.increaseBalance(driverId, addingDriverAccount));

        //then
        assertEquals(DRIVER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Increasing deleted driver account balance")
    void increaseBalanceTest_WithDriverWasDeletedException_ReturnsException() {
        //given
        long driverId = 1;
        DriverAccount addingDriverAccount = new DriverAccount(0, null, 50f, Currency.BYN);
        Optional<DriverAccount> driverAccountOptional = Optional.of(new DriverAccount(
                1, driverWithIsDeleted(true), 100f, Currency.BYN));
        doReturn(driverAccountOptional).when(driverAccountRepository).findByDriverId(driverId);

        //when
        DriverWasDeletedException exception = assertThrows(DriverWasDeletedException.class,
                () -> driverAccountService.increaseBalance(driverId, addingDriverAccount));

        //then
        assertTrue(driverAccountOptional.get().getDriver().isDeleted());
        assertEquals(DRIVER_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Cancel driver account balance")
    void cancelBalanceTest_WithoutExceptions_ReturnsValidBalance() {
        //given
        long driverId = 1;
        DriverAccount addingDriverAccount = new DriverAccount(0, null, 50f, Currency.BYN);
        Optional<DriverAccount> driverAccountOptional = Optional.of(new DriverAccount(
                1, driverWithIsDeleted(false), 100f, Currency.BYN));
        doReturn(driverAccountOptional).when(driverAccountRepository).findByDriverId(driverId);
        DriverAccount finalDriverAccount = new DriverAccount(
                1, driverWithIsDeleted(false), 50f, Currency.BYN);
        doReturn(finalDriverAccount).when(this.driverAccountRepository).save(finalDriverAccount);

        //when
        DriverAccount driverAccountFromDb = driverAccountService.cancelBalance(driverId,
                addingDriverAccount);

        //then
        assertFalse(driverAccountOptional.get().getDriver().isDeleted());
        assertNotNull(driverAccountFromDb);
        assertNotEquals(driverAccountFromDb.getId(), 0);
        assertEquals(finalDriverAccount.getDriver(), driverAccountFromDb.getDriver());
        assertEquals(finalDriverAccount.getBalance(), driverAccountFromDb.getBalance());
        assertEquals(finalDriverAccount.getCurrency(), driverAccountFromDb.getCurrency());
    }

    @Test
    @DisplayName("Cancel non-existing driver account balance")
    void cancelBalanceTest_WithDriverNotFoundException_ReturnsException() {
        //given
        long driverId = 1;
        DriverAccount addingDriverAccount = new DriverAccount(0, null, 50f, Currency.BYN);
        doThrow(new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE))
                .when(driverAccountRepository).findByDriverId(driverId);

        //when
        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverAccountService.cancelBalance(driverId, addingDriverAccount));

        //then
        assertEquals(DRIVER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Cancel deleted driver account balance")
    void cancelBalanceTest_WithDriverWasDeletedException_ReturnsDriverAccount() {
        //given
        long driverId = 1;
        DriverAccount addingDriverAccount = new DriverAccount(0, null, 50f, Currency.BYN);
        Optional<DriverAccount> driverAccountOptional = Optional.of(new DriverAccount(
                1, driverWithIsDeleted(true), 100f, Currency.BYN));
        doReturn(driverAccountOptional).when(driverAccountRepository).findByDriverId(driverId);

        //when
        DriverWasDeletedException exception = assertThrows(DriverWasDeletedException.class,
                () -> driverAccountService.cancelBalance(driverId, addingDriverAccount));

        //then
        assertTrue(driverAccountOptional.get().getDriver().isDeleted());
        assertEquals(DRIVER_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Cancel driver account balance by an amount greater than what is in the account")
    void cancelBalanceTest_WithInsufficientAccountBalanceExceptionException_ReturnsException() {
        //given
        long driverId = 1;
        DriverAccount addingDriverAccount = new DriverAccount(0, null, 150f, Currency.BYN);
        Optional<DriverAccount> driverAccountOptional = Optional.of(new DriverAccount(
                1, driverWithIsDeleted(false), 100f, Currency.BYN));
        doReturn(driverAccountOptional).when(driverAccountRepository).findByDriverId(driverId);

        //when
        InsufficientAccountBalanceException exception = assertThrows(InsufficientAccountBalanceException.class,
                () -> driverAccountService.cancelBalance(driverId, addingDriverAccount));

        //then
        assertTrue(addingDriverAccount.getBalance() > driverAccountOptional.get().getBalance());
        assertEquals(INSUFFICIENT_ACCOUNT_BALANCE_EXCEPTION, exception.getMessage());
    }
}