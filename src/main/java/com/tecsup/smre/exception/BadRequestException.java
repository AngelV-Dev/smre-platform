package com.tecsup.smre.exception;

public class BadRequestException extends DomainException {
    public BadRequestException(String message) {
        super(message);
    }
}
