package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.exceptions.CarNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.observer.DriverSubject;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
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
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {
    @Mock
    DriverRepository driverRepository;

    @Mock
    CarRepository carRepository;

    @Mock
    DriverSubject driverSubject;

    @InjectMocks
    DriverService driverService;

    private List<Driver> initDrivers() {
        return List.of(
                new Driver(1, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                new Driver(2, "Ivan", "ivan@gmail.com",
                        "+375332929293", Sex.MALE, new Car(2, CarColor.GREEN, CarBrand.ASTON_MARTIN,
                        "A123BC-2", false), true));
    }

    @Test
    @DisplayName("Getting all drivers.")
    void getAllDriversTest_ReturnDrivers() {
        //given
        List<Driver> drivers = initDrivers();
        doReturn(drivers).when(driverRepository).findAll();

        //when
        List<Driver> driversFromDb = driverService.getAllDrivers(true);

        //then
        assertNotNull(driversFromDb);
        assertEquals(drivers, driversFromDb);
    }

    @Test
    @DisplayName("Getting all not deleted drivers.")
    void getNotDeletedAllDriversTest_ReturnsValidDrivers() {
        //given
        List<Driver> drivers = initDrivers();
        List<Driver> notDeletedDriver = List.of(drivers.get(0));
        doReturn(notDeletedDriver).when(driverRepository).findAll();

        //when
        List<Driver> driversFromDb = driverService.getAllDrivers(false);

        //then
        assertNotNull(driversFromDb);
        assertEquals(notDeletedDriver, driversFromDb);
    }

    @Test
    @DisplayName("Getting driver by id.")
    void getDriverByIdTest_WithoutExceptions_ReturnsDriver() {
        //given
        long driverId = 1;
        Optional<Driver> driver = Optional.of(new Driver(driverId, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false), false));
        doReturn(driver).when(this.driverRepository).findById(driverId);

        //when
        Driver driverFromDb = driverService.getDriverById(driverId);

        //then
        assertNotNull(driverFromDb);
        assertNotEquals(driverFromDb.getId(), 0);
        assertEquals(driver.get().getName(), driverFromDb.getName());
        assertEquals(driver.get().getEmail(), driverFromDb.getEmail());
        assertEquals(driver.get().getPhoneNumber(), driverFromDb.getPhoneNumber());
        assertEquals(driver.get().isDeleted(), driverFromDb.isDeleted());
    }

    @Test
    @DisplayName("Getting non-existing driver by id.")
    void getDriverByIdTest_WithDriverNotFoundException_ReturnsException() {
        //given
        long driverId = 1;
        doThrow(new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE))
                .when(this.driverRepository).findById(driverId);

        //when
        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverService.getDriverById(driverId));

        //then
        assertEquals(DRIVER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Saving new driver.")
    void saveDriverTest_ReturnsSavedDriver() {
        //given
        long carId = 1;
        Driver newDriver = new Driver(0, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, null, false);
        Optional<Car> carFromDb = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(carFromDb).when(carRepository).findById(carId);
        Driver driver = new Driver(1, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, carFromDb.get(), false);
        doReturn(driver).when(this.driverRepository).save(newDriver);
        doNothing().when(this.driverSubject).notifyDriverObservers(driver.getId());

        //when
        Driver driverFromDb = driverService.saveDriver(carId, newDriver);

        //then
        assertNotNull(driverFromDb);
        assertNotEquals(driverFromDb.getId(), 0);
        assertEquals(driver.getName(), driverFromDb.getName());
        assertEquals(driver.getEmail(), driverFromDb.getEmail());
        assertEquals(driver.getPhoneNumber(), driverFromDb.getPhoneNumber());
        assertEquals(driver.isDeleted(), driverFromDb.isDeleted());
        verify(this.driverRepository).save(newDriver);
    }

    @Test
    @DisplayName("Updating driver by id.")
    void updateDriverTest_WithoutException_ReturnsUpdatedDriver() {
        //given
        long driverId = 1;
        long carId = 1;
        Optional<Car> carFromDb = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(carFromDb).when(carRepository).findById(carId);
        Optional<Driver> optionalDriver = Optional.of(new Driver(driverId, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, carFromDb.get(), false));
        doReturn(optionalDriver).when(this.driverRepository).findById(driverId);
        Driver driverData = new Driver(driverId, "Alex1", "alex1@gmail.com",
                "+375299999999", Sex.MALE, carFromDb.get(), false);
        doReturn(driverData).when(this.driverRepository).save(driverData);
        driverData.setId(0);
        driverData.setCar(null);

        //when
        Driver driverFromDb = driverService.updateDriver(driverId, carId, driverData);

        //then
        assertNotNull(driverFromDb);
        assertNotEquals(driverFromDb.getId(), 0);
        assertEquals(driverData.getName(), driverFromDb.getName());
        assertEquals(driverData.getEmail(), driverFromDb.getEmail());
        assertEquals(driverData.getPhoneNumber(), driverFromDb.getPhoneNumber());
        assertEquals(driverData.isDeleted(), driverFromDb.isDeleted());
        verify(this.driverRepository).save(driverData);
    }

    @Test
    @DisplayName("Updating driver non-existent car by id.")
    void updateDriverTest_WithCarNotFoundException_ReturnsException() {
        //given
        long carId = 1;
        long driverId = 1;
        doThrow(new CarNotFoundException(CAR_NOT_FOUND_MESSAGE))
                .when(this.carRepository).findById(carId);
        Driver driverData = new Driver(0, "Alex1", "alex1@gmail.com",
                "+375299999999", Sex.MALE, null, false);

        //when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class, () ->
                driverService.updateDriver(driverId, carId, driverData));

        //then
        assertEquals(CAR_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Updating non-existent driver by id.")
    void updateDriverTest_WithDriverNotFoundException_ReturnsException() {
        //given
        long carId = 1;
        long driverId = 1;
        Optional<Car> carFromDb = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(carFromDb).when(carRepository).findById(carId);
        Driver driverData = new Driver(0, "Alex1", "alex1@gmail.com",
                "+375299999999", Sex.MALE, null, false);
        doThrow(new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE)).when(driverRepository).findById(driverId);

        //when
        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class, () ->
                driverService.updateDriver(driverId, carId, driverData));

        //then
        assertEquals(DRIVER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Updating deleted driver by id")
    void updateDriverTest_WithDriverWasDeletedException_ReturnsException() {
        //given
        long carId = 1;
        long driverId = 1;
        Optional<Car> carFromDb = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(carFromDb).when(carRepository).findById(carId);
        Optional<Driver> optionalDriver = Optional.of(new Driver(driverId, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, carFromDb.get(), true));
        doReturn(optionalDriver).when(driverRepository).findById(driverId);
        Driver driverData = new Driver(0, "Alex1", "alex1@gmail.com",
                "+375299999999", Sex.MALE, null, false);

        //when
        DriverWasDeletedException exception = assertThrows(DriverWasDeletedException.class, () ->
                driverService.updateDriver(driverId, carId, driverData));

        //then
        assertEquals(DRIVER_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Partially updating driver by id.")
    void patchDriverTest_WithoutException_ReturnsPatchedDriver() {
        //given
        long driverId = 1;
        long carId = 1;
        Optional<Car> carFromDb = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(carFromDb).when(carRepository).findById(carId);
        Optional<Driver> optionalDriver = Optional.of(new Driver(driverId, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, carFromDb.get(), false));
        doReturn(optionalDriver).when(this.driverRepository).findById(driverId);
        Driver driverData = new Driver(driverId, "Alex1", "alex1@gmail.com",
                "+375299999999", Sex.MALE, carFromDb.get(), false);
        doReturn(driverData).when(this.driverRepository).save(driverData);
        driverData.setId(0);
        driverData.setCar(null);

        //when
        Driver driverFromDb = driverService.patchDriver(driverId, carId, driverData);

        //then
        assertNotNull(driverFromDb);
        assertNotEquals(driverFromDb.getId(), 0);
        assertEquals(driverData.getName(), driverFromDb.getName());
        assertEquals(driverData.getEmail(), driverFromDb.getEmail());
        assertEquals(driverData.getPhoneNumber(), driverFromDb.getPhoneNumber());
        assertEquals(driverData.isDeleted(), driverFromDb.isDeleted());
        verify(this.driverRepository).save(driverData);
    }

    @Test
    @DisplayName("Partially updating driver with non-existent car by id.")
    void patchDriverTest_WithCarNotFoundException_ReturnsException() {
        //given
        long carId = 2;
        long driverId = 1;
        Optional<Driver> optionalDriver = Optional.of(new Driver(driverId, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false), false));
        doReturn(optionalDriver).when(driverRepository).findById(driverId);
        doThrow(new CarNotFoundException(CAR_NOT_FOUND_MESSAGE))
                .when(this.carRepository).findById(carId);
        Driver driverData = new Driver(0, "Alex1", "alex1@gmail.com",
                "+375299999999", Sex.MALE, null, false);

        //when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class, () ->
                driverService.patchDriver(driverId, carId, driverData));

        //then
        assertEquals(CAR_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Partially updating non-existent driver by id.")
    void patchDriverTest_WithDriverNotFoundException_ReturnsException() {
        //given
        long carId = 1;
        long driverId = 1;
        Driver driverData = new Driver(0, "Alex1", "alex1@gmail.com",
                "+375299999999", Sex.MALE, null, false);
        doThrow(new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE)).when(driverRepository).findById(driverId);

        //when
        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class, () ->
                driverService.patchDriver(driverId, carId, driverData));

        //then
        assertEquals(DRIVER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Partially updating deleted driver by id")
    void patchDriverTest_WithDriverWasDeletedException_ReturnsException() {
        //given
        long carId = 1;
        long driverId = 1;
        Optional<Driver> optionalDriver = Optional.of(new Driver(driverId, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false), true));
        doReturn(optionalDriver).when(driverRepository).findById(driverId);

        Driver driverData = new Driver(0, "Alex1", "alex1@gmail.com",
                "+375299999999", Sex.MALE, null, false);

        //when
        DriverWasDeletedException exception = assertThrows(DriverWasDeletedException.class, () ->
                driverService.patchDriver(driverId, carId, driverData));

        //then
        assertEquals(DRIVER_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Soft deleting driver by id")
    void softDeleteDriverByIdTest_WithoutException_ReturnsSoftDeletedDriver() {
        //given
        long driverId = 1;
        Optional<Driver> foundDriver = Optional.of(new Driver(1, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false), false));
        doReturn(foundDriver).when(this.driverRepository).findById(driverId);
        Driver deletingDriver = foundDriver.get();
        deletingDriver.setDeleted(true);
        doReturn(deletingDriver).when(this.driverRepository).save(deletingDriver);

        //when
        Driver driverFromDb = driverService.softDeleteDriverById(driverId);

        //then
        assertNotNull(driverFromDb);
        assertNotEquals(driverFromDb.getId(), 0);
        assertEquals(deletingDriver.getName(), driverFromDb.getName());
        assertEquals(deletingDriver.getEmail(), driverFromDb.getEmail());
        assertEquals(deletingDriver.getPhoneNumber(), driverFromDb.getPhoneNumber());
        assertEquals(deletingDriver.isDeleted(), driverFromDb.isDeleted());
        verify(this.driverRepository).save(deletingDriver);
    }

    @Test
    @DisplayName("Soft deleting non-existent driver by id")
    void softDeleteDriverByIdTest_WithDriverNotFoundException_ReturnsException() {
        //given
        long driverId = 2;
        doThrow(new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE))
                .when(this.driverRepository).findById(driverId);

        //when
        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverService.softDeleteDriverById(driverId));

        //then
        assertEquals(DRIVER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Soft recovering driver by id")
    void softRecoveryDriverByIdTest_WithoutException_ReturnsSoftRecoveryDriver() {
        //given
        long driverId = 1;
        Optional<Driver> foundDriver = Optional.of(new Driver(1, "Alex", "alex@gmail.com",
                "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false), false));
        doReturn(foundDriver).when(this.driverRepository).findById(driverId);
        Driver recoveringDriver = foundDriver.get();
        recoveringDriver.setDeleted(false);
        doReturn(recoveringDriver).when(this.driverRepository).save(recoveringDriver);

        //when
        Driver driverFromDb = driverService.softRecoveryDriverById(driverId);

        //then
        assertNotNull(driverFromDb);
        assertNotEquals(driverFromDb.getId(), 0);
        assertEquals(recoveringDriver.getName(), driverFromDb.getName());
        assertEquals(recoveringDriver.getEmail(), driverFromDb.getEmail());
        assertEquals(recoveringDriver.getPhoneNumber(), driverFromDb.getPhoneNumber());
        assertEquals(recoveringDriver.isDeleted(), driverFromDb.isDeleted());
        verify(this.driverRepository).save(recoveringDriver);
    }

    @Test
    @DisplayName("Soft recovering non-existent driver by id")
    void softRecoveryDriverByIdTest_WithDriverNotFoundException_ReturnsException() {
        //given
        long driverId = 2;
        doThrow(new DriverNotFoundException(DRIVER_NOT_FOUND_MESSAGE))
                .when(this.driverRepository).findById(driverId);

        //when
        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverService.softRecoveryDriverById(driverId));

        //then
        assertEquals(DRIVER_NOT_FOUND_MESSAGE, exception.getMessage());
    }
}