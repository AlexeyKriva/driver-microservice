package com.software.modsen.drivermicroservice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheableUpdateMethod {
    long ttl() default 10;
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
