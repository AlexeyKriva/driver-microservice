package com.software.modsen.drivermicroservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableRetry
@EnableWebMvc
@EnableCaching
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableConfigurationProperties
@OpenAPIDefinition(
        info = @Info(
                title = "Driver API",
                description = "Driver microservice for Modsen internship",
                contact = @Contact(
                        name = "Alexey Kryvetski",
                        email = "alexey.kriva03.com@gmail.com"
                )
        )
)
public class DriverMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DriverMicroserviceApplication.class, args);
    }
}