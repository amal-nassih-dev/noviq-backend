package com.noviq.backend.users.dto;

import com.noviq.backend.users.User;

public record UserSearchResponse(
    Long id,
    String email,
    String fullName
) {

    public static UserSearchResponse from(User user) {
        return new UserSearchResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName()
        );
    }
}