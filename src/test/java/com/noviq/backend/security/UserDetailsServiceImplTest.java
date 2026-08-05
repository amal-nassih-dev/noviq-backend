package com.noviq.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.noviq.backend.users.User;
import com.noviq.backend.users.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUser() {

        User user = new User(
                "amal@example.com",
                "Amal",
                "password"
        );

        when(userRepository.findByEmailIgnoreCase("amal@example.com"))
                .thenReturn(Optional.of(user));

        User result = (User) service.loadUserByUsername("amal@example.com");

        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository)
                .findByEmailIgnoreCase("amal@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundException() {

        when(userRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("amal@example.com")
        );
    }
}