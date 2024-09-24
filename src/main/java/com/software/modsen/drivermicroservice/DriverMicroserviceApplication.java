package com.software.modsen.drivermicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class DriverMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverMicroserviceApplication.class, args);
    }

}
