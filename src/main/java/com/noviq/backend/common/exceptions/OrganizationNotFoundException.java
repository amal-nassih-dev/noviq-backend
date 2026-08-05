package com.noviq.backend.common.exceptions;

public class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException(Long id) {
        super("Organization with id " + id + " was not found.");
    }
}