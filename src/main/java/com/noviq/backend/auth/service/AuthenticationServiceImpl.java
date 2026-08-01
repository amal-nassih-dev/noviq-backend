package com.noviq.backend.auth.service;

import com.noviq.backend.auth.dto.RegistrationRequest;
import com.noviq.backend.common.exceptions.EmailAlreadyExistsException;
import com.noviq.backend.users.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.noviq.backend.users.User;
import com.noviq.backend.users.dto.UserResponse;
import com.noviq.backend.auth.dto.AuthenticationResponse;
import com.noviq.backend.auth.dto.LoginRequest;
import com.noviq.backend.users.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.noviq.backend.security.JwtService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final AuthenticationManager authenticationManager;
   private final JwtService jwtService;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates a user based on the provided login request. If the email or password is invalid, an exception is thrown this is done by spring security.
     *
     * @param request The login request containing user credentials.
     */
   public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmailIgnoreCase(request.email())
        .orElseThrow(() ->
                new IllegalStateException(
                        "Authenticated user not found."
                ));

        String jwt = jwtService.generateToken(user);

        return new AuthenticationResponse(
                jwt,
                UserResponse.from(user)
        );
    }


   /**
    * Registers a new user based on the provided registration request. we hash the password before saving the user to the database. 
    * If the email already exists, an exception is thrown.
    *
    * @param request The registration request containing user details.
    * @throws EmailAlreadyExistsException if the email already exists in the database.
    */
   public AuthenticationResponse register(RegistrationRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
    
        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser = new User(request.email(), request.fullName(), hashedPassword);
        newUser.setRole(Role.USER);
        User savedUser = userRepository.save(newUser);
        String jwt = jwtService.generateToken(savedUser);
        return new AuthenticationResponse(jwt, UserResponse.from(savedUser));
   }
}
