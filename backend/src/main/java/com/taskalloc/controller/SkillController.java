package com.taskalloc.controller;

import com.taskalloc.model.Skill;
import com.taskalloc.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<List<Skill>> getAll() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @PostMapping
    public ResponseEntity<Skill> create(@RequestBody Map<String, String> body) {
        Skill skill = skillService.createSkill(body.get("name"), body.get("category"));
        return ResponseEntity.ok(skill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
