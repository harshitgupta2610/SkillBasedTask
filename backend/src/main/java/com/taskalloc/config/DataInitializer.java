package com.taskalloc.config;

import com.taskalloc.model.*;
import com.taskalloc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds demo users and skills on every startup.
 * Uses upsert (find-or-create) so credentials always work,
 * even if the DB already had old data from a previous schema.
 *
 * Demo credentials (plain text passwords for MVP only):
 *
 *   ── ADMIN / MANAGER ─────────────────────────────────
 *   admin@test.com   / admin123
 *
 *   ── EMPLOYEES ───────────────────────────────────────
 *   emp1@test.com    / emp123      (Java/Spring/MySQL expert)
 *   emp2@test.com    / emp123      (Frontend specialist)
 *   emp3@test.com    / emp123      (DevOps specialist)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    @Override
    public void run(String... args) {
        log.info("Seeding/updating demo data...");

        // ── Skills (upsert by name) ─────────────────────
        Skill java    = upsertSkill("Java",        "BACKEND");
        Skill spring  = upsertSkill("Spring Boot", "BACKEND");
        Skill react   = upsertSkill("React",       "FRONTEND");
        Skill angular = upsertSkill("Angular",     "FRONTEND");
        Skill nodejs  = upsertSkill("Node.js",     "BACKEND");
        Skill docker  = upsertSkill("Docker",      "DEVOPS");
        Skill mysql   = upsertSkill("MySQL",       "DATABASE");
        Skill aws     = upsertSkill("AWS",         "DEVOPS");

        // ── Manager ─────────────────────────────────────
        upsertUser("Admin User", "admin@test.com", "admin123", User.Role.MANAGER);

        // ── Employees ───────────────────────────────────
        User emp1 = upsertUser("Emp One",   "emp1@test.com", "emp123", User.Role.EMPLOYEE);
        ensureSkill(emp1, java,   5, 6);
        ensureSkill(emp1, spring, 4, 4);
        ensureSkill(emp1, mysql,  4, 5);

        User emp2 = upsertUser("Emp Two",   "emp2@test.com", "emp123", User.Role.EMPLOYEE);
        ensureSkill(emp2, react,   5, 5);
        ensureSkill(emp2, angular, 4, 3);
        ensureSkill(emp2, nodejs,  4, 4);

        User emp3 = upsertUser("Emp Three", "emp3@test.com", "emp123", User.Role.EMPLOYEE);
        ensureSkill(emp3, docker, 5, 4);
        ensureSkill(emp3, aws,    4, 3);
        ensureSkill(emp3, java,   2, 1);

        log.info("Demo users ready:");
        log.info("  MANAGER   -> admin@test.com / admin123");
        log.info("  EMPLOYEE  -> emp1@test.com  / emp123");
        log.info("  EMPLOYEE  -> emp2@test.com  / emp123");
        log.info("  EMPLOYEE  -> emp3@test.com  / emp123");
    }

    private Skill upsertSkill(String name, String category) {
        return skillRepository.findByName(name).orElseGet(() -> {
            Skill s = new Skill();
            s.setName(name);
            s.setCategory(category);
            return skillRepository.save(s);
        });
    }

    private User upsertUser(String name, String email, String password, User.Role role) {
        User user = userRepository.findByEmail(email).orElseGet(User::new);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setAvailable(true);
        return userRepository.save(user);
    }

    private void ensureSkill(User user, Skill skill, int proficiency, int years) {
        userSkillRepository.findByUserIdAndSkillId(user.getId(), skill.getId())
                .ifPresentOrElse(
                    existing -> {
                        existing.setProficiencyLevel(proficiency);
                        existing.setYearsExperience(years);
                        userSkillRepository.save(existing);
                    },
                    () -> {
                        UserSkill us = new UserSkill();
                        us.setUser(user);
                        us.setSkill(skill);
                        us.setProficiencyLevel(proficiency);
                        us.setYearsExperience(years);
                        userSkillRepository.save(us);
                    }
                );
    }
}
