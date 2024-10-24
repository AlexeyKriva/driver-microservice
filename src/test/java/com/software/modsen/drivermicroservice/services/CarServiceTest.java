package com.software.modsen.drivermicroservice.services;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.exceptions.CarNotFoundException;
import com.software.modsen.drivermicroservice.exceptions.CarWasDeletedException;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.CAR_NOT_FOUND_MESSAGE;
import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.CAR_WAS_DELETED_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    CarRepository carRepository;

    @InjectMocks
    CarService carService;

    private List<Car> initCars() {
        return List.of(
                new Car(1, CarColor.BLUE, CarBrand.AUDI,
                        "1234AB-1", false),
                new Car(2, CarColor.GREEN, CarBrand.ASTON_MARTIN,
                        "A123BC-2", true));
    }

    @Test
    @DisplayName("Getting all cars.")
    void getAllCarsTest_ReturnsCars() {
        //given
        List<Car> cars = initCars();
        doReturn(cars).when(carRepository).findAll();

        //when
        List<Car> carsFromDb = carService.getAllCars(true);

        //then
        assertNotNull(carsFromDb);
        assertEquals(cars, carsFromDb);
    }

    @Test
    @DisplayName("Getting all not deleted cars.")
    void getAllNotDeletedCarsTest_ReturnsValidCars() {
        //given
        List<Car> cars = initCars();
        List<Car> notDeletedCars = List.of(cars.get(0));
        doReturn(notDeletedCars).when(carRepository).findAll();

        //when
        List<Car> carsFromDb = carService.getAllCars(false);

        //then
        assertNotNull(carsFromDb);
        assertEquals(notDeletedCars, carsFromDb);
    }

    @Test
    @DisplayName("Getting car by id.")
    void getCarByIdTest_WithoutExceptions_ReturnsPassenger() {
        //given
        long carId = 1;
        Optional<Car> car = Optional.of(new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(car).when(this.carRepository).findById(carId);

        //when
        Car carFromDb = carService.getCarById(carId);

        //then
        assertNotNull(carFromDb);
        assertNotEquals(carFromDb.getId(), 0);
        assertEquals(car.get().getColor(), carFromDb.getColor());
        assertEquals(car.get().getBrand(), carFromDb.getBrand());
        assertEquals(car.get().getCarNumber(), carFromDb.getCarNumber());
        assertEquals(car.get().isDeleted(), carFromDb.isDeleted());
    }

    @Test
    @DisplayName("Getting non-existing car by id.")
    void getCarByIdTest_WithCarNotFoundException_ReturnsException() {
        //given
        long carId = 1;
        doThrow(new CarNotFoundException(CAR_NOT_FOUND_MESSAGE))
                .when(this.carRepository).findById(carId);

        //when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class,
                () -> carService.getCarById(carId));

        //then
        assertEquals(CAR_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Getting deleted car by id.")
    void getCarByIdTest_WithCarWasDeletedException_ReturnsException() {
        //given
        long carId = 1;
        Optional<Car> car = Optional.of(new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", true));
        doReturn(car).when(this.carRepository).findById(carId);

        //when
        CarWasDeletedException exception = assertThrows(CarWasDeletedException.class,
                () -> carService.getCarById(carId));

        //then
        assertEquals(CAR_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Saving new car.")
    void saveCarTest_ReturnsSavedCar() {
        //given
        Car newCar = new Car(0, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false);
        Car car = new Car(1, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false);
        doReturn(car).when(this.carRepository).save(newCar);

        //when
        Car carFromDb = carService.saveCar(newCar);

        //then
        assertNotNull(carFromDb);
        assertNotEquals(carFromDb.getId(), 0);
        assertEquals(car.getColor(), carFromDb.getColor());
        assertEquals(car.getBrand(), carFromDb.getBrand());
        assertEquals(car.getCarNumber(), carFromDb.getCarNumber());
        assertEquals(car.isDeleted(), carFromDb.isDeleted());
        verify(this.carRepository).save(newCar);
    }

    @Test
    @DisplayName("Updating car by id.")
    void updateCarTest_WithoutException_ReturnsUpdatedCar() {
        //given
        long carId = 1;
        Optional<Car> optionalCar = Optional.of(new Car(0, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(optionalCar).when(this.carRepository).findById(carId);
        Car carData = new Car(carId, CarColor.YELLOW, CarBrand.AUDI,
                "1234AB-1", false);
        doReturn(carData).when(this.carRepository).save(carData);
        carData.setId(0);

        //when
        Car carFromDb = carService.updateCar(carId, carData);

        //then
        assertNotNull(carFromDb);
        assertNotEquals(carFromDb.getId(), 0);
        assertEquals(carData.getColor(), carFromDb.getColor());
        assertEquals(carData.getBrand(), carFromDb.getBrand());
        assertEquals(carData.getCarNumber(), carFromDb.getCarNumber());
        assertEquals(carData.isDeleted(), carFromDb.isDeleted());
        verify(this.carRepository).save(carData);
    }

    @Test
    @DisplayName("Updating non-existent car by id")
    void updateCarTest_WithCarNotFoundException_ReturnsException() {
        //given
        int carId = 1;
        doThrow(new CarNotFoundException(CAR_NOT_FOUND_MESSAGE))
                .when(this.carRepository).findById(1L);
        Car carData = new Car(0, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false);

        //when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class, () ->
                carService.updateCar(carId, carData));

        //then
        assertEquals(CAR_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Updating deleted car by id")
    void updateCarByIdTest_WithCarWasDeletedException_ReturnsException() {
        //given
        int carId = 1;
        Optional<Car> optionalCar = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", true));
        doReturn(optionalCar)
                .when(this.carRepository).findById(1L);
        Car carData = new Car(0, CarColor.BROWN, CarBrand.AUDI,
                "1234AB-1", false);

        //when
        CarWasDeletedException exception = assertThrows(CarWasDeletedException.class, () ->
                carService.updateCar(carId, carData));

        //then
        assertEquals(CAR_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Partially updating car by id.")
    void patchCarByIdTest_WithoutException_ReturnsPatchedCar() {
        //given
        long carId = 1;
        Optional<Car> optionalCar = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(optionalCar).when(this.carRepository).findById(carId);
        Car carData = new Car(0, CarColor.BLUE, CarBrand.AUDI,
                null, false);
        carData.setId(carId);
        carData.setCarNumber(optionalCar.get().getCarNumber());
        doReturn(carData).when(this.carRepository).save(carData);
        carData.setId(0);
        carData.setCarNumber(null);

        //when
        Car carFromDb = carService.patchCar(carId, carData);

        //then
        assertNotNull(carFromDb);
        assertNotEquals(carFromDb.getId(), 0);
        assertEquals(carData.getColor(), carFromDb.getColor());
        assertEquals(carData.getBrand(), carFromDb.getBrand());
        assertEquals(carData.getCarNumber(), carFromDb.getCarNumber());
        assertEquals(carData.isDeleted(), carFromDb.isDeleted());
        verify(this.carRepository).save(carData);
    }

    @Test
    @DisplayName("Partially updating non-existent car by id.")
    void patchCarByIdTest_WithCarNotFoundException_ReturnsException() {
        //given
        long carId = 1;
        doThrow(new CarNotFoundException(CAR_NOT_FOUND_MESSAGE))
                .when(this.carRepository).findById(carId);
        Car carData = new Car(0, CarColor.BLUE, CarBrand.AUDI,
                null, false);

        //when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class, () ->
                carService.patchCar(carId, carData));

        //then
        assertEquals(CAR_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Updating deleted car by id")
    void patchCarByIdTest_WithCarWasDeletedException_ReturnsException() {
        //given
        int carId = 1;
        Optional<Car> optionalCar = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", true));
        doReturn(optionalCar)
                .when(this.carRepository).findById(1L);
        Car carData = new Car(0, CarColor.BLUE, CarBrand.AUDI,
                null, false);

        //when
        CarWasDeletedException exception = assertThrows(CarWasDeletedException.class, () ->
                carService.patchCar(carId, carData));

        //then
        assertEquals(CAR_WAS_DELETED_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Soft deleting car by id.")
    void softDeleteCarByIdTest_WithoutException_ReturnsSoftDeletedCar() {
        //given
        long carId = 1;
        Optional<Car> foundCar = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", false));
        doReturn(foundCar).when(this.carRepository).findById(carId);
        Car deletingCar = foundCar.get();
        deletingCar.setDeleted(true);
        doReturn(deletingCar).when(this.carRepository).save(deletingCar);

        //when
        Car carFromDb = carService.softDeleteCarById(carId);

        //then
        assertNotNull(carFromDb);
        assertNotEquals(carFromDb.getId(), 0);
        assertEquals(deletingCar.getColor(), carFromDb.getColor());
        assertEquals(deletingCar.getBrand(), carFromDb.getBrand());
        assertEquals(deletingCar.getCarNumber(), carFromDb.getCarNumber());
        assertEquals(deletingCar.isDeleted(), carFromDb.isDeleted());
        verify(this.carRepository).save(deletingCar);
    }

    @Test
    @DisplayName("Soft deleting non-existent car by id.")
    void softDeleteCarByIdTest_WithCarNotFoundException_ReturnsException() {
        //given
        long carId = 2;
        doThrow(new CarNotFoundException(CAR_NOT_FOUND_MESSAGE))
                .when(this.carRepository).findById(carId);

        //when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class,
                () -> carService.softDeleteCarById(carId));

        //then
        assertEquals(CAR_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    @DisplayName("Soft recovering car by id")
    void softRecoveryCarByIdTest_WithoutException_ReturnsSoftRecoveryCar() {
        //given
        long carId = 1;
        Optional<Car> foundCar = Optional.of(new Car(carId, CarColor.BLUE, CarBrand.AUDI,
                "1234AB-1", true));
        doReturn(foundCar).when(this.carRepository).findById(carId);
        Car recoveringCar = foundCar.get();
        recoveringCar.setDeleted(false);
        doReturn(recoveringCar).when(this.carRepository).save(recoveringCar);

        //when
        Car carFromDb = carService.softRecoveryCarById(carId);

        //then
        assertNotNull(carFromDb);
        assertNotEquals(carFromDb.getId(), 0);
        assertEquals(recoveringCar.getColor(), carFromDb.getColor());
        assertEquals(recoveringCar.getBrand(), carFromDb.getBrand());
        assertEquals(recoveringCar.getCarNumber(), carFromDb.getCarNumber());
        assertEquals(recoveringCar.isDeleted(), carFromDb.isDeleted());
        verify(this.carRepository).save(recoveringCar);
    }

    @Test
    @DisplayName("Soft recovering non-existent car by id")
    void softRecoveryCarByIdTest_WithCarNotFoundException_ReturnsException() {
        //given
        long carId = 2;
        doThrow(new CarNotFoundException(CAR_NOT_FOUND_MESSAGE))
                .when(this.carRepository).findById(carId);

        //when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class,
                () -> carService.softRecoveryCarById(carId));

        //then
        assertEquals(CAR_NOT_FOUND_MESSAGE, exception.getMessage());
    }
}