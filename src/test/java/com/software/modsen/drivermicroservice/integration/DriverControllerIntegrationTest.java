package com.software.modsen.drivermicroservice.integration;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
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
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DriverControllerIntegrationTest extends TestconteinersConfig {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CarService carService;

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
                        .build(),
                Car.builder()
                        .color(CarColor.BLACK)
                        .brand(CarBrand.ROLLS_ROYCE)
                        .carNumber("3333CD-3")
                        .isDeleted(false)
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
                                .color(CarColor.GREEN)
                                .brand(CarBrand.MERCEDES_BENZ)
                                .carNumber("3333AB-3")
                                .isDeleted(true)
                                .build())
                        .isDeleted(true)
                        .build(),
                Driver.builder()
                        .name("Sergei")
                        .email("sergei@gmail.com")
                        .phoneNumber("+375297778123")
                        .sex(Sex.MALE)
                        .car(Car.builder()
                                .color(CarColor.BLACK)
                                .brand(CarBrand.ROLLS_ROYCE)
                                .carNumber("3333CD-3")
                                .isDeleted(false)
                                .build())
                        .isDeleted(false)
                        .build()
        );
    }

    @Test
    @SneakyThrows
    void getAllDriversTest_ReturnsDrivers() {
        //given
        List<Car> cars = defaultCars();
        List<Driver> drivers = defaultDrivers();
        for (int i = 0; i < cars.size(); i++) {
            Car car = carService.saveCar(cars.get(i));
            driverService.saveDriver(car.getId(), drivers.get(i));
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/driver")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("vlad@gmail.com")),
                () -> assertTrue(responseContent.contains("+375293333333")),
                () -> assertTrue(responseContent.contains("AUDI")),
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("kirill@gmail.com")),
                () -> assertTrue(responseContent.contains("+375443333333")),
                () -> assertTrue(responseContent.contains("FERRARI")),
                () -> assertTrue(responseContent.contains("Dima")),
                () -> assertTrue(responseContent.contains("dima@gmail.com")),
                () -> assertTrue(responseContent.contains("+375333333333")),
                () -> assertTrue(responseContent.contains("MERCEDES_BENZ")),
                () -> assertTrue(responseContent.contains("Sergei")),
                () -> assertTrue(responseContent.contains("sergei@gmail.com")),
                () -> assertTrue(responseContent.contains("+375297778123")),
                () -> assertTrue(responseContent.contains("ROLLS_ROYCE"))
        );
    }

    @Test
    @SneakyThrows
    void getAllNotDeletedDriversTest_ReturnsValidDrivers() {
        //given
        List<Car> cars = defaultCars();
        List<Driver> drivers = defaultDrivers();
        for (int i = 0; i < cars.size(); i++) {
            Car car = carService.saveCar(cars.get(i));
            driverService.saveDriver(car.getId(), drivers.get(i));
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/driver/not-deleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("vlad@gmail.com")),
                () -> assertTrue(responseContent.contains("+375293333333")),
                () -> assertTrue(responseContent.contains("AUDI")),
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("kirill@gmail.com")),
                () -> assertTrue(responseContent.contains("+375443333333")),
                () -> assertTrue(responseContent.contains("FERRARI")),
                () -> assertFalse(responseContent.contains("Dima")),
                () -> assertFalse(responseContent.contains("dima@gmail.com")),
                () -> assertFalse(responseContent.contains("+375333333333")),
                () -> assertFalse(responseContent.contains("MERCEDES_BENZ")),
                () -> assertTrue(responseContent.contains("sergei@gmail.com")),
                () -> assertTrue(responseContent.contains("+375297778123")),
                () -> assertTrue(responseContent.contains("ROLLS_ROYCE"))
        );
    }

    @Test
    @SneakyThrows
    void getDriverByIdTest_ReturnsDriver() {
        //given
        Car car = defaultCars().get(0);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(0);
        driver = driverService.saveDriver(car.getId(), driver);

        MvcResult mvcResult = mockMvc.perform(get("/api/driver/" + driver.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println(responseContent);

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("vlad@gmail.com")),
                () -> assertTrue(responseContent.contains("+375293333333")),
                () -> assertTrue(responseContent.contains("AUDI"))
        );
    }

    private String driverDto = """
                {
                    "name": "Danik",
                    "email": "danik@gmail.com",
                    "phoneNumber": "+375443377999",
                    "sex": "MALE",
            """;

    @Test
    @SneakyThrows
    void saveDriverTest_ReturnsDriver() {
        //given
        Car car = defaultCars().get(0);
        car = carService.saveCar(car);

        driverDto += "\"carId\": " + car.getId() + "}";

        MvcResult mvcResult = mockMvc.perform(post("/api/driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Danik")),
                () -> assertTrue(responseContent.contains("danik@gmail.com")),
                () -> assertTrue(responseContent.contains("+375443377999")),
                () -> assertTrue(responseContent.contains("AUDI"))
        );
    }

    private String driverUpdateDto = """
                {
                    "name": "Nikita",
                    "email": "nikita@gmail.com",
                    "phoneNumber": "+375447655431",
                    "sex": "MALE",
            """;

    @Test
    @SneakyThrows
    void updateDriverByIdTest_ReturnsDriver() {
        //given
        Car car = defaultCars().get(1);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(1);
        driver = driverService.saveDriver(car.getId(), driver);

        driverUpdateDto += "\"carId\": " + car.getId() + "}";

        MvcResult mvcResult = mockMvc.perform(put("/api/driver/" + driver.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverUpdateDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Nikita")),
                () -> assertTrue(responseContent.contains("nikita@gmail.com")),
                () -> assertTrue(responseContent.contains("+375447655431")),
                () -> assertTrue(responseContent.contains("FERRARI"))
        );
    }

    private final String driverPatchDto = """
                {
                    "name": "Alexandr",
                    "email": "alexandr@gmail.com"
                }
            """;

    @Test
    @SneakyThrows
    void patchCarByIdTest_ReturnsCar() {
        //given
        Car car = defaultCars().get(0);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(0);
        driver = driverService.saveDriver(car.getId(), driver);

        MvcResult mvcResult = mockMvc.perform(patch("/api/driver/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverPatchDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Alexandr")),
                () -> assertTrue(responseContent.contains("alexandr@gmail.com")),
                () -> assertTrue(responseContent.contains("+375293333333")),
                () -> assertTrue(responseContent.contains("AUDI"))
        );
    }

    @Test
    @SneakyThrows
    void softDeleteCarByIdTest_ReturnsCar() {
        //given
        Car car = defaultCars().get(3);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(3);
        driver = driverService.saveDriver(car.getId(), driver);

        MvcResult mvcResult = mockMvc.perform(post("/api/driver/" + driver.getId() +
                        "/soft-delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Sergei")),
                () -> assertTrue(responseContent.contains("sergei@gmail.com")),
                () -> assertTrue(responseContent.contains("+375297778123")),
                () -> assertTrue(responseContent.contains("ROLLS_ROYCE")),
                () -> assertTrue(responseContent.contains("true"))
        );
    }

    @Test
    @SneakyThrows
    void softRecoveryCarByIdTest_ReturnsCar() {
        //given
        Car car = defaultCars().get(2);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(2);
        driver = driverService.saveDriver(car.getId(), driver);

        MvcResult mvcResult = mockMvc.perform(post("/api/driver/" + driver.getId() +
                        "/soft-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverPatchDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Dima")),
                () -> assertTrue(responseContent.contains("dima@gmail.com")),
                () -> assertTrue(responseContent.contains("+375333333333")),
                () -> assertTrue(responseContent.contains("MERCEDES_BENZ")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }
}