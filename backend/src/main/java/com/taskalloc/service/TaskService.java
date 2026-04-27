package com.taskalloc.service;

import com.taskalloc.dto.*;
import com.taskalloc.model.*;
import com.taskalloc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final AllocationService allocationService;

    @Transactional
    public TaskResponse createTask(TaskCreateRequest req) {
        User creator = userRepository.findById(req.getCreatedById())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found"));

        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setPriority(req.getPriority());
        task.setDeadline(req.getDeadline());
        task.setCreatedBy(creator);
        task.setStatus(Task.Status.OPEN);
        task.setRequiredSkills(new ArrayList<>());

        task = taskRepository.save(task);

        if (req.getRequiredSkills() != null) {
            for (TaskCreateRequest.SkillRequirementDto dto : req.getRequiredSkills()) {
                Skill skill = skillRepository.findById(dto.getSkillId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found: " + dto.getSkillId()));
                TaskSkillRequirement tsr = new TaskSkillRequirement();
                tsr.setTask(task);
                tsr.setSkill(skill);
                tsr.setMinProficiencyLevel(dto.getMinProficiencyLevel());
                task.getRequiredSkills().add(tsr);
            }
            task = taskRepository.save(task);
        }

        task = autoAllocate(task);
        return toResponse(task);
    }

    private Task autoAllocate(Task task) {
        allocationService.findBestCandidate(task).ifPresentOrElse(
            candidate -> {
                task.setAssignedTo(candidate.getUser());
                task.setAssignedAt(LocalDateTime.now());
                task.setStatus(Task.Status.ASSIGNED);
                task.setAllocationScore(candidate.getScore());
                taskRepository.save(task);
                log.info("Task '{}' assigned to '{}' with score {}",
                    task.getTitle(), candidate.getUser().getName(), candidate.getScore());
            },
            () -> log.warn("No suitable candidate found for task: {}", task.getTitle())
        );
        return task;
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, Task.Status newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setStatus(newStatus);
        taskRepository.save(task);
        return toResponse(task);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getMyTasks(Long userId) {
        return taskRepository.findByAssignedToId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        return toResponse(taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")));
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setPriority(task.getPriority());
        res.setStatus(task.getStatus());
        res.setDeadline(task.getDeadline());
        res.setCreatedAt(task.getCreatedAt());
        res.setAssignedAt(task.getAssignedAt());
        res.setAllocationScore(task.getAllocationScore());

        if (task.getCreatedBy() != null) {
            TaskResponse.UserSummary createdBy = new TaskResponse.UserSummary();
            createdBy.setId(task.getCreatedBy().getId());
            createdBy.setName(task.getCreatedBy().getName());
            createdBy.setEmail(task.getCreatedBy().getEmail());
            res.setCreatedBy(createdBy);
        }

        if (task.getAssignedTo() != null) {
            TaskResponse.UserSummary assignedTo = new TaskResponse.UserSummary();
            assignedTo.setId(task.getAssignedTo().getId());
            assignedTo.setName(task.getAssignedTo().getName());
            assignedTo.setEmail(task.getAssignedTo().getEmail());
            res.setAssignedTo(assignedTo);
        }

        List<TaskResponse.SkillRequirementSummary> reqs = task.getRequiredSkills().stream().map(r -> {
            TaskResponse.SkillRequirementSummary s = new TaskResponse.SkillRequirementSummary();
            s.setSkillId(r.getSkill().getId());
            s.setSkillName(r.getSkill().getName());
            s.setSkillCategory(r.getSkill().getCategory());
            s.setMinProficiencyLevel(r.getMinProficiencyLevel());
            return s;
        }).collect(Collectors.toList());
        res.setRequiredSkills(reqs);

        return res;
    }
}
