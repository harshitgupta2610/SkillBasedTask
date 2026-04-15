package com.taskalloc.repository;

import com.taskalloc.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserId(Long userId);
    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    @Query("SELECT us FROM UserSkill us WHERE us.user.id = :userId AND us.skill.id = :skillId AND us.proficiencyLevel >= :minLevel")
    Optional<UserSkill> findMatchingSkill(Long userId, Long skillId, int minLevel);
}
