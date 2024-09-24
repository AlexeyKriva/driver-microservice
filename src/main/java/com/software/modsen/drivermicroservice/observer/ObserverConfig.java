package com.software.modsen.drivermicroservice.observer;

import com.software.modsen.drivermicroservice.repositories.DriverRatingRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObserverConfig {
    @Bean
    public DriverSubject passengerSubject(DriverRatingRepository driverRatingRepository) {
        DriverSubject driverSubject = new DriverSubject();
        driverSubject.addDriverObserver(new DriverRatingObserver(driverRatingRepository));

        return driverSubject;
    }
}
