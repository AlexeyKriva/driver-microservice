package com.software.modsen.drivermicroservice.aspects;

import com.software.modsen.drivermicroservice.annotations.CacheableMethod;
import com.software.modsen.drivermicroservice.annotations.CacheableUpdateMethod;
import com.software.modsen.drivermicroservice.entities.driver.rating.DriverRating;
import com.software.modsen.drivermicroservice.services.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DriverRatingWithCachingAspect {
    private final RedisService redisService;

    @Pointcut("@annotation(com.software.modsen.drivermicroservice.annotations.CacheableMethod)")
    public void isCacheableMethod() {
    }

    @Pointcut("@annotation(com.software.modsen.drivermicroservice.annotations.CacheableUpdateMethod)")
    public void isCacheableUpdateMethod() {
    }

    @Pointcut("@annotation(com.software.modsen.drivermicroservice.annotations.CacheableDeleteMethod)")
    public void isCacheableDeleteMethod() {
    }

    @Around("isCacheableMethod()")
    public DriverRating cachingMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = "";

        for (Object arg: joinPoint.getArgs()) {
            if (arg.getClass().equals(Long.class)) {
                key = generateRedisKey(String.valueOf((Long) arg));
            }
        }

        DriverRating cachedDriverRating = (DriverRating) redisService.getFromCache(key);

        log.info("Redis key: {}", key);

        if (cachedDriverRating != null) {
            return cachedDriverRating;
        }

        DriverRating driverRating = (DriverRating) joinPoint.proceed();

        CacheableMethod annotation = ((MethodSignature) joinPoint.getSignature()).getMethod()
                .getAnnotation(CacheableMethod.class);

        if (driverRating != null) {
            redisService.saveToCache(key, driverRating, annotation.ttl(), annotation.timeUnit());
        }

        return driverRating;
    }

    @Around("isCacheableUpdateMethod()")
    public DriverRating cachingUpdateMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        String key = "";

        for (Object arg: args) {
            if (arg.getClass().equals(Long.class)) {
                key = generateRedisKey(String.valueOf((Long) arg));
            }
        }

        log.info("Redis key: {}", key);

        DriverRating driverRating = (DriverRating) joinPoint.proceed();

        CacheableUpdateMethod annotation = ((MethodSignature) joinPoint.getSignature()).getMethod()
                .getAnnotation(CacheableUpdateMethod.class);

        if (driverRating != null) {
            redisService.saveToCache(key, driverRating, annotation.ttl(), annotation.timeUnit());
        }

        return driverRating;
    }

    @Around("isCacheableDeleteMethod()")
    public DriverRating cachingDeleteMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        String key = "";

        for (Object arg: args) {
            if (arg.getClass().equals(Long.class)) {
                key = generateRedisKey(String.valueOf((Long) arg));
            }
        }

        log.info("Redis key: {}", key);

        DriverRating driverRating = (DriverRating) joinPoint.proceed();

        redisService.invalidateCache(key);

        return driverRating;
    }

    private String generateRedisKey(String id) {
        return "driverRating" + ":" + id;
    }
}