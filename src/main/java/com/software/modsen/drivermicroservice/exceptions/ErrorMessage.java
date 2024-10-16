package com.software.modsen.drivermicroservice.exceptions;

public class ErrorMessage {
    public static final String CAR_NOT_FOUND_MESSAGE = "Car not found.";
    public static final String CAR_WAS_DELETED_MESSAGE = "Car was deleted.";
    public static final String DRIVER_NOT_FOUND_MESSAGE = "Driver not found.";
    public static final String DRIVER_WAS_DELETED_MESSAGE = "Driver was deleted.";
    public static final String DRIVER_RATING_NOT_FOUND_MESSAGE = "Driver rating not found.";

    public static final String METHOD_NOT_SUPPORTED_MESSAGE = " method is not supported.";
    public static final String INVALID_TYPE_FOR_PARAMETER_MESSAGE = "Invalid value for parameter '%s'. Expected type:" +
            " %s, but got: %s.";
    public static final String DATA_INTEGRITY_VIOLENT_MESSAGE = "This car number, email or phone number" +
            " has already been registered.";
    public static final String JSON_MAPPING_MESSAGE = "Invalid data in JSON";
    public static final String REQUEST_RESOURCE_NOT_FOUND_MESSAGE = "The requested resource was not found. Please" +
            " check the URL and try again.";
    public static final String INVALID_JSON_FORMAT = "Invalid json format.";

    public static final String DRIVER_ACCOUNT_NOT_FOUND_MESSAGE = "Driver account not found.";

    public static final String INSUFFICIENT_ACCOUNT_BALANCE_EXCEPTION = "Insufficient account balance.";

    public static final String BAD_CONNECTION_TO_DATABASE_MESSAGE = "Unsuccessful attempt to connect to the database. " +
            "Please, wait and try again later.";

    public static final String CANNOT_GET_DATA_MESSAGE = " For this reason you cannot get the data.";
    public static final String CANNOT_UPDATE_DATA_MESSAGE = " For this reason you cannot update the data.";
}