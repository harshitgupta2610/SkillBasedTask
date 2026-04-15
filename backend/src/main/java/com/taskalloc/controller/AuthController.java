package com.taskalloc.controller;

import com.taskalloc.dto.*;
import com.taskalloc.model.User;
import com.taskalloc.security.JwtUtil;
import com.taskalloc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.getByEmail(req.getEmail());
        return ResponseEntity.ok(buildAuthResponse(user));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildAuthResponse(user));
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getEmail()))
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
