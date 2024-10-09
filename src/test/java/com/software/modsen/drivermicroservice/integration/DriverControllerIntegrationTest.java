package com.software.modsen.drivermicroservice.integration;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.services.CarService;
import com.software.modsen.drivermicroservice.services.DriverService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
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
@Testcontainers
@Transactional
public class DriverControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CarService carService;

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

    @BeforeEach
    void setUp() {
        List<Driver> drivers = defaultDrivers();
        long carId = 1;
        List<Car> cars = defaultCars();
        for (Car car: cars) {
            carService.saveCar(car);
        }
        for (Driver driver : drivers) {
            driverService.saveDriver(carId++, driver);
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
    void getAllDriversTest_ReturnsDrivers() {
        //given
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
                () -> assertTrue(responseContent.contains("MERCEDES_BENZ"))
        );
    }

    @Test
    @SneakyThrows
    void getAllNotDeletedDriversTest_ReturnsValidDrivers() {
        //given
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
                () -> assertFalse(responseContent.contains("MERCEDES_BENZ"))
        );
    }

    @Test
    @SneakyThrows
    void getDriverByIdTest_ReturnsDriver() {
        //given
        MvcResult mvcResult = mockMvc.perform(get("/api/driver/1")
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

    private final String driverDto = """
                {
                    "name": "Danik",
                    "email": "danik@gmail.com",
                    "phone_number": "+375443377999",
                    "sex": "MALE",
                    "car_id": 4
                }
            """;

    @Test
    @SneakyThrows
    void saveCarTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(post("/api/driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("4")),
                () -> assertTrue(responseContent.contains("Danik")),
                () -> assertTrue(responseContent.contains("danik@gmail.com")),
                () -> assertTrue(responseContent.contains("+375443377999")),
                () -> assertTrue(responseContent.contains("ROLLS_ROYCE")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }

    @Test
    @SneakyThrows
    void updateCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(put("/api/driver/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("2")),
                () -> assertTrue(responseContent.contains("Danik")),
                () -> assertTrue(responseContent.contains("danik@gmail.com")),
                () -> assertTrue(responseContent.contains("+375443377999")),
                () -> assertTrue(responseContent.contains("ROLLS_ROYCE")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }

    private final String driverPatchDto = """
                {
                    "name": "Danik",
                    "email": "danik@gmail.com",
                    "phone_number": "+375443377999"
                }
            """;

    @Test
    @SneakyThrows
    void patchCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(patch("/api/driver/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverPatchDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("2")),
                () -> assertTrue(responseContent.contains("Danik")),
                () -> assertTrue(responseContent.contains("danik@gmail.com")),
                () -> assertTrue(responseContent.contains("+375443377999")),
                () -> assertTrue(responseContent.contains("FERRARI")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }

    @Test
    @SneakyThrows
    void softDeleteCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(post("/api/driver/1/soft-delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("1")),
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("vlad@gmail.com")),
                () -> assertTrue(responseContent.contains("+375293333333")),
                () -> assertTrue(responseContent.contains("AUDI")),
                () -> assertTrue(responseContent.contains("true"))
        );
    }

    @Test
    @SneakyThrows
    void softRecoveryCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(post("/api/driver/3/soft-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverPatchDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("3")),
                () -> assertTrue(responseContent.contains("Dima")),
                () -> assertTrue(responseContent.contains("dima@gmail.com")),
                () -> assertTrue(responseContent.contains("+375333333333")),
                () -> assertTrue(responseContent.contains("MERCEDES_BENZ")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }
}
