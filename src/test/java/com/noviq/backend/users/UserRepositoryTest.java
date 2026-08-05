package com.noviq.backend.users;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void existsByEmailIgnoreCase_shouldReturnTrue() {

        User user = new User(
                "amal@example.com",
                "Amal Nassih",
                "password"
        );

        user.setRole(Role.USER);

        userRepository.save(user);

        boolean exists =
                userRepository.existsByEmailIgnoreCase("AMAL@example.com");

        assertTrue(exists);
    }
    
    @Test
    void existsByEmailIgnoreCase_shouldReturnFalse() {

        boolean exists =
                userRepository.existsByEmailIgnoreCase("unknown@example.com");

        assertFalse(exists);
    }

    @Test
    void findByEmailIgnoreCase_shouldReturnUser() {

        User user = new User(
                "amal@example.com",
                "Amal Nassih",
                "password"
        );

        user.setRole(Role.USER);

        userRepository.save(user);

        User result = userRepository
                .findByEmailIgnoreCase("AMAL@example.com")
                .orElse(null);

        assertNotNull(result);
        assertEquals("Amal Nassih", result.getFullName());
    }

    @Test
    void findByEmailIgnoreCase_shouldReturnEmpty() {

        assertTrue(
                userRepository
                        .findByEmailIgnoreCase("missing@example.com")
                        .isEmpty()
        );
    }

    @Test
    void save_shouldGenerateId() {

        User user = new User(
                "amal@example.com",
                "Amal Nassih",
                "password"
        );

        user.setRole(Role.USER);

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
    }

}