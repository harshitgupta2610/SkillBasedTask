package com.taskalloc.dto;

import com.taskalloc.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TaskCreateRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private Task.Priority priority;
    private LocalDate deadline;
    @NotNull
    private Long createdById;   // id of the manager creating the task
    private List<SkillRequirementDto> requiredSkills;

    @Data
    public static class SkillRequirementDto {
        private Long skillId;
        private int minProficiencyLevel;
    }
}
