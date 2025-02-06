package com.software.modsen.drivermicroservice.aspects;

import com.software.modsen.drivermicroservice.entities.driver.Driver;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DriverAspect {
    @Pointcut("within(com.software.modsen.drivermicroservice.services.DriverService) && @annotation(org.springframework.transaction.annotation.Transactional)")
    public void aroundAnyTransactionalMethod() {}

//    @Around("aroundAnyTransactionalMethod()")
//    public Driver aroundAnyTransactionalMethodAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
//        log.info("Transaction start!!!");
//
//        Driver driver = (Driver) joinPoint.proceed();
//
//        log.info("Transaction end!!!");
//
//        return driver;
//    }
}