package com.software.modsen.drivermicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableDiscoveryClient
@EnableRetry
@EnableTransactionManagement
public class DriverMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverMicroserviceApplication.class, args);
    }

}
