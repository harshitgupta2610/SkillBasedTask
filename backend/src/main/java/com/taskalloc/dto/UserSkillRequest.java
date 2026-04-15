package com.taskalloc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSkillRequest {
    @NotNull
    private Long skillId;
    @Min(1) @Max(5)
    private int proficiencyLevel;
    @Min(0)
    private int yearsExperience;
}
