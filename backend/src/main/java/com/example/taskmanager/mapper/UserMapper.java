package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.UserResponse;
import com.example.taskmanager.entity.User;

public class UserMapper {
    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
