package com.noviq.backend.users.service;

import java.util.List;

import com.noviq.backend.users.dto.UserSearchResponse;

public interface UserService {
    public List<UserSearchResponse> searchUsers(String query);
}
