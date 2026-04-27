package com.taskalloc.dto;

import com.taskalloc.model.User;
import lombok.Data;

@Data
public class AuthResponse {
    private Long userId;
    private String name;
    private String email;
    private User.Role role;
}
