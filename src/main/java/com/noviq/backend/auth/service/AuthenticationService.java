package com.noviq.backend.auth.service;

import com.noviq.backend.auth.dto.RegistrationRequest;
import com.noviq.backend.auth.dto.AuthenticationResponse;
import com.noviq.backend.auth.dto.LoginRequest;

public interface AuthenticationService {

    public AuthenticationResponse login(LoginRequest request);
    public AuthenticationResponse register(RegistrationRequest request);
}
