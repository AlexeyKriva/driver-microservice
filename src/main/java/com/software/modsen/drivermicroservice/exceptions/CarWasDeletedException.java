package com.software.modsen.drivermicroservice.exceptions;

public class CarWasDeletedException extends RuntimeException {
    public CarWasDeletedException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
