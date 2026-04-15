package com.taskalloc.controller;

import com.taskalloc.dto.*;
import com.taskalloc.model.Notification;
import com.taskalloc.service.NotificationService;
import com.taskalloc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService         userService;
    private final NotificationService notificationService;

    // ── User listings ─────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserResponse>> getEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(userService.toResponse(userService.getByEmail(ud.getUsername())));
    }

    // ── Profile update ────────────────────────────────────────────
    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long userId,
                                                       @Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(userService.updateProfile(userId, req));
    }

    // ── Availability ──────────────────────────────────────────────
    @PatchMapping("/{userId}/availability")
    public ResponseEntity<UserResponse> updateAvailability(@PathVariable Long userId,
                                                            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(userService.updateAvailability(userId, body.get("available")));
    }

    // ── Skill management ──────────────────────────────────────────
    @PostMapping("/{userId}/skills")
    public ResponseEntity<UserResponse> addSkill(@PathVariable Long userId,
                                                  @Valid @RequestBody UserSkillRequest req) {
        return ResponseEntity.ok(userService.addSkillToUser(userId, req));
    }

    @PutMapping("/{userId}/skills/{userSkillId}")
    public ResponseEntity<UserResponse> updateSkill(@PathVariable Long userId,
                                                     @PathVariable Long userSkillId,
                                                     @Valid @RequestBody UserSkillRequest req) {
        return ResponseEntity.ok(userService.updateSkill(userId, userSkillId, req));
    }

    @DeleteMapping("/{userId}/skills/{userSkillId}")
    public ResponseEntity<UserResponse> removeSkill(@PathVariable Long userId,
                                                     @PathVariable Long userSkillId) {
        return ResponseEntity.ok(userService.removeSkill(userId, userSkillId));
    }

    // ── Notifications ─────────────────────────────────────────────
    @GetMapping("/{userId}/notifications")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getForUser(userId));
    }

    @PatchMapping("/{userId}/notifications/read-all")
    public ResponseEntity<Void> markAllRead(@PathVariable Long userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/notifications/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(userId)));
    }
}
