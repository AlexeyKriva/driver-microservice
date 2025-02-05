package com.software.modsen.drivermicroservice.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DriverRatingAspect {
    @Pointcut("within(com.software.modsen.drivermicroservice.services.DriverRatingService)")
    public void isDriverRatingService() {}

    @Pointcut("isDriverRatingService() && " +
            "args(long, com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating)")
    public void beforeAnyServiceMethod() {}

    @Before("beforeAnyServiceMethod()")
    public void beforeAnyServiceMethodAdvice() {
        log.info("We are updating driver rating now!!!");
    }
}