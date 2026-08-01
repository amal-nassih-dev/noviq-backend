package com.noviq.backend.users.dto;

import com.noviq.backend.users.User;

public record UserResponse(Long id, String email, String fullName) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName());
    }
}