package com.noviq.backend.auth.dto;

import com.noviq.backend.users.dto.UserResponse;

public record AuthenticationResponse(String token, UserResponse user) {
}
