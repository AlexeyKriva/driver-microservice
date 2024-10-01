package com.software.modsen.drivermicroservice.exceptions;

public class DriverWasDeletedException extends RuntimeException {
    public DriverWasDeletedException(String message) {
        super(message);
    }
}
