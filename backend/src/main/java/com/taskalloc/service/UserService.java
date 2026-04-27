package com.taskalloc.service;

import com.taskalloc.dto.*;
import com.taskalloc.model.*;
import com.taskalloc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;
    private final TaskRepository taskRepository;

    // ── Auth (plain text password, no encoding) ─────────────────────
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!user.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return user;
    }

    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPassword(req.getPassword());  // plain text — basic MVP only
        u.setRole(req.getRole());
        u.setAvailable(true);
        return userRepository.save(u);
    }

    // ── Lookups ─────────────────────────────────────────────────────
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

    public UserResponse getMe(Long userId) {
        return toResponse(getById(userId));
    }

    // ── Skills ──────────────────────────────────────────────────────
    public UserResponse addSkillToUser(Long userId, UserSkillRequest req) {
        User user = getById(userId);
        Skill skill = skillRepository.findById(req.getSkillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found"));

        userSkillRepository.findByUserIdAndSkillId(userId, req.getSkillId())
                .ifPresent(e -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Skill already added"); });

        UserSkill us = new UserSkill();
        us.setUser(user);
        us.setSkill(skill);
        us.setProficiencyLevel(req.getProficiencyLevel());
        us.setYearsExperience(req.getYearsExperience());
        userSkillRepository.save(us);
        return toResponse(user);
    }

    // ── Response mapper ─────────────────────────────────────────────
    public UserResponse toResponse(User user) {
        List<UserSkill> skills = userSkillRepository.findByUserId(user.getId());
        long activeTasks = taskRepository.countActiveTasksByUser(user.getId());

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole());
        res.setAvailable(user.isAvailable());
        res.setActiveTaskCount(activeTasks);

        List<UserResponse.SkillSummary> skillList = skills.stream().map(us -> {
            UserResponse.SkillSummary s = new UserResponse.SkillSummary();
            s.setSkillId(us.getSkill().getId());
            s.setSkillName(us.getSkill().getName());
            s.setCategory(us.getSkill().getCategory());
            s.setProficiencyLevel(us.getProficiencyLevel());
            s.setYearsExperience(us.getYearsExperience());
            return s;
        }).collect(Collectors.toList());
        res.setSkills(skillList);

        return res;
    }
}
