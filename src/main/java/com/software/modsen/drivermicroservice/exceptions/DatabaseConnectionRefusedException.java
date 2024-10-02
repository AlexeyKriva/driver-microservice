package com.software.modsen.drivermicroservice.exceptions;

public class DatabaseConnectionRefusedException extends RuntimeException {
    public DatabaseConnectionRefusedException(String message) {
        super(message);
    }
}