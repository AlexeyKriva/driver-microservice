package com.software.modsen.drivermicroservice.integration;

import com.software.modsen.drivermicroservice.entities.car.Car;
import com.software.modsen.drivermicroservice.entities.car.CarBrand;
import com.software.modsen.drivermicroservice.entities.car.CarColor;
import com.software.modsen.drivermicroservice.entities.driver.Driver;
import com.software.modsen.drivermicroservice.entities.driver.Sex;
import com.software.modsen.drivermicroservice.entities.driver.account.DriverAccount;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DriverAccountControllerIntegrationTest extends TestconteinersConfig {
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
                        .build()
        );
    }

    @Test
    @SneakyThrows
    void getAllDriverAccountsTest_ReturnsDriverAccounts() {
        //given
        List<Car> cars = defaultCars();
        List<Driver> drivers = defaultDrivers();
        for (int i = 0; i < cars.size(); i++) {
            Car car = carService.saveCar(cars.get(i));
            driverService.saveDriver(car.getId(), drivers.get(i));
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/drivers/accounts?includeDeleted=true")
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
                () -> assertTrue(responseContent.contains("BYN"))
        );
    }

    @Test
    @SneakyThrows
    void getAllNotDeletedDriverAccountsTest_ReturnsDriverAccounts() {
        //given
        List<Car> cars = defaultCars();
        List<Driver> drivers = defaultDrivers();
        for (int i = 0; i < cars.size(); i++) {
            Car car = carService.saveCar(cars.get(i));
            driverService.saveDriver(car.getId(), drivers.get(i));
        }

        MvcResult mvcResult = mockMvc.perform(get("/api/drivers/accounts?includeDeleted=false")
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
                () -> assertTrue(responseContent.contains("BYN"))
        );
    }

    @Test
    @SneakyThrows
    void getNotDeletedDriverAccountsByIdTest_ReturnsDriverAccount() {
        //given
        Car car = defaultCars().get(0);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(0);
        driver = driverService.saveDriver(car.getId(), driver);
        DriverAccount driverAccount = driverAccountRepository.findByDriverId(driver.getId()).get();

        MvcResult mvcResult = mockMvc.perform(get("/api/drivers/accounts/" + driverAccount.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("0.0")),
                () -> assertTrue(responseContent.contains("BYN"))
        );
    }

    @Test
    @SneakyThrows
    void getNotDeletedDriverAccountsByDriverIdTest_ReturnsDriverAccount (){
        //given
        Car car = defaultCars().get(1);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(1);
        driver = driverService.saveDriver(car.getId(), driver);

        MvcResult mvcResult = mockMvc.perform(get("/api/drivers/" + driver.getId() +
                        "/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("0.0")),
                () -> assertTrue(responseContent.contains("BYN"))
        );
    }

    private final String passengerAccountBalanceUpDto = """
            {
                "balance": 1000.0,
                "currency": "BYN"
            }
            """;

    private final String passengerAccountBalanceDownDto = """
            {
                "balance": 800.0,
                "currency": "BYN"
            }
            """;

    @Test
    @SneakyThrows
    void increaseBalanceByDriverIdTest_ReturnsDriverAccount() {
        //given
        Car car = defaultCars().get(0);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(0);
        driver = driverService.saveDriver(car.getId(), driver);

        MvcResult mvcResult = mockMvc.perform(put("/api/drivers/" + driver.getId() +
                        "/accounts/up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerAccountBalanceUpDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Vlad")),
                () -> assertTrue(responseContent.contains("1000.0")),
                () -> assertTrue(responseContent.contains("BYN"))
        );
    }

    @Test
    @SneakyThrows
    void cancelBalanceByPassengerIdTest_ReturnsPassengerAccount() {
        //given
        Car car = defaultCars().get(1);
        car = carService.saveCar(car);
        Driver driver = defaultDrivers().get(1);
        driver = driverService.saveDriver(car.getId(), driver);

        mockMvc.perform(put("/api/drivers/" + driver.getId() +
                "/accounts/up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(passengerAccountBalanceUpDto));

        MvcResult mvcResult = mockMvc.perform(put("/api/drivers/" + driver.getId() +
                        "/accounts/down")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerAccountBalanceDownDto))
                .andExpect(status().isOk())
                .andReturn();

        //when
        String responseContent = mvcResult.getResponse().getContentAsString();

        //then
        assertAll("Check response content",
                () -> assertTrue(responseContent.contains("Kirill")),
                () -> assertTrue(responseContent.contains("200.0")),
                () -> assertTrue(responseContent.contains("BYN"))
        );
    }
}
