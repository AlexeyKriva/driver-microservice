package com.software.modsen.drivermicroservice.annotations;

import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheableMethod {
    long ttl() default 10;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}