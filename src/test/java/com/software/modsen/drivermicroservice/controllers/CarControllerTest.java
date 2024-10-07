package com.software.modsen.drivermicroservice.controllers;

import com.software.modsen.drivermicroservice.entities.car.*;
import com.software.modsen.drivermicroservice.mappers.CarMapper;
import com.software.modsen.drivermicroservice.services.CarService;
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
public class CarControllerTest {
    @Mock
    CarService carService;

    @Mock
    CarMapper carMapper;

    @InjectMocks
    CarController carController;

    @BeforeEach
    void setUp() {
        carMapper = CarMapper.INSTANCE;
    }

    private List<Car> initCars() {
        return List.of(
                new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false),
                new Car(2, CarColor.GREEN, CarBrand.ASTON_MARTIN,
                        "A123BC-2", true));
    }

    @Test
    @DisplayName("Getting all cars.")
    void getAllCarsTest_ReturnsValidResponseEntity() {
        //given
        List<Car> carsFromDb = initCars();
        doReturn(carsFromDb).when(this.carService).getAllCars();

        //when
        ResponseEntity<List<Car>> responseEntity = carController.getAllCars();

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(carsFromDb, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting all not deleted cars.")
    void getAllNotDeletedCarsTest_ReturnsValidResponseEntity() {
        //given
        List<Car> carsFromDb = initCars();
        doReturn(carsFromDb).when(this.carService).getAllNotDeletedCars();

        //when
        ResponseEntity<List<Car>> responseEntity = carController.getAllNotDeletedCars();

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(carsFromDb, responseEntity.getBody());
    }

    @Test
    @DisplayName("Getting car by id.")
    void getCarByIdTest_ReturnsValidResponseEntity() {
        //given
        Car carFromDb = new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false);
        doReturn(carFromDb).when(this.carService).getCarById(1);

        //when
        ResponseEntity<Car> responseEntity = carController.getCarById(1);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(carFromDb, responseEntity.getBody());
    }

    @Test
    @DisplayName("Save new car.")
    void saveCarTest_ReturnsValidResponseEntity() {
        //given
        CarDto carDto = new CarDto(CarColor.BROWN, CarBrand.TESLA,
                "1235AB-1");
        Car savedCar = new Car(1, CarColor.BROWN, CarBrand.TESLA,
                "1235AB-1", false);
        doReturn(savedCar).when(this.carService).saveCar(
                carMapper.fromCarDtoToCar(carDto)
        );

        //when
        ResponseEntity<Car> responseEntity = carController.saveCar(carDto);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Car.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(savedCar.getColor(), responseEntity.getBody().getColor());
        assertEquals(savedCar.getBrand(), responseEntity.getBody().getBrand());
        assertEquals(savedCar.getCarNumber(), responseEntity.getBody().getCarNumber());
        assertFalse(responseEntity.getBody().isDeleted());
        verify(this.carService).saveCar(
                carMapper.fromCarDtoToCar(carDto));
    }

    @Test
    @DisplayName("Update car by id.")
    void updateCarByIdTest_ReturnsValidResponseEntity() {
        //given
        int carId = 1;
        CarDto carDto = new CarDto(CarColor.BROWN, CarBrand.CADILLAC,
                "1235AB-1");
        Car updatingCar = new Car(carId, CarColor.BROWN, CarBrand.CADILLAC,
                "1235AB-1", false);
        when(this.carService.updateCar(carId,
                carMapper.fromCarDtoToCar(carDto)))
                .thenReturn(updatingCar);

        //when
        ResponseEntity<Car> responseEntity = carController.updateCarById(carId, carDto);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Car.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(updatingCar.getColor(), responseEntity.getBody().getColor());
        assertEquals(updatingCar.getBrand(), responseEntity.getBody().getBrand());
        assertEquals(updatingCar.getCarNumber(), responseEntity.getBody().getCarNumber());
        assertFalse(responseEntity.getBody().isDeleted());
        verify(this.carService).updateCar(carId,
                carMapper.fromCarDtoToCar(carDto));
    }

    @Test
    @DisplayName("Partially update car by id.")
    void patchCarByIdTest_ReturnsValidResponseEntity() {
        //given
        int carId = 1;
        CarPatchDto carPatchDto = new CarPatchDto(CarColor.YELLOW, CarBrand.VOLKSWAGEN,
                null);
        Car updatingCar = new Car(carId, CarColor.YELLOW, CarBrand.VOLKSWAGEN,
                "1235AB-1", false);
        when(this.carService.patchCar(carId,
                carMapper.fromCarPatchDtoToCar(carPatchDto)))
                .thenReturn(updatingCar);

        //when
        ResponseEntity<Car> responseEntity = carController.patchCarById(carId, carPatchDto);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Car.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(updatingCar.getColor(), responseEntity.getBody().getColor());
        assertEquals(updatingCar.getBrand(), responseEntity.getBody().getBrand());
        assertEquals(updatingCar.getCarNumber(), responseEntity.getBody().getCarNumber());
        assertFalse(responseEntity.getBody().isDeleted());
        verify(this.carService).patchCar(carId,
                carMapper.fromCarPatchDtoToCar(carPatchDto));
    }

    @Test
    @DisplayName("Soft delete car by id.")
    void softDeleteCarByIdTest_ReturnsValidResponseEntity() {
        //given
        int carId = 1;
        Car softDeletedCar = new Car(carId, CarColor.YELLOW, CarBrand.VOLKSWAGEN,
                "1235AB-1", true);
        doReturn(softDeletedCar).when(this.carService).softDeleteCarById(carId);

        //when
        ResponseEntity<Car> responseEntity = carController.softDeleteCarById(carId);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Car.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(softDeletedCar.getColor(), responseEntity.getBody().getColor());
        assertEquals(softDeletedCar.getBrand(), responseEntity.getBody().getBrand());
        assertEquals(softDeletedCar.getCarNumber(), responseEntity.getBody().getCarNumber());
        assertTrue(responseEntity.getBody().isDeleted());
    }

    @Test
    @DisplayName("Soft recovery car by id.")
    void softRecoveryCarByIdTest_ReturnsValidResponseEntity() {
        //given
        int carId = 1;
        Car softRecoveryCar = new Car(carId, CarColor.YELLOW, CarBrand.VOLKSWAGEN,
                "1235AB-1", false);
        doReturn(softRecoveryCar).when(this.carService).softRecoveryCarById(carId);

        //when
        ResponseEntity<Car> responseEntity = carController.softRecoveryCarById(carId);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Car.class, responseEntity.getBody());
        assertNotEquals(responseEntity.getBody().getId(), 0);
        assertEquals(softRecoveryCar.getColor(), responseEntity.getBody().getColor());
        assertEquals(softRecoveryCar.getBrand(), responseEntity.getBody().getBrand());
        assertEquals(softRecoveryCar.getCarNumber(), responseEntity.getBody().getCarNumber());
        assertFalse(responseEntity.getBody().isDeleted());
    }
}