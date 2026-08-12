package com.noviq.backend.common.exceptions;

public class OrganizationMemberNotFoundException extends RuntimeException {
    public OrganizationMemberNotFoundException(String message) {
        super(message);
    }
}

