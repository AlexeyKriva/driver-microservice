package com.software.modsen.drivermicroservice.exceptions;

public class DriverAccountNotFoundException extends RuntimeException {
    public DriverAccountNotFoundException(String message) {
        super(message);
    }
}