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

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

   public AuthenticationResponse login(LoginRequest request) {
      return null;
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
        return new AuthenticationResponse("dummy-token" + hashedPassword, UserResponse.from(savedUser));
   }
}
