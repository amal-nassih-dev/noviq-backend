package com.noviq.backend.common.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task with id " + id + " was not found.");
    } 
}
