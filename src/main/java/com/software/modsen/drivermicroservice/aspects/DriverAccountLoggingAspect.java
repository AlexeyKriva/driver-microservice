package com.software.modsen.drivermicroservice.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DriverAccountLoggingAspect {
    @Pointcut("execution(* com.software.modsen.drivermicroservice.services.DriverAccountService.get*(..))")
    public void afterThrowingGettingServiceMethod() {
    }

    @AfterThrowing(pointcut = "afterThrowingGettingServiceMethod()", throwing = "throwable")
    public void afterThrowingGettingServiceMethodAdvice(JoinPoint joinPoint, Throwable throwable) {
        log.error("Method from {} with such parameters:\n" +
                        "{}\n" +
                        "{}\n" +
                        "ended with error: {}",
                joinPoint.getTarget(), joinPoint.getSignature(), joinPoint.getArgs(), throwable.getMessage()
        );
    }

    @Pointcut("target(com.software.modsen.drivermicroservice.controllers.DriverAccountController)")
    public void afterReturningAnyControllerEndpoint() {
    }

    @AfterReturning(pointcut = "afterReturningAnyControllerEndpoint()", returning = "returning")
    public void afterReturningAnyControllerEndpointAdvice(JoinPoint joinPoint, Object returning) {
        log.info("{} endpoint returned the result successfully!\nReturned result: {}.", joinPoint.getSignature(),
                returning.toString());
    }
}