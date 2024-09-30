package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import com.software.modsen.drivermicroservice.repositories.DriverRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfig {
    @Bean
    public DriverSubject passengerSubject(DriverRatingRepository driverRatingRepository,
                                          DriverRepository driverRepository) {
        DriverSubject driverSubject = new DriverSubject();
        driverSubject.addDriverObserver(new DriverRatingObserver(driverRatingRepository, driverRepository));

        return driverSubject;
    }
}