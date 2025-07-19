package com.krazytop.bungie.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiErrorEnum {

    // Config
    CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "One or more constraints were violated.", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCHED("TYPE_MISMATCHED", "Type mismatched for one or more fields.", HttpStatus.BAD_REQUEST),
    MISSING_HEADER("MISSING_HEADER", "Header missed for one or more fields.", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED("VALIDATION_FAILED", "Validation failed for one or more fields.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "Method is not supported", HttpStatus.METHOD_NOT_ALLOWED),
    UNEXPECTED_RUNTIME_ERROR("UNEXPECTED_RUNTIME_ERROR", "An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("NOT_FOUND", "The requested resource was not found.", HttpStatus.NOT_FOUND),
    // Bungie
    BUNGIE_AUTH_ERROR("BUNGIE_AUTH_ERROR", "An unexpected error occurred while authenticating to Bungie. Please try again later or log out before trying again.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private final String code;
    private final String message;
    private final HttpStatus status;

}