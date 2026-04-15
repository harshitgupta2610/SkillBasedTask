package com.taskalloc.service;

import com.taskalloc.model.*;
import com.taskalloc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Core allocation algorithm:
 *
 * Score = skill_match_score + workload_bonus
 *
 * For each required skill:
 *   - Employee must have skill at or above minProficiency (else disqualified)
 *   - score += 10 + (employeeProficiency - minProficiency) * 2   (reward over-qualification)
 *
 * Workload bonus (active task count):
 *   0 tasks → +15,  1 → +10,  2 → +5,  3+ → 0
 *
 * Employee with highest score gets assigned.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AllocationService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final TaskRepository taskRepository;

    public Optional<ScoredCandidate> findBestCandidate(Task task) {
        List<TaskSkillRequirement> requirements = task.getRequiredSkills();

        // Get all available employees
        List<User> candidates = userRepository.findByRoleAndAvailable(User.Role.EMPLOYEE, true);

        if (candidates.isEmpty()) {
            log.warn("No available employees found for task: {}", task.getTitle());
            return Optional.empty();
        }

        ScoredCandidate best = null;

        for (User candidate : candidates) {
            double score = calculateScore(candidate, requirements);
            if (score < 0) continue; // disqualified — missing required skill

            log.info("Candidate: {} | Score: {}", candidate.getName(), score);

            if (best == null || score > best.getScore()) {
                best = new ScoredCandidate(candidate, score);
            }
        }

        return Optional.ofNullable(best);
    }

    private double calculateScore(User employee, List<TaskSkillRequirement> requirements) {
        double score = 0;

        for (TaskSkillRequirement req : requirements) {
            Optional<UserSkill> userSkill = userSkillRepository
                    .findByUserIdAndSkillId(employee.getId(), req.getSkill().getId());

            if (userSkill.isEmpty()) {
                return -1; // missing required skill — disqualified
            }

            int empLevel = userSkill.get().getProficiencyLevel();
            if (empLevel < req.getMinProficiencyLevel()) {
                return -1; // below minimum — disqualified
            }

            // Base match + reward for higher proficiency
            score += 10 + (empLevel - req.getMinProficiencyLevel()) * 2;
        }

        // Workload bonus
        long activeTasks = taskRepository.countActiveTasksByUser(employee.getId());
        if (activeTasks == 0)      score += 15;
        else if (activeTasks == 1) score += 10;
        else if (activeTasks == 2) score += 5;
        // 3+ active tasks → no bonus

        return score;
    }

    public record ScoredCandidate(User user, double score) {
        public User getUser()  { return user; }
        public double getScore() { return score; }
    }
}
