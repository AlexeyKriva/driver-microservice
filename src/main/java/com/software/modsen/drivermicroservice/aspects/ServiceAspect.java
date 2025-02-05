package com.software.modsen.drivermicroservice.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Aspect
@Component
public class ServiceAspect {
    private final static long NANOS_IN_ONE_MILLISECOND = 1_000_000;

    @Pointcut("within(com.software.modsen.drivermicroservice.services.*)")
    public void beforeAndAfterAnyServices() {}

    @Before("beforeAndAfterAnyServices()")
    public void beforeAnyServicesAdviceTest() {
        log.warn("Test before!");
    }

    @After("beforeAndAfterAnyServices()")
    public void afterAnyServicesAdviceTest() {
        log.warn("Test after!");
    }

    @Around("beforeAndAfterAnyServices()")
    public Object calculateMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = Instant.now().getNano();

        log.info("Starting to run method {}.", joinPoint.getSignature().getName());

        Object object = joinPoint.proceed();

        long endTime = Instant.now().getNano();
        long executionTimeInMillis = (endTime - startTime) / NANOS_IN_ONE_MILLISECOND;

        log.info("Method {} worked in {} milliseconds", joinPoint.getSignature().getName(),
                executionTimeInMillis);

        return object;
    }
}