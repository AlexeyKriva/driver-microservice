package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPatchDto;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRatingPutDto;
import com.software.modsen.drivermicroservice.mappers.DriverRatingMapper;
import com.software.modsen.drivermicroservice.services.DriverRatingService;
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
public class DriverRatingControllerTest {
    @Mock
    DriverRatingService driverRatingService;

    @Mock
    DriverRatingMapper driverRatingMapper;

    @InjectMocks
    DriverRatingController driverRatingControllerRatingController;

    @BeforeEach
    void setUp() {
        driverRatingMapper = DriverRatingMapper.INSTANCE;
    }

    private List<DriverRating> initDriverRatings() {
        return List.of(
                new DriverRating(1,
                        new Driver(1, "Alex", "alex@gmail.com",
                                "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                                "1234AB-1", false), false),
                        100f, 30),
                new DriverRating(2,
                        new Driver(2, "Ivan", "ivan@gmail.com",
                                "+375332929293", Sex.MALE, new Car(2, CarColor.GREEN, CarBrand.ASTON_MARTIN,
                                "A123BC-2", false), false),
                        90f, 25)
        );
    }

    @Test
    @DisplayName("Getting all driver ratings.")
    void getAllDriverRatingsTest_ReturnsValidResponseEntity() {
        //given
        List<DriverRating> driverRatings = initDriverRatings();
        doReturn(driverRatings).when(this.driverRatingService).getAllDriverRatings(true);

        //when
        ResponseEntity<List<DriverRating>> responseEntity = driverRatingControllerRatingController
                .getAllDriverRatings(true);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverRatings, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting all not deleted driver ratings.")
    void getAllNotDeletedDriverRatingsRatingsTest_ReturnsValidResponseEntity() {
        //given
        List<DriverRating> driverRatings = initDriverRatings();
        doReturn(driverRatings).when(this.driverRatingService).getAllDriverRatings(false);

        //when
        ResponseEntity<List<DriverRating>> responseEntity =
                driverRatingControllerRatingController.getAllDriverRatings(false);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverRatings, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting driver rating by id.")
    void getDriverRatingByIdTest_ReturnsValidResponseEntity() {
        //given
        int driverRatingId = 1;
        DriverRating driverRating = new DriverRating(driverRatingId,
                new Driver(1, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                100f, 30);
        doReturn(driverRating).when(this.driverRatingService).getDriverRatingById(driverRatingId);

        //when
        ResponseEntity<DriverRating> responseEntity = driverRatingControllerRatingController
                .getDriverRatingById(driverRatingId);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverRating, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting driver rating by driver id.")
    void getDriverRatingByDriverIdTest_ReturnsValidResponseEntity() {
        //given
        int driverId = 1;
        DriverRating driverRating = new DriverRating(1,
                new Driver(driverId, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                100f, 30);
        doReturn(driverRating).when(this.driverRatingService).getDriverRatingByDriverId(driverId);

        //when
        ResponseEntity<DriverRating> responseEntity = driverRatingControllerRatingController
                .getDriverRatingByDriverId(driverId);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(driverRating, responseEntity.getBody());
    }

    @Test
    @DisplayName("Update driver rating by id")
    void putDriverRatingByIdTest_ReturnsValidResponseEntity() {
        //given
        int driverRatingId = 1;
        DriverRatingPutDto driverRatingPutDto = new DriverRatingPutDto(
                30f, 7);
        DriverRating driverRating = new DriverRating(driverRatingId,
                new Driver(1, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                30f, 7);
        doReturn(driverRating).when(this.driverRatingService).putDriverRatingById(driverRatingId,
                driverRatingMapper.fromDriverRatingPutDtoToDriverRating(driverRatingPutDto));

        //when
        ResponseEntity<DriverRating> responseEntity = driverRatingControllerRatingController.putDriverRatingById(
                driverRatingId, driverRatingPutDto
        );

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(DriverRating.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(driverRating.getDriver(), responseEntity.getBody().getDriver());
        assertEquals(driverRating.getRatingValue(), responseEntity.getBody().getRatingValue());
        assertEquals(driverRating.getNumberOfRatings(), responseEntity.getBody().getNumberOfRatings());
        verify(this.driverRatingService).putDriverRatingById(driverRatingId,
                driverRatingMapper.fromDriverRatingPutDtoToDriverRating(driverRatingPutDto));
    }

    @Test
    @DisplayName("Partially update driver rating by id.")
    void patchDriverRatingByIdTest_ReturnsValidResponseEntity() {
        //given
        int driverRatingId = 1;
        DriverRatingPatchDto driverRatingPatchDto = new DriverRatingPatchDto(
                30f, 7);
        DriverRating driverRating = new DriverRating(driverRatingId,
                new Driver(1, "Alex", "alex@gmail.com",
                        "+375299999999", Sex.MALE, new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false), false),
                30f, 7);

        doReturn(driverRating).when(this.driverRatingService).patchDriverRatingById(driverRatingId,
                driverRatingMapper.fromDriverRatingPatchDtoToDriverRating(driverRatingPatchDto));

        //when
        ResponseEntity<DriverRating> responseEntity = driverRatingControllerRatingController.patchDriverRatingById(
                driverRatingId, driverRatingPatchDto
        );

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(DriverRating.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(driverRating.getDriver(), responseEntity.getBody().getDriver());
        assertEquals(driverRating.getRatingValue(), responseEntity.getBody().getRatingValue());
        assertEquals(driverRating.getNumberOfRatings(), responseEntity.getBody().getNumberOfRatings());
        verify(this.driverRatingService).patchDriverRatingById(driverRatingId,
                driverRatingMapper.fromDriverRatingPatchDtoToDriverRating(driverRatingPatchDto));
    }
}