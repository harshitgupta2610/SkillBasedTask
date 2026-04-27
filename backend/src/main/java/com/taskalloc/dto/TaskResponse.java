package com.taskalloc.dto;

import com.taskalloc.model.Task;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Task.Priority priority;
    private Task.Status status;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private Double allocationScore;

    private UserSummary createdBy;
    private UserSummary assignedTo;
    private List<SkillRequirementSummary> requiredSkills;

    @Data
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    public static class SkillRequirementSummary {
        private Long skillId;
        private String skillName;
        private String skillCategory;
        private int minProficiencyLevel;
    }
}
