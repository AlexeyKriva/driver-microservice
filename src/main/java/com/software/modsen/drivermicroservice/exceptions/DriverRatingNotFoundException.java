package com.software.modsen.drivermicroservice.exceptions;

public class DriverRatingNotFoundException extends RuntimeException {
    public DriverRatingNotFoundException(String message) {
        super(message);
    }
}