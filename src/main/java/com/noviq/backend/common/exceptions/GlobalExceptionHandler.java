package com.noviq.backend.common.exceptions;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.noviq.backend.common.ErrorResponse;
import com.noviq.backend.common.ValidationErrorResponse;

import java.time.Instant;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
                    Instant.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    ex.getMessage(),
                    request.getRequestURI()
            )); 
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
        .getFieldErrors()
        .forEach(error ->
            fieldErrors.put(
                error.getField(),
                error.getDefaultMessage()
            ));

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Validation failed",
                        request.getRequestURI(),
                        fieldErrors
                );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse>  handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                    Instant.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    ex.getMessage(),
                    request.getRequestURI()
            ));
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrganizationNotFound(
            OrganizationNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(OrganizationAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleOrganizationAlreadyExists(
            OrganizationAlreadyExistsException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "An unexpected error occurred.",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Malformed JSON request.",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "User Not found.",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(OrganizationMemberAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleMemberAlreadyExist(
            OrganizationMemberAlreadyExistsException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        "User is already a member of this organization.",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(OrganizationMemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(
            OrganizationMemberNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "User is not a member of this organization.",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(InvalidOrganizationRoleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrganizationRole(
            InvalidOrganizationRoleException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "The requested organization role is invalid.",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrganizationRole(
            ProjectNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "Project does not exist",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrganizationRole(
            TaskNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "Task does not exist",
                        request.getRequestURI()
                ));
    }
}
