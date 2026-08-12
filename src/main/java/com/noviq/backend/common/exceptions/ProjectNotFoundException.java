package com.noviq.backend.common.exceptions;

public class ProjectNotFoundException  extends RuntimeException {
    public ProjectNotFoundException(Long id) {
        super("Organization with id " + id + " was not found.");
    } 
}
