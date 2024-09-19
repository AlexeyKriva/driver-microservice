package com.software.modsen.drivermicroservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.INVALID_TYPE_FOR_PARAMETER_MESSAGE;
import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.METHOD_NOT_SUPPORTED_MESSAGE;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<String> carNotFoundExceptionHandler(CarNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CarWasDeletedException.class)
    public ResponseEntity<String> carWasDeletedExceptionHandler(CarWasDeletedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.GONE);
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<String> driverNotFoundExceptionHandler(DriverNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DriverWasDeletedException.class)
    public ResponseEntity<String> driverWasDeletedExceptionHandler(DriverWasDeletedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.GONE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException
                                                                                exception) {
        return new ResponseEntity<>(exception.getMethod() + METHOD_NOT_SUPPORTED_MESSAGE, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException
                                                                             exception) {
        return new ResponseEntity<>(String.format(INVALID_TYPE_FOR_PARAMETER_MESSAGE, exception.getName(),
                exception.getRequiredType().getSimpleName(), exception.getValue()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> methodArgumentNotValidException(MethodArgumentNotValidException
                                                                               exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
