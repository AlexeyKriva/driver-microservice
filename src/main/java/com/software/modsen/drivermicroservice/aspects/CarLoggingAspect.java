package com.software.modsen.drivermicroservice.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CarLoggingAspect {
    @Pointcut("within(com.software.modsen.drivermicroservice.services.CarService)")
    public void afterAnyServiceMethod() {}

    @After("afterAnyServiceMethod()")
    public void afterAnyServiceMethodAdvice(JoinPoint joinPoint) {
        log.info("Method with name {} is successfully ended!", joinPoint.getSignature().getName());
    }
}