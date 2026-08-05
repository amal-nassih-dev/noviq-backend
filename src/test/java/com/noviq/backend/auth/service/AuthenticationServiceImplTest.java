package com.noviq.backend.auth.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.noviq.backend.auth.dto.AuthenticationResponse;
import com.noviq.backend.auth.dto.LoginRequest;
import com.noviq.backend.auth.dto.RegistrationRequest;
import com.noviq.backend.common.exceptions.EmailAlreadyExistsException;
import com.noviq.backend.security.JwtService;
import com.noviq.backend.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.noviq.backend.users.User;
import com.noviq.backend.users.Role;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.Optional;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class) // Use MockitoExtension to enable Mockito annotations and functionality in the test class
public class AuthenticationServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    private AuthenticationServiceImpl authenticationService;

    @BeforeEach // This method will be executed before each test case to set up the test environment
    public void setUp() {
        authenticationService = new AuthenticationServiceImpl(
                userRepository,
                passwordEncoder,
                authenticationManager,
                jwtService
        ); }


    @Test
    void register_shouldCreateNewUser() {
    
       RegistrationRequest request = new RegistrationRequest(
                "amal@example.com",
                "Amal Nassih",
                "Password123!"
        );
       // mock the responses of the dependencies used in the register method
       when(userRepository.existsByEmailIgnoreCase(request.email()))
        .thenReturn(false);

       when(passwordEncoder.encode(request.password()))
        .thenReturn("hashedPassword");

        User savedUser = new User(
                request.email(),
                request.fullName(),
                "hashedPassword"
        );

        savedUser.setRole(Role.USER);
        
       when(userRepository.save(any(User.class)))
        .thenReturn(savedUser);
        when(jwtService.generateToken(savedUser))
        .thenReturn("fake-jwt-token");
        // act or just the method that we want to test
        AuthenticationResponse response = authenticationService.register(request);

        // assert the expected results
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
        assertEquals(savedUser.getEmail(), response.user().email());
        assertEquals(savedUser.getFullName(), response.user().fullName());

        // verify that the dependencies were called with the expected arguments
        verify(userRepository).existsByEmailIgnoreCase(request.email());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(savedUser);
    }

     @Test
    void register_shouldThrowExceptionWhenUserAlreadyExists() {
    
       RegistrationRequest request = new RegistrationRequest(
                "amal@example.com",
                "Amal Nassih",
                "Password123!"
        );
       // mock the responses of the dependencies used in the register method
       when(userRepository.existsByEmailIgnoreCase(request.email()))
        .thenReturn(true);
 
        // act and assert the expected exception
        assertThrows(
            EmailAlreadyExistsException.class,
            () -> authenticationService.register(request)
        );
        
        // verify that the dependencies were called with the expected arguments
        verify(userRepository).existsByEmailIgnoreCase(request.email());
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(any());

    }

    @Test
    void login_shouldReturnJwtToken() {
    
       LoginRequest request = new LoginRequest(
                "amal@example.com",
                "Password123!"
        );
       // mock the responses of the dependencies used in the register method
       when(authenticationManager.authenticate(any()))
        .thenReturn(new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        ));
        
       when(jwtService.generateToken(any()))
        .thenReturn("fake-jwt-token");

        when(userRepository.findByEmailIgnoreCase(request.email()))
        .thenReturn(Optional.of(new User(
                request.email(),
                "Amal Nassih",
                "hashedPassword"
        )));
        // act or just the method that we want to test
        AuthenticationResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());

       
        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmailIgnoreCase(request.email());
        verify(jwtService).generateToken(any());

    }

    @Test
    void login_shouldThrowIllegalStateException_whenAuthenticatedUserNotFound() {
    
       LoginRequest request = new LoginRequest(
                "amal@example.com",
                "Password123!"
        );
       // mock the responses of the dependencies used in the register method
       when(authenticationManager.authenticate(any()))
        .thenReturn(new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        ));

        when(userRepository.findByEmailIgnoreCase(request.email()))
        .thenReturn(Optional.empty()); // Simulate that the user is not found in the repository
        
        IllegalStateException exception = assertThrows( // assert + act
            IllegalStateException.class,
            () -> authenticationService.login(request)
        );

        assertEquals("Authenticated user not found.", exception.getMessage());

       
        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmailIgnoreCase(request.email());
        verify(jwtService, never()).generateToken(any());

    }

    @Test
    void register_shouldSaveEncodedPassword() {

        RegistrationRequest request = new RegistrationRequest(
                "amal@example.com",
                "Amal Nassih",
                "Password123!"
        );

        when(userRepository.existsByEmailIgnoreCase(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("hashedPassword");

        User savedUser = new User(
                request.email(),
                request.fullName(),
                "hashedPassword"
        );

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken(any()))
                .thenReturn("token");

        authenticationService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User user = captor.getValue();

        assertEquals("hashedPassword", user.getPassword());
        assertEquals("amal@example.com", user.getEmail());
        assertEquals("Amal Nassih", user.getFullName());
    }

    @Test
    void register_shouldAssignDefaultUserRole() {

        RegistrationRequest request = new RegistrationRequest(
                "amal@example.com",
                "Amal Nassih",
                "Password123!"
        );

        when(userRepository.existsByEmailIgnoreCase(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("hashedPassword");

        User savedUser = new User(
                request.email(),
                request.fullName(),
                "hashedPassword"
        );

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken(any()))
                .thenReturn("token");

        authenticationService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        assertEquals(Role.USER, captor.getValue().getRole());
    }

    @Test
    void login_shouldAuthenticateUsingProvidedCredentials() {
        LoginRequest request = new LoginRequest(
                "amal@example.com",
                "Password123!"
        );

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                ));

        User user = new User(
                request.email(),
                "Amal Nassih",
                "hashedPassword"
        );

        when(userRepository.findByEmailIgnoreCase(request.email()))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(any()))
                .thenReturn("fake-token");

        authenticationService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        verify(authenticationManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken token = captor.getValue();

        assertEquals(request.email(), token.getPrincipal());
        assertEquals(request.password(), token.getCredentials());
    }

    @Test
    void login_shouldGenerateTokenForAuthenticatedUser() {

        LoginRequest request = new LoginRequest(
                "amal@example.com",
                "Password123!"
        );

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                ));

        User user = new User(
                request.email(),
                "Amal Nassih",
                "hashedPassword"
        );

        when(userRepository.findByEmailIgnoreCase(request.email()))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(any()))
                .thenReturn("fake-token");

        authenticationService.login(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(jwtService).generateToken(captor.capture());

        assertEquals("amal@example.com", captor.getValue().getEmail());
    }

    @Test
    void login_shouldPropagateAuthenticationException() {

        LoginRequest request = new LoginRequest(
                "amal@example.com",
                "WrongPassword"
        );

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.login(request)
        );

        verify(userRepository, never()).findByEmailIgnoreCase(any());
        verify(jwtService, never()).generateToken(any());
    }

}
