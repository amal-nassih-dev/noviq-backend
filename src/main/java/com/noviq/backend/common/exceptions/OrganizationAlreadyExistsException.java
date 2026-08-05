package com.noviq.backend.common.exceptions;

public class OrganizationAlreadyExistsException extends RuntimeException {

    public OrganizationAlreadyExistsException(String message) {
        super(message);
    }
}