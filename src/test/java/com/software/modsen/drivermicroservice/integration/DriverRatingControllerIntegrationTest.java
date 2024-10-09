package com.software.modsen.drivermicroservice.integration;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DriverRatingControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarService carService;

    @Autowired
    private DriverService driverService;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15"))
            .withDatabaseName("cab-aggregator-db")
            .withUsername("postgres")
            .withPassword("98479847");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    static boolean isAlreadySetUped = false;

    @BeforeEach
    void setUp() {
        if (!isAlreadySetUped) {
            List<Driver> drivers = defaultDrivers();
            long carId = 1;
            List<Car> cars = defaultCars();
            for (Car car : cars) {
                carService.saveCar(car);
            }
            for (Driver driver : drivers) {
                driverService.saveDriver(carId++, driver);
            }

            isAlreadySetUped = true;
        }
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
    @Order(1)
    @SneakyThrows
    void getAllDriverRatingsTest_ReturnsDriverRatings() {
        //given
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
    @Order(2)
    @SneakyThrows
    void getAllNotDeletedDriverRatingsTest_ReturnsValidDriverRatings() {
        //given
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
    @Order(3)
    @SneakyThrows
    void getDriverRatingByIdTest_ReturnsDriverRating() {
        //given
        MvcResult mvcResult = mockMvc.perform(get("/api/driver/rating/1")
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
    @Order(4)
    @SneakyThrows
    void getDriverRatingByDriverIdTest_ReturnsPassengerRating() {
        //given
        MvcResult mvcResult = mockMvc.perform(get("/api/driver/rating/2/by-driver")
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
    @Order(5)
    @SneakyThrows
    void putDriverRatingByIdTest_ReturnsDriverRating() {
        //given
        MvcResult mvcResult = mockMvc.perform(put("/api/driver/rating/1")
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
    @Order(6)
    @SneakyThrows
    void patchDriverRatingByIdTest_ReturnsDriverRating() {
        //given
        MvcResult mvcResult = mockMvc.perform(patch("/api/driver/rating/1")
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
