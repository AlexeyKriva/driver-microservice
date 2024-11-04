package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.DriverPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.mappers.DriverMapper;
import com.software.modsen.drivermicroservice.services.DriverService;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class DriverControllerTest {
    @Mock
    DriverService driverService;

    @Mock
    DriverMapper driverMapper;

    @InjectMocks
    DriverController driverController;

    @BeforeEach
    void setUp() {
        driverMapper = DriverMapper.INSTANCE;
    }

    private List<Driver> initDrivers() {
        return List.of(
                new Driver(1, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                new Driver(2, "Ivan", "ivan@gmail.com",
                        "+375332929293", Sex.MALE, new Car(2, CarColor.GREEN, CarBrand.ASTON_MARTIN,
                        "A123BC-2", false), false));
    }

    @Test
    @DisplayName("Getting all drivers.")
    void getAllDriversTest_ReturnsValidResponseEntity() {
        //given
        List<Driver> driversFromDb = initDrivers();
        doReturn(driversFromDb).when(this.driverService).getAllDrivers(true, null);

        //when
        ResponseEntity<List<Driver>> responseEntity = driverController.getAllDrivers(true, null);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driversFromDb, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting all not deleted drivers.")
    void getAllNotDeletedDriversTest_ReturnsValidResponseEntity() {
        //given
        List<Driver> driversFromDb = initDrivers();
        doReturn(driversFromDb).when(this.driverService).getAllDrivers(false, null);

        //when
        ResponseEntity<List<Driver>> responseEntity = driverController.getAllDrivers(false, null);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driversFromDb, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting driver by id.")
    void getDriverByIdTest_ReturnsValidResponseEntity() {
        //given
        Driver driverFromDb = new Driver(1, "Artem", "artem@gmail.com",
                "+375293333333", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.BENTLEY,
                "1234AB-1", false), false);
        doReturn(driverFromDb).when(this.driverService).getDriverById(1);

        //when
        ResponseEntity<Driver> responseEntity = driverController.getDriverById(1);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverFromDb, responseEntity.getBody());
    }

    @Test
    @DisplayName("Save new driver.")
    void saveDriverTest_ReturnsValidResponseEntity() {
        //given
        DriverDto driverDto = new DriverDto("Vova", "vova@gmail.com",
                "+375251100333", Sex.MALE, 1L);
        Driver savedDriver = new Driver(1, "Vova", "vova@gmail.com",
                "+375251100333", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.BENTLEY,
                "1234AB-1", false), false);
        doReturn(savedDriver).when(this.driverService).saveDriver(1L,
                driverMapper.fromDriverDtoToDriver(driverDto)
        );

        //when
        ResponseEntity<Driver> responseEntity = driverController.saveDriver(driverDto);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Driver.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(savedDriver.getName(), responseEntity.getBody().getName());
        assertEquals(savedDriver.getEmail(), responseEntity.getBody().getEmail());
        assertEquals(savedDriver.getPhoneNumber(), responseEntity.getBody().getPhoneNumber());
        assertEquals(savedDriver.getSex(), responseEntity.getBody().getSex());
        assertEquals(savedDriver.getCar(), responseEntity.getBody().getCar());
        assertFalse(responseEntity.getBody().isDeleted());
        verify(this.driverService).saveDriver(1L,
                driverMapper.fromDriverDtoToDriver(driverDto));
    }

    @Test
    @DisplayName("Update driver by id.")
    void updateDriverByIdTest_ReturnsValidResponseEntity() {
        //given
        int driverId = 1;
        DriverDto driverDto = new DriverDto("Vovchik", "vovchik@gmail.com",
                null, null, 1L);
        Driver updatingDriver = new Driver(driverId, "Vovchik", "vovchik@gmail.com",
                "+375251100333", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.BENTLEY,
                "1234AB-1", false), false);
        when(this.driverService.updateDriver(driverId, 1L,
                driverMapper.fromDriverDtoToDriver(driverDto)))
                .thenReturn(updatingDriver);

        //when
        ResponseEntity<Driver> responseEntity = driverController.updateDriverById(driverId, driverDto);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Driver.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(updatingDriver.getName(), responseEntity.getBody().getName());
        assertEquals(updatingDriver.getEmail(), responseEntity.getBody().getEmail());
        assertEquals(updatingDriver.getPhoneNumber(), responseEntity.getBody().getPhoneNumber());
        assertEquals(updatingDriver.getSex(), responseEntity.getBody().getSex());
        assertEquals(updatingDriver.getCar(), responseEntity.getBody().getCar());
        assertFalse(responseEntity.getBody().isDeleted());
        verify(this.driverService).updateDriver(driverId, 1L,
                driverMapper.fromDriverDtoToDriver(driverDto));
    }

    @Test
    @DisplayName("Partially update driver by id.")
    void patchDriverByIdTest_ReturnsValidResponseEntity() {
        //given
        int driverId = 1;
        DriverPatchDto driverPatchDto = new DriverPatchDto("CrazyVovchik", null,
                null, Sex.MALE, 1L);
        Driver updatingDriver = new Driver(driverId, "CrazyVovchik", "vovchik@gmail.com",
                "+375251111333", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.BENTLEY,
                "1234AB-1", false), false);
        when(this.driverService.patchDriver(driverId, 1L,
                driverMapper.fromDriverPatchDtoToDriver(driverPatchDto)))
                .thenReturn(updatingDriver);

        //when
        ResponseEntity<Driver> responseEntity = driverController.patchDriverById(driverId, driverPatchDto);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Driver.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(updatingDriver.getName(), responseEntity.getBody().getName());
        assertEquals(updatingDriver.getEmail(), responseEntity.getBody().getEmail());
        assertEquals(updatingDriver.getPhoneNumber(), responseEntity.getBody().getPhoneNumber());
        assertEquals(updatingDriver.getSex(), responseEntity.getBody().getSex());
        assertEquals(updatingDriver.getCar(), responseEntity.getBody().getCar());
        assertFalse(responseEntity.getBody().isDeleted());
        verify(this.driverService).patchDriver(driverId, 1L,
                driverMapper.fromDriverPatchDtoToDriver(driverPatchDto));
    }

    @Test
    @DisplayName("Soft delete driver by id.")
    void softDeleteDriverByIdTest_ReturnsValidResponseEntity() {
        //given
        int driverId = 1;
        Driver softDeletedDriver = new Driver(driverId, "Vova", "vova@gmail.com",
                "+375251100333", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.BENTLEY,
                "1234AB-1", false), true);
        doReturn(softDeletedDriver).when(this.driverService).softDeleteDriverById(driverId);

        //when
        ResponseEntity<Driver> responseEntity = driverController.softDeleteDriverById(driverId);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Driver.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(softDeletedDriver.getName(), responseEntity.getBody().getName());
        assertEquals(softDeletedDriver.getEmail(), responseEntity.getBody().getEmail());
        assertEquals(softDeletedDriver.getPhoneNumber(), responseEntity.getBody().getPhoneNumber());
        assertEquals(softDeletedDriver.getSex(), responseEntity.getBody().getSex());
        assertEquals(softDeletedDriver.getCar(), responseEntity.getBody().getCar());
        assertTrue(responseEntity.getBody().isDeleted());
    }

    @Test
    @DisplayName("Soft recovery driver by id.")
    void softRecoveryDriverByIdTest_ReturnsValidResponseEntity() {
        //given
        int driverId = 1;
        Driver softRecoveryDriver = new Driver(driverId, "Vova", "vova@gmail.com",
                "+375251100333", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.BENTLEY,
                "1234AB-1", false), false);
        doReturn(softRecoveryDriver).when(this.driverService).softRecoveryDriverById(driverId);

        //when
        ResponseEntity<Driver> responseEntity = driverController.softRecoveryDriverById(driverId);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Driver.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(softRecoveryDriver.getName(), responseEntity.getBody().getName());
        assertEquals(softRecoveryDriver.getEmail(), responseEntity.getBody().getEmail());
        assertEquals(softRecoveryDriver.getPhoneNumber(), responseEntity.getBody().getPhoneNumber());
        assertEquals(softRecoveryDriver.getSex(), responseEntity.getBody().getSex());
        assertEquals(softRecoveryDriver.getCar(), responseEntity.getBody().getCar());
        assertFalse(responseEntity.getBody().isDeleted());
    }
}