package com.software.modsen.drivermicroservice.integration;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.repositories.CarRepository;
import com.software.modsen.drivermicroservice.repositories.DriverAccountRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import com.software.modsen.drivermicroservice.services.CarService;
import com.software.modsen.drivermicroservice.services.DriverService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DriverRatingControllerIntegrationTest extends TestconteinersConfig {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarService carService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverRatingRepository driverRatingRepository;

    @Autowired
    private DriverAccountRepository driverAccountRepository;

    @AfterEach
    void setDown() {
        driverAccountRepository.deleteAll();
        driverRatingRepository.deleteAll();
        driverRepository.deleteAll();
        carRepository.deleteAll();
    }

    private static List<Car> defaultCars() {
        return List.of(
                Car.builder()
                        .color(CarColor.BLUE)
                        .brand(CarBrand.AUDI)
                        .carNumber("1234AB-1")
                        .isDeleted(false)
                        .build(),
                Car.builder()
                        .color(CarColor.BROWN)
                        .brand(CarBrand.FERRARI)
                        .carNumber("7890AB-7")
                        .isDeleted(false)
                        .build(),
                Car.builder()
                        .color(CarColor.GREEN)
                        .brand(CarBrand.MERCEDES_BENZ)
                        .carNumber("3333AB-3")
                        .isDeleted(true)
                        .build()
        );
    }

    private static List<Driver> defaultDrivers() {
        return List.of(
                Driver.builder()
                        .name("Vlad")
                        .email("vlad@gmail.com")
                        .phoneNumber("+375293333333")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .id(1)
                                .color(CarColor.BLUE)
                                .brand(CarBrand.AUDI)
                                .carNumber("1234AB-1")
                                .isDeleted(false)
                                .build())
                        .isDeleted(false)
                        .build()
                ,
                Driver.builder()
                        .name("Kirill")
                        .email("kirill@gmail.com")
                        .phoneNumber("+375443333333")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .id(2)
                                .color(CarColor.BROWN)
                                .brand(CarBrand.FERRARI)
                                .carNumber("7890AB-7")
                                .isDeleted(false)
                                .build())
                        .isDeleted(false)
                        .build()
                ,
                Driver.builder()
                        .name("Dima")
                        .email("dima@gmail.com")
                        .phoneNumber("+375333333333")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .id(3)
                                .color(CarColor.GREEN)
                                .brand(CarBrand.MERCEDES_BENZ)
                                .carNumber("3333AB-3")
                                .isDeleted(true)
                                .build())
                        .isDeleted(true)
                        .build()
        );
    }

    @Test
    @SneakyThrows
    void getAllDriverRatingsTest_ReturnsDriverRatings() {
        //given
        List<Car> cars = defaultCars();
        List<Driver> drivers = defaultDrivers();
        for (int i = 0; i < cars.size(); i++) {
            Car car = carService.saveCar(cars.get(i));
            driverService.saveDriver(car.getId(), drivers.get(i));
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/driver/rating")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("Dima")),
                () -> assertTrue(responseContent.contains("0.0")),
                () -> assertTrue(responseContent.contains("0"))
        );
    }

    @Test
    @SneakyThrows
    void getAllNotDeletedDriverRatingsTest_ReturnsValidDriverRatings() {
        //given
        List<Car> cars = defaultCars();
        List<Driver> drivers = defaultDrivers();
        for (int i = 0; i < cars.size(); i++) {
            Car car = carService.saveCar(cars.get(i));
            driverService.saveDriver(car.getId(), drivers.get(i));
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/driver/rating/not-deleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertFalse(responseContent.contains("Dima")),
                () -> assertTrue(responseContent.contains("0.0")),
                () -> assertTrue(responseContent.contains("0"))
        );
    }

    @Test
    @SneakyThrows
    void getDriverRatingByIdTest_ReturnsDriverRating() {
        //given
        Car car = defaultCars().get(0);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(0);
        driver = driverService.saveDriver(car.getId(), driver);
        DriverRating driverRating = driverRatingRepository.findByDriverId(driver.getId()).get();

        MvcResult mvcResult = mockMvc.perform(get("/api/driver/rating/" + driverRating.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("0.0")),
                () -> assertTrue(responseContent.contains("0"))
        );
    }

    @Test
    @SneakyThrows
    void getDriverRatingByDriverIdTest_ReturnsPassengerRating() {
        //given
        Car car = defaultCars().get(1);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(1);
        driver = driverService.saveDriver(car.getId(), driver);

        MvcResult mvcResult = mockMvc.perform(get("/api/driver/rating/" + driver.getId() +
                        "/by-driver")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("0.0")),
                () -> assertTrue(responseContent.contains("0"))
        );
    }

    private final String driverRatingDto = """
                {
                    "rating_value": 5,
                    "number_of_ratings": 100
                }
            """;

    @Test
    @SneakyThrows
    void putDriverRatingByIdTest_ReturnsDriverRating() {
        //given
        Car car = defaultCars().get(0);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(0);
        driver = driverService.saveDriver(car.getId(), driver);
        DriverRating driverRating = driverRatingRepository.findByDriverId(driver.getId()).get();

        MvcResult mvcResult = mockMvc.perform(put("/api/driver/rating/" + driverRating.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverRatingDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("5")),
                () -> assertTrue(responseContent.contains("100"))
        );
    }

    @Test
    @SneakyThrows
    void patchDriverRatingByIdTest_ReturnsDriverRating() {
        //given
        Car car = defaultCars().get(0);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(0);
        driver = driverService.saveDriver(car.getId(), driver);
        DriverRating driverRating = driverRatingRepository.findByDriverId(driver.getId()).get();

        MvcResult mvcResult = mockMvc.perform(patch("/api/driver/rating/" + driverRating.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverRatingDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("5")),
                () -> assertTrue(responseContent.contains("100"))
        );
    }
}
