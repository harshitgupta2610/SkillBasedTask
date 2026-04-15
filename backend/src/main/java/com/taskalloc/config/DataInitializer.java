package com.taskalloc.config;

import com.taskalloc.model.*;
import com.taskalloc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with demo data on first run.
 * Safe to rerun — skips if data already exists.
 *
 * Demo credentials:
 *   manager@demo.com  / password123  (MANAGER)
 *   alice@demo.com    / password123  (EMPLOYEE — Java Expert, React Intermediate)
 *   bob@demo.com      / password123  (EMPLOYEE — React Expert, Node.js Advanced)
 *   charlie@demo.com  / password123  (EMPLOYEE — Java Beginner, DevOps Advanced)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded — skipping initialization");
            return;
        }

        log.info("Seeding demo data...");

        // ── Skills ────────────────────────────────────────────────
        Skill java      = save(skill("Java",       "BACKEND"));
        Skill spring    = save(skill("Spring Boot", "BACKEND"));
        Skill react     = save(skill("React",       "FRONTEND"));
        Skill angular   = save(skill("Angular",     "FRONTEND"));
        Skill nodejs    = save(skill("Node.js",     "BACKEND"));
        Skill docker    = save(skill("Docker",      "DEVOPS"));
        Skill mysql     = save(skill("MySQL",       "DATABASE"));
        Skill aws       = save(skill("AWS",         "DEVOPS"));

        // ── Manager ───────────────────────────────────────────────
        User manager = userRepository.save(User.builder()
                .name("Manager Mike")
                .email("manager@demo.com")
                .password(passwordEncoder.encode("password123"))
                .role(User.Role.MANAGER)
                .available(true)
                .build());

        // ── Employee: Alice — strong Java/Spring, decent React ────
        User alice = userRepository.save(User.builder()
                .name("Alice Johnson")
                .email("alice@demo.com")
                .password(passwordEncoder.encode("password123"))
                .role(User.Role.EMPLOYEE)
                .available(true)
                .build());
        addSkill(alice, java,    5, 6);
        addSkill(alice, spring,  4, 4);
        addSkill(alice, react,   3, 2);
        addSkill(alice, mysql,   4, 5);

        // ── Employee: Bob — frontend specialist + Node.js ─────────
        User bob = userRepository.save(User.builder()
                .name("Bob Smith")
                .email("bob@demo.com")
                .password(passwordEncoder.encode("password123"))
                .role(User.Role.EMPLOYEE)
                .available(true)
                .build());
        addSkill(bob, react,   5, 5);
        addSkill(bob, angular, 4, 3);
        addSkill(bob, nodejs,  4, 4);
        addSkill(bob, mysql,   2, 1);

        // ── Employee: Charlie — DevOps / infra ────────────────────
        User charlie = userRepository.save(User.builder()
                .name("Charlie Brown")
                .email("charlie@demo.com")
                .password(passwordEncoder.encode("password123"))
                .role(User.Role.EMPLOYEE)
                .available(true)
                .build());
        addSkill(charlie, docker, 5, 4);
        addSkill(charlie, aws,    4, 3);
        addSkill(charlie, java,   2, 1);
        addSkill(charlie, mysql,  3, 2);

        // ── Employee: Diana — full-stack ──────────────────────────
        User diana = userRepository.save(User.builder()
                .name("Diana Prince")
                .email("diana@demo.com")
                .password(passwordEncoder.encode("password123"))
                .role(User.Role.EMPLOYEE)
                .available(true)
                .build());
        addSkill(diana, java,    3, 3);
        addSkill(diana, spring,  3, 2);
        addSkill(diana, angular, 3, 2);
        addSkill(diana, docker,  2, 1);
        addSkill(diana, mysql,   3, 2);

        log.info("Demo data seeded successfully!");
        log.info("Login credentials: manager@demo.com / alice@demo.com / bob@demo.com / charlie@demo.com / diana@demo.com");
        log.info("Password for all: password123");
    }

    private Skill skill(String name, String category) {
        return Skill.builder().name(name).category(category).build();
    }

    private Skill save(Skill s) {
        return skillRepository.save(s);
    }

    private void addSkill(User user, Skill skill, int proficiency, int years) {
        userSkillRepository.save(UserSkill.builder()
                .user(user)
                .skill(skill)
                .proficiencyLevel(proficiency)
                .yearsExperience(years)
                .build());
    }
}
