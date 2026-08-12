package com.noviq.backend.common.exceptions;

public class InvalidOrganizationRoleException extends RuntimeException {
    public InvalidOrganizationRoleException(String message) {
        super(message);
    }
}
