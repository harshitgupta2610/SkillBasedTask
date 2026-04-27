package com.taskalloc.dto;

import com.taskalloc.model.User;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private User.Role role;
    private boolean available;
    private long activeTaskCount;
    private List<SkillSummary> skills;

    @Data
    public static class SkillSummary {
        private Long skillId;
        private String skillName;
        private String category;
        private int proficiencyLevel;
        private int yearsExperience;
    }
}
