package com.noviq.backend.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.json.JsonMapper; 
import com.noviq.backend.auth.dto.AuthenticationResponse;
import com.noviq.backend.auth.dto.LoginRequest;
import com.noviq.backend.auth.dto.RegistrationRequest;
import com.noviq.backend.auth.service.AuthenticationService;
import com.noviq.backend.common.exceptions.EmailAlreadyExistsException;
import com.noviq.backend.users.dto.UserResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc; // it is like a mock postman operation inside java
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@AutoConfigureMockMvc // this will enable spring to create the bean for MockMvc
@SpringBootTest // when this is used spring cannot autowire the objects so we will need to use AutoConfigureRestTestClient
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper; // we need this to send a stringified json instead of object you will need spring-boot-web 

    @MockitoBean
    private AuthenticationService authenticationService; // instead of creating a fake bean and it is at the application level unlike Mock this is used for Integration tests

    @Test
    void register_shouldReturnAuthenticationResponse() throws Exception {

        RegistrationRequest request = new RegistrationRequest(
                "amal@example.com",
                "Amal Nassih",
                "Password123!"
        );

        UserResponse user = new UserResponse(
                1L,
                "amal@example.com",
                "Amal Nassih"
        );

        AuthenticationResponse response =
                new AuthenticationResponse("fake-token", user);

        when(authenticationService.register(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("fake-token"))
                .andExpect(jsonPath("$.user.email").value("amal@example.com"))
                .andExpect(jsonPath("$.user.fullName").value("Amal Nassih"));

        verify(authenticationService).register(any());
    }

    @Test
    void register_shouldReturnConflictWhenEmailExists() throws Exception {

        RegistrationRequest request = new RegistrationRequest(
                "amal@example.com",
                "Amal Nassih",
                "Password123!"
        );

        when(authenticationService.register(any()))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(authenticationService).register(any());
    }

    @Test
    void register_shouldReturnBadRequestForInvalidInput() throws Exception {

        RegistrationRequest request = new RegistrationRequest(
                "bad-email",
                "",
                "123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnAuthenticationResponse() throws Exception {

        LoginRequest request = new LoginRequest(
                "amal@example.com",
                "Password123!"
        );

        UserResponse user = new UserResponse(
                1L,
                "amal@example.com",
                "Amal Nassih"
        );

        AuthenticationResponse response =
                new AuthenticationResponse("fake-token", user);

        when(authenticationService.login(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"))
                .andExpect(jsonPath("$.user.email").value("amal@example.com"));

        verify(authenticationService).login(any());
    }

    @Test
    void login_shouldReturnUnauthorizedForBadCredentials() throws Exception {

        LoginRequest request = new LoginRequest(
                "amal@example.com",
                "wrong-password"
        );

        when(authenticationService.login(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authenticationService).login(any());
    }

    @Test
    void login_shouldReturnBadRequestForInvalidInput() throws Exception {

        LoginRequest request = new LoginRequest(
                "",
                ""
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}