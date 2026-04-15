package com.taskalloc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "skill_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // 1 = Beginner, 2 = Elementary, 3 = Intermediate, 4 = Advanced, 5 = Expert
    @Column(name = "proficiency_level", nullable = false)
    private int proficiencyLevel;

    @Column(name = "years_experience")
    private int yearsExperience;
}
