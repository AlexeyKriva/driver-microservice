package com.software.modsen.drivermicroservice.integration;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.services.CarService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

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

    static boolean isAlreadySetUped = false;

    @BeforeEach
    void setUp() {
        if (!isAlreadySetUped) {
            List<Car> cars = defaultCars();
            for (Car car : cars) {
                carService.saveCar(car);
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
                        .build(),
                Car.builder()
                        .color(CarColor.WHITE)
                        .brand(CarBrand.VOLKSWAGEN)
                        .carNumber("3333TY-3")
                        .isDeleted(false)
                        .build()
        );
    }

    @Test
    @Order(1)
    @SneakyThrows
    void getAllCarsTest_ReturnsCars() {
        //given
        MvcResult mvcResult = mockMvc.perform(get("/api/car")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("BLUE")),
                () -> assertTrue(responseContent.contains("AUDI")),
                () -> assertTrue(responseContent.contains("1234AB-1")),
                () -> assertTrue(responseContent.contains("BROWN")),
                () -> assertTrue(responseContent.contains("FERRARI")),
                () -> assertTrue(responseContent.contains("7890AB-7")),
                () -> assertTrue(responseContent.contains("GREEN")),
                () -> assertTrue(responseContent.contains("MERCEDES_BENZ")),
                () -> assertTrue(responseContent.contains("3333AB-3")),
                () -> assertTrue(responseContent.contains("WHITE")),
                () -> assertTrue(responseContent.contains("VOLKSWAGEN")),
                () -> assertTrue(responseContent.contains("3333TY-3"))
        );
    }

    @Test
    @Order(2)
    @SneakyThrows
    void getAllNotDeletedCarsTest_ReturnsValidCars() {
        //given
        MvcResult mvcResult = mockMvc.perform(get("/api/car/not-deleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("BLUE")),
                () -> assertTrue(responseContent.contains("AUDI")),
                () -> assertTrue(responseContent.contains("1234AB-1")),
                () -> assertTrue(responseContent.contains("BROWN")),
                () -> assertTrue(responseContent.contains("FERRARI")),
                () -> assertTrue(responseContent.contains("7890AB-7")),
                () -> assertFalse(responseContent.contains("GREEN")),
                () -> assertFalse(responseContent.contains("MERCEDES_BENZ")),
                () -> assertFalse(responseContent.contains("3333AB-3")),
                () -> assertTrue(responseContent.contains("WHITE")),
                () -> assertTrue(responseContent.contains("VOLKSWAGEN")),
                () -> assertTrue(responseContent.contains("3333TY-3"))
        );
    }

    @Test
    @Order(3)
    @SneakyThrows
    void getCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(get("/api/car/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println(responseContent);

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("BLUE")),
                () -> assertTrue(responseContent.contains("AUDI")),
                () -> assertTrue(responseContent.contains("1234AB-1"))
        );
    }

    private final String carDto = """
                {
                    "color": "YELLOW",
                    "brand": "BMW",
                    "car_number": "A123AB-3"
                }
            """;

    @Test
    @Order(4)
    @SneakyThrows
    void saveCarTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(post("/api/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("5")),
                () -> assertTrue(responseContent.contains("YELLOW")),
                () -> assertTrue(responseContent.contains("BMW")),
                () -> assertTrue(responseContent.contains("A123AB-3")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }

    private final String carUpdateDto = """
                {
                    "color": "RED",
                    "brand": "LAMBORGHINI",
                    "car_number": "3123AB-3"
                }
            """;

    @Test
    @Order(5)
    @SneakyThrows
    void updateCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(put("/api/car/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carUpdateDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("2")),
                () -> assertTrue(responseContent.contains("RED")),
                () -> assertTrue(responseContent.contains("LAMBORGHINI")),
                () -> assertTrue(responseContent.contains("3123AB-3")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }

    private final String carPatchDto = """
                {
                    "color": "LIGHT_BLUE",
                    "brand": "LEXUS"
                }
            """;

    @Test
    @Order(6)
    @SneakyThrows
    void patchCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(patch("/api/car/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carPatchDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("1")),
                () -> assertTrue(responseContent.contains("LIGHT_BLUE")),
                () -> assertTrue(responseContent.contains("LEXUS")),
                () -> assertTrue(responseContent.contains("1234AB-1")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }

    @Test
    @Order(7)
    @SneakyThrows
    void softDeleteCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(post("/api/car/4/soft-delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("4")),
                () -> assertTrue(responseContent.contains("WHITE")),
                () -> assertTrue(responseContent.contains("VOLKSWAGEN")),
                () -> assertTrue(responseContent.contains("3333TY-3")),
                () -> assertTrue(responseContent.contains("true"))
        );
    }

    @Test
    @Order(8)
    @SneakyThrows
    void softRecoveryCarByIdTest_ReturnsCar() {
        //given
        MvcResult mvcResult = mockMvc.perform(post("/api/car/3/soft-recovery")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("3")),
                () -> assertTrue(responseContent.contains("GREEN")),
                () -> assertTrue(responseContent.contains("MERCEDES_BENZ")),
                () -> assertTrue(responseContent.contains("3333AB-3")),
                () -> assertTrue(responseContent.contains("false"))
        );
    }
}