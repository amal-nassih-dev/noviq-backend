package com.noviq.backend.users;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<User> findTop10ByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
        String email,
        String fullName
    );
}
