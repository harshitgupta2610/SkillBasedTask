package com.taskalloc.service;

import com.taskalloc.dto.*;
import com.taskalloc.model.*;
import com.taskalloc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository        userRepository;
    private final UserSkillRepository   userSkillRepository;
    private final SkillRepository       skillRepository;
    private final TaskRepository        taskRepository;
    private final PasswordEncoder       passwordEncoder;

    // ── Auth ─────────────────────────────────────────────────────
    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        return userRepository.save(User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .available(true)
                .build());
    }

    // ── Lookups ───────────────────────────────────────────────────
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    public List<UserResponse> getAllEmployees() {
        return userRepository.findByRole(User.Role.EMPLOYEE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Profile update ────────────────────────────────────────────
    public UserResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = getById(userId);
        user.setName(req.getName());
        userRepository.save(user);
        return toResponse(user);
    }

    // ── Availability ──────────────────────────────────────────────
    public UserResponse updateAvailability(Long userId, boolean available) {
        User user = getById(userId);
        user.setAvailable(available);
        userRepository.save(user);
        return toResponse(user);
    }

    // ── Skills ────────────────────────────────────────────────────
    public UserResponse addSkillToUser(Long userId, UserSkillRequest req) {
        User user = getById(userId);
        Skill skill = skillRepository.findById(req.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));

        userSkillRepository.findByUserIdAndSkillId(userId, req.getSkillId())
                .ifPresent(e -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Skill already added"); });

        userSkillRepository.save(UserSkill.builder()
                .user(user).skill(skill)
                .proficiencyLevel(req.getProficiencyLevel())
                .yearsExperience(req.getYearsExperience())
                .build());
        return toResponse(user);
    }

    public UserResponse updateSkill(Long userId, Long userSkillId, UserSkillRequest req) {
        UserSkill us = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill entry not found"));

        if (!us.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your skill");
        }
        us.setProficiencyLevel(req.getProficiencyLevel());
        us.setYearsExperience(req.getYearsExperience());
        userSkillRepository.save(us);
        return toResponse(getById(userId));
    }

    public UserResponse removeSkill(Long userId, Long userSkillId) {
        UserSkill us = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill entry not found"));

        if (!us.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your skill");
        }
        userSkillRepository.delete(us);
        return toResponse(getById(userId));
    }

    // ── Response mapper ───────────────────────────────────────────
    public UserResponse toResponse(User user) {
        List<UserSkill> skills = userSkillRepository.findByUserId(user.getId());
        long activeTasks = taskRepository.countActiveTasksByUser(user.getId());

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .available(user.isAvailable())
                .activeTaskCount(activeTasks)
                .skills(skills.stream().map(us -> UserResponse.SkillSummary.builder()
                        .userSkillId(us.getId())
                        .skillId(us.getSkill().getId())
                        .skillName(us.getSkill().getName())
                        .category(us.getSkill().getCategory())
                        .proficiencyLevel(us.getProficiencyLevel())
                        .yearsExperience(us.getYearsExperience())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
