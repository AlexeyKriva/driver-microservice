package com.software.modsen.drivermicroservice;

import com.software.modsen.drivermicroservice.controllers.DriverAccountController;
import com.software.modsen.drivermicroservice.controllers.DriverController;
import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.DriverDto;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.services.CarService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.wait.strategy.Wait.forListeningPort;

@AutoConfigureMessageVerifier
@Testcontainers
@SpringBootTest(classes = DriverMicroserviceApplication.class)
public class BaseTestClass {
    @Autowired
    private DriverController driverController;

    @Autowired
    private DriverAccountController driverAccountController;

    @Autowired
    private CarService carService;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15"))
            .withDatabaseName("cab-aggregator-db")
            .withUsername("postgres")
            .withPassword("98479847")
            .waitingFor(forListeningPort());

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    static int id = 1;

    @BeforeEach
    void setup() {
        carService.saveCar(Car.builder()
                .color(CarColor.WHITE)
                .brand(CarBrand.AUDI)
                .carNumber("1234AB-" + id)
                .build());
        driverController.saveDriver(new DriverDto("Ivan" + id, "ivan" + id + "@gmail.com",
                "+37529123987" + id, Sex.MALE, (long) id++));

        RestAssuredMockMvc.standaloneSetup(driverController, driverAccountController);
    }
}