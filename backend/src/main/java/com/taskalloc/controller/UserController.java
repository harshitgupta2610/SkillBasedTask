package com.taskalloc.controller;

import com.taskalloc.dto.*;
import com.taskalloc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserResponse>> getEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getMe(userId));
    }

    @PostMapping("/{userId}/skills")
    public ResponseEntity<UserResponse> addSkill(@PathVariable Long userId,
                                                  @Valid @RequestBody UserSkillRequest req) {
        return ResponseEntity.ok(userService.addSkillToUser(userId, req));
    }
}
