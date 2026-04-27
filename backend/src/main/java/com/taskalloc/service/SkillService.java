package com.taskalloc.service;

import com.taskalloc.model.Skill;
import com.taskalloc.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill createSkill(String name, String category) {
        if (skillRepository.existsByName(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Skill already exists: " + name);
        }
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(category);
        return skillRepository.save(skill);
    }

    public Skill getById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found: " + id));
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }
}
