package com.noviq.backend.common.exceptions;

public class OrganizationMemberAlreadyExistsException extends RuntimeException {
    public OrganizationMemberAlreadyExistsException(String message) {
        super(message);
    }
}

