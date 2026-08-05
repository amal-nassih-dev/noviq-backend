package com.noviq.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.noviq.backend.users.Role;
import com.noviq.backend.users.User;

import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("THIS_IS_MY_SUPER_SECRET_KEY_12345678901234567890");
        properties.setExpiration(86400000);

        jwtService = new JwtService(properties);
    }

    @Test
    void generateToken_shouldGenerateJwt() {

        User user = new User(
                "amal@example.com",
                "Amal Nassih",
                "password"
        );
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_shouldReturnEmail() {

        User user = new User(
                "amal@example.com",
                "Amal Nassih",
                "password"
        );

        String token = jwtService.generateToken(user);

        assertEquals(
                "amal@example.com",
                jwtService.extractUsername(token)
        );
    }

    @Test
    void isTokenValid_shouldReturnTrue() {

        User user = new User(
                "amal@example.com",
                "Amal Nassih",
                "password"
        );

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_shouldReturnFalseForAnotherUser() {

        User amal = new User(
                "amal@example.com",
                "Amal",
                "password"
        );

        User john = new User(
                "john@example.com",
                "John",
                "password"
        );

        String token = jwtService.generateToken(amal);

        assertFalse(jwtService.isTokenValid(token, john));
    }

    @Test
    void extractUsername_shouldThrowExceptionForInvalidToken() {

        assertThrows(
                JwtException.class,
                () -> jwtService.extractUsername("abc.def.ghi")
        );
    }
}