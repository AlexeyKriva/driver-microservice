package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.exceptions.DriverRatingNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.DriverWasDeletedException;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.DRIVER_RATING_NOT_FOUND_MESSAGE;
import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.DRIVER_WAS_DELETED_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class DriverRatingServiceTest {
    @Mock
    DriverRatingRepository driverRatingRepository;

    @Mock
    DriverRepository driverRepository;

    @InjectMocks
    DriverRatingService driverRatingService;

    private List<DriverRating> initDriverRatings() {
        return List.of(
                new DriverRating(1, new Driver(1, "Alex", "alex@gmail.com",
                "+375299999999",Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false), false),
                        4.5F, 129),
                new DriverRating(1, new Driver(2, "Ivan", "ivan@gmail.com",
                "+375332929293", Sex.MALE, new Car(2, CarColor.GREEN, CarBrand.ASTON_MARTIN,
                "A123BC-2", false), true),
                        5.0F, 33));
    }

    private Driver driverWithIsDeleted(Boolean isDeleted) {
        return new Driver(1, "Alex", "alex@gmail.com",
                "+375299999999",Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false), isDeleted);
    }

    @Test
    @DisplayName("Getting all driver ratings.")
    void getAllDriverRatingsTest_ReturnDriverAccounts() {
        //given
        List<DriverRating> driverRatings = initDriverRatings();
        doReturn(driverRatings).when(driverRatingRepository).findAll();

        //when
        List<DriverRating> driversRatingsFromDb = driverRatingService.getAllDriverRatings();

        //then
        assertNotNull(driversRatingsFromDb);
        assertEquals(driverRatings, driversRatingsFromDb);
    }

    @Test
    @DisplayName("Getting all not deleted drivers.")
    void getAllNotDeletedDriverAccountsTest_ReturnsValidAccounts() {
        //given
        List<DriverRating> driverRatings = initDriverRatings();
        List<DriverRating> notDeletedDriverRatings = List.of(driverRatings.get(0));
        doReturn(driverRatings).when(driverRatingRepository).findAll();
        Optional<Driver> driverOptional = Optional.of(notDeletedDriverRatings.get(0).getDriver());
        doReturn(driverOptional).when(this.driverRepository)
                .findDriverByIdAndIsDeleted(notDeletedDriverRatings.get(0).getDriver().getId(),
                        false);

        //when
        List<DriverRating> passengerRatingsFromDb = driverRatingService.getAllNotDeletedDriverRatings();

        //then
        assertNotNull(passengerRatingsFromDb);
        assertEquals(notDeletedDriverRatings, passengerRatingsFromDb);
    }

    @Test
    @DisplayName("Getting driver rating by id.")
    void getDriverRatingByIdTest_WithoutException_ReturnsDriverRating() {
        //given
        long driverRatingId = 1;
        Optional<DriverRating> driverRating = Optional.of(new DriverRating(1,
                driverWithIsDeleted(false),
                4.5F, 129));
        doReturn(driverRating).when(this.driverRatingRepository).findById(driverRatingId);

        //when
        DriverRating driverRatingFromDb = driverRatingService.getDriverRatingById(driverRatingId);

        //then
        assertNotNull(driverRatingFromDb);
        assertNotEquals(driverRatingFromDb.getId(), 0);
        assertEquals(driverRating.get().getDriver(), driverRatingFromDb.getDriver());
        assertEquals(driverRating.get().getRatingValue(), driverRatingFromDb.getRatingValue());
        assertEquals(driverRating.get().getNumberOfRatings(), driverRatingFromDb.getNumberOfRatings());
    }

    @Test
    @DisplayName("Getting driver rating by id.")
    void getDriverRatingByIdTest_WithDriverRatingNotFoundException_ReturnsException() {
        //given
        long driverRatingId = 1;
        doThrow(new DriverRatingNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE))
                .when(driverRatingRepository).findById(driverRatingId);

        //when
        DriverRatingNotFoundException exception = assertThrows(DriverRatingNotFoundException.class,
                () -> driverRatingService.getDriverRatingById(driverRatingId));

        //then
        assertEquals(DRIVER_RATING_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Getting driver rating by driver id.")
    void getDriverRatingByDriverIdTest_WithoutException_ReturnsDriverRating() {
        //given
        long driverId = 1;
        Optional<DriverRating> driverRating = Optional.of(new DriverRating(1,
                driverWithIsDeleted(false),
                4.5F, 129));
        doReturn(driverRating).when(this.driverRatingRepository).findByDriverId(driverId);

        //when
        DriverRating driverRatingFromDb = driverRatingService.getDriverRatingByDriverId(driverId);

        //then
        assertNotNull(driverRatingFromDb);
        assertNotEquals(driverRatingFromDb.getId(), 0);
        assertEquals(driverRating.get().getDriver(), driverRatingFromDb.getDriver());
        assertEquals(driverRating.get().getRatingValue(), driverRatingFromDb.getRatingValue());
        assertEquals(driverRating.get().getNumberOfRatings(), driverRatingFromDb.getNumberOfRatings());
    }

    @Test
    @DisplayName("Getting non-existing driver rating by driver id.")
    void getDriverRatingByDriverIdTest_WithDriverRatingNotFoundException_ReturnsException() {
        //given
        long driverId = 1;
        doThrow(new DriverRatingNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE))
                .when(driverRatingRepository).findByDriverId(driverId);

        //when
        DriverRatingNotFoundException exception = assertThrows(DriverRatingNotFoundException.class,
                () -> driverRatingService.getDriverRatingByDriverId(driverId));

        //then
        assertEquals(DRIVER_RATING_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Getting deleted driver rating by driver id.")
    void getDriverRatingByDriverIdTest_WithDriverWasDeletedException_ReturnsException() {
        //given
        long driverId = 1;
        Optional<DriverRating> driverRating = Optional.of(new DriverRating(1,
                driverWithIsDeleted(true),
                4.5F, 129));
        doReturn(driverRating).when(this.driverRatingRepository).findByDriverId(driverId);

        //when
        DriverWasDeletedException exception = assertThrows(DriverWasDeletedException.class,
                () -> driverRatingService.getDriverRatingByDriverId(driverId));

        //then
        assertTrue(driverRating.get().getDriver().isDeleted());
        assertEquals(DRIVER_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Update driver rating by id.")
    void putDriverRatingByIdTest_WithoutException_ReturnsDriverRating() {
        //given
        long driverRatingId = 1;
        DriverRating driverRatingData = new DriverRating(0, null,
                4.7f, 29);
        Optional<DriverRating> driverRatingFromData = Optional.of(new DriverRating(1,
                driverWithIsDeleted(false),
                2.7f, 15));
        doReturn(driverRatingFromData).when(driverRatingRepository).findById(driverRatingId);
        DriverRating updatingDriverRating = new DriverRating(driverRatingId, driverWithIsDeleted(false),
                4.7f, 29);
        doReturn(updatingDriverRating).when(driverRatingRepository).save(updatingDriverRating);

        //when
        DriverRating driverRatingFromDb = driverRatingService.putDriverRatingById(driverRatingId,
                driverRatingData);

        //then
        assertNotNull(driverRatingFromDb);
        assertNotEquals(driverRatingFromDb.getId(), 0);
        assertEquals(updatingDriverRating.getDriver(), driverRatingFromDb.getDriver());
        assertEquals(updatingDriverRating.getRatingValue(), driverRatingFromDb.getRatingValue());
        assertEquals(updatingDriverRating.getNumberOfRatings(), driverRatingFromDb.getNumberOfRatings());
    }

    @Test
    @DisplayName("Update non-existing driver rating by id.")
    void putDriverRatingByIdTest_WithDriverRatingNotFoundException_ReturnsException(){
        //given
        long driverRatingId = 1;
        DriverRating driverRatingData = new DriverRating(0, null,
                4.7f, 29);
        doThrow(new DriverRatingNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE))
                .when(driverRatingRepository).findById(driverRatingId);

        //when
        DriverRatingNotFoundException exception = assertThrows(DriverRatingNotFoundException.class,
                () -> driverRatingService.putDriverRatingById(driverRatingId, driverRatingData));

        //then
        assertEquals(DRIVER_RATING_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Update deleted driver rating by id.")
    void putDriverRatingByIdTest_WithDriverWasDeletedException_ReturnsException(){
        //given
        long driverRatingId = 1;
        DriverRating driverRatingData = new DriverRating(0, null,
                4.7f, 29);
        Optional<DriverRating> driverRatingFromData = Optional.of(new DriverRating(1,
                driverWithIsDeleted(true),
                2.7f, 15));
        doReturn(driverRatingFromData).when(driverRatingRepository).findById(driverRatingId);

        //when
        DriverWasDeletedException exception = assertThrows(DriverWasDeletedException.class,
                () -> driverRatingService.putDriverRatingById(driverRatingId, driverRatingData));

        //then
        assertTrue(driverRatingFromData.get().getDriver().isDeleted());
        assertEquals(DRIVER_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Partially update driver rating by id.")
    void patchDriverRatingByIdTest_WithoutException_ReturnsDriverRating() {
        //given
        long driverRatingId = 1;
        DriverRating driverRatingData = new DriverRating(0, null,
                4.7f, 29);
        Optional<DriverRating> driverRatingFromData = Optional.of(new DriverRating(1,
                driverWithIsDeleted(false),
                2.7f, 15));
        doReturn(driverRatingFromData).when(driverRatingRepository).findById(driverRatingId);
        DriverRating updatingDriverRating = new DriverRating(driverRatingId, driverWithIsDeleted(false),
                4.7f, 29);
        doReturn(updatingDriverRating).when(driverRatingRepository).save(updatingDriverRating);

        //when
        DriverRating driverRatingFromDb = driverRatingService.patchDriverRatingById(driverRatingId,
                driverRatingData);

        //then
        assertNotNull(driverRatingFromDb);
        assertNotEquals(driverRatingFromDb.getId(), 0);
        assertEquals(updatingDriverRating.getDriver(), driverRatingFromDb.getDriver());
        assertEquals(updatingDriverRating.getRatingValue(), driverRatingFromDb.getRatingValue());
        assertEquals(updatingDriverRating.getNumberOfRatings(), driverRatingFromDb.getNumberOfRatings());
    }

    @Test
    @DisplayName("Partially update non-existing driver rating by id.")
    void patchDriverRatingByIdTest_WithDriverRatingNotFoundException_ReturnsException(){
        //given
        long driverRatingId = 1;
        DriverRating driverRatingData = new DriverRating(0, null,
                4.7f, 29);
        doThrow(new DriverRatingNotFoundException(DRIVER_RATING_NOT_FOUND_MESSAGE))
                .when(driverRatingRepository).findById(driverRatingId);

        //when
        DriverRatingNotFoundException exception = assertThrows(DriverRatingNotFoundException.class,
                () -> driverRatingService.patchDriverRatingById(driverRatingId, driverRatingData));

        //then
        assertEquals(DRIVER_RATING_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Partially update deleted driver rating by id.")
    void patchDriverRatingByIdTest_WithDriverWasDeletedException_ReturnsException(){
        //given
        long driverRatingId = 1;
        DriverRating driverRatingData = new DriverRating(0, null,
                4.7f, 29);
        Optional<DriverRating> driverRatingFromData = Optional.of(new DriverRating(1,
                driverWithIsDeleted(true),
                2.7f, 15));
        doReturn(driverRatingFromData).when(driverRatingRepository).findById(driverRatingId);

        //when
        DriverWasDeletedException exception = assertThrows(DriverWasDeletedException.class,
                () -> driverRatingService.patchDriverRatingById(driverRatingId, driverRatingData));

        //then
        assertTrue(driverRatingFromData.get().getDriver().isDeleted());
        assertEquals(DRIVER_WAS_DELETED_MESSAGE, exception.getMessage());
    }
}