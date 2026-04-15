package com.taskalloc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_skill_requirements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskSkillRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "min_proficiency_level", nullable = false)
    private int minProficiencyLevel;
}
