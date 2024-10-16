package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.entities.driver.account.Currency;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountIncreaseDto;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccountCancelDto;
import com.software.modsen.drivermicroservice.mappers.DriverAccountMapper;
import com.software.modsen.drivermicroservice.services.DriverAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DriverAccountControllerTest {
    @Mock
    DriverAccountService driverAccountService;

    @Mock
    DriverAccountMapper driverAccountMapper;

    @InjectMocks
    DriverAccountController driverAccountController;

    @BeforeEach
    void setUp() {
        driverAccountMapper = DriverAccountMapper.INSTANCE;
    }

    private List<DriverAccount> initDriverAccounts() {
        return List.of(
                new DriverAccount(1,
                        new Driver(1, "Alex", "alex@gmail.com",
                                "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                                "1234AB-1", false), false),
                        100f, Currency.BYN),
                new DriverAccount(2,
                        new Driver(2, "Ivan", "ivan@gmail.com",
                                "+375332929293", Sex.MALE, new Car(2, CarColor.GREEN, CarBrand.ASTON_MARTIN,
                                "A123BC-2", false), false),
                        90f, Currency.BYN)
        );
    }

    @Test
    @DisplayName("Getting all of driver accounts.")
    void getAllDriverAccountsTest_ReturnsValidResponseEntity() {
        //given
        List<DriverAccount> driverAccounts = initDriverAccounts();
        doReturn(driverAccounts).when(this.driverAccountService).getAllDriverAccounts();

        //when
        ResponseEntity<List<DriverAccount>> responseEntity = driverAccountController.getAllDriverAccounts();

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverAccounts, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting all not deleted of driver account.")
    void getAllNotDeletedDriverAccountsTest_ReturnsValidResponseEntity() {
        //given
        List<DriverAccount> driverAccounts = initDriverAccounts();
        doReturn(driverAccounts).when(this.driverAccountService).getAllNotDeletedDriverAccounts();

        //when
        ResponseEntity<List<DriverAccount>> responseEntity =
                driverAccountController.getAllNotDeletedDriverAccounts();

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverAccounts, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting driver account by id.")
    void getNotDeletedDriverAccountsByIdTest_ReturnsValidResponseEntity() {
        //given
        int driverAccountId = 1;
        DriverAccount driverAccount = new DriverAccount(driverAccountId,
                new Driver(1, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                100f, Currency.BYN);
        doReturn(driverAccount).when(this.driverAccountService).getDriverAccountById(driverAccountId);

        //when
        ResponseEntity<DriverAccount> responseEntity = driverAccountController
                .getNotDeletedDriverAccountsById(driverAccountId);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverAccount, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting not deleted accounts by driver id.")
    void getNotDeletedDriverAccountsByDriverIdTest_ReturnsValidResponseEntity() {
        //given
        int driverId = 1;
        DriverAccount driverAccount = new DriverAccount(1,
                new Driver(driverId, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                100f, Currency.BYN);
        doReturn(driverAccount).when(this.driverAccountService).getDriverAccountByDriverId(driverId);

        //when
        ResponseEntity<DriverAccount> responseEntity = driverAccountController
                .getNotDeletedDriverAccountsByDriverId(driverId);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverAccount, responseEntity.getBody());
    }

    @Test
    @DisplayName("Increase driver balance by driver id.")
    void increaseBalanceByDriverIdTest_ReturnsValidResponseEntity() {
        //given
        int driverId = 1;
        DriverAccountIncreaseDto driverAccountIncreaseDto = new DriverAccountIncreaseDto(1000f,
                Currency.BYN);
        DriverAccount driverAccount = new DriverAccount(1,
                new Driver(driverId, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                1100f, Currency.BYN);
        doReturn(driverAccount).when(this.driverAccountService).increaseBalance(driverId,
                driverAccountMapper.fromDriverAccountIncreaseDtoToDriverAccount(driverAccountIncreaseDto));

        //when
        ResponseEntity<DriverAccount> responseEntity = driverAccountController
                .increaseBalanceByDriverId(driverId, driverAccountIncreaseDto);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(DriverAccount.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(driverAccount.getDriver(), responseEntity.getBody().getDriver());
        assertEquals(driverAccount.getBalance(), responseEntity.getBody().getBalance());
        assertEquals(driverAccount.getCurrency(), responseEntity.getBody().getCurrency());
        verify(this.driverAccountService).increaseBalance(driverId,
                driverAccountMapper.fromDriverAccountIncreaseDtoToDriverAccount(driverAccountIncreaseDto));
    }

    @Test
    @DisplayName("Cancel driver balance by driver id.")
    void cancelBalanceByDriverIdTest_ReturnsValidResponseEntity() {
        //given
        int driverId = 1;
        DriverAccountCancelDto driverAccountCancelDto = new DriverAccountCancelDto(50f,
                Currency.BYN);
        DriverAccount driverAccount = new DriverAccount(1,
                new Driver(driverId, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                100f, Currency.BYN);
        doReturn(driverAccount).when(this.driverAccountService).cancelBalance(driverId,
                driverAccountMapper.fromDriverAccountCancelDtoToDriverAccount(driverAccountCancelDto));

        //when
        ResponseEntity<DriverAccount> responseEntity = driverAccountController
                .cancelBalanceByDriverId(driverId, driverAccountCancelDto);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(DriverAccount.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(driverAccount.getDriver(), responseEntity.getBody().getDriver());
        assertEquals(driverAccount.getBalance(), responseEntity.getBody().getBalance());
        assertEquals(driverAccount.getCurrency(), responseEntity.getBody().getCurrency());
        verify(this.driverAccountService).cancelBalance(driverId,
                driverAccountMapper.fromDriverAccountCancelDtoToDriverAccount(driverAccountCancelDto));
    }
}