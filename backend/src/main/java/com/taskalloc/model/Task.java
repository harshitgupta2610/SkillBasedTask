package com.taskalloc.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.OPEN;

    @Column
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "allocation_score")
    private Double allocationScore;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<TaskSkillRequirement> requiredSkills = new ArrayList<>();

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum Status {
        OPEN, ASSIGNED, IN_PROGRESS, DONE, CANCELLED
    }
}
