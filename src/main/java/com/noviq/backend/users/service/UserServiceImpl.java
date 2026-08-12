package com.noviq.backend.users.service;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.noviq.backend.users.User;
import com.noviq.backend.users.UserRepository;
import com.noviq.backend.users.dto.UserSearchResponse;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSearchResponse> searchUsers(String query) {

        if (!StringUtils.hasText(query)) {
            return List.of();
        }

        String search = query.trim();

        if (search.length() < 2) {
            return List.of();
        }

        List<User> users =
            userRepository
                .findTop10ByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                    search,
                    search
                );

        return users.stream()
            .map(UserSearchResponse::from)
            .toList();
    }
}