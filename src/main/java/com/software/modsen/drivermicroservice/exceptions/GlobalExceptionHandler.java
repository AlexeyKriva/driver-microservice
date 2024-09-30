package com.software.modsen.drivermicroservice.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.software.modsen.drivermicroservice.exceptions.ErrorMessage.*;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException exception) {
        return new ResponseEntity<>(DATA_INTEGRITY_VIOLENT_MESSAGE, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<String> jsonMappingExceptionHandler(JsonMappingException exception) {
        return new ResponseEntity<>(JSON_MAPPING_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(NoHandlerFoundException exception) {
        return new ResponseEntity<>(REQUEST_RESOURCE_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> httpMessageNotReadableExceptionMessage(HttpMessageNotReadableException
                                                                                 exception) {
        return new ResponseEntity<>(INVALID_JSON_FORMAT, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DriverRatingNotFoundException.class)
    public ResponseEntity<String> driverRatingNotFoundExceptionHandler(DriverRatingNotFoundException
                                                                       exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
