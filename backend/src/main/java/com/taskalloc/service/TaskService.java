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
    private final NotificationService notificationService;

    @Transactional
    public TaskResponse createTask(TaskCreateRequest req, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found"));

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority())
                .deadline(req.getDeadline())
                .createdBy(creator)
                .status(Task.Status.OPEN)
                .requiredSkills(new ArrayList<>())
                .build();

        // Save task first to get ID
        task = taskRepository.save(task);

        // Add skill requirements
        if (req.getRequiredSkills() != null) {
            for (TaskCreateRequest.SkillRequirementDto dto : req.getRequiredSkills()) {
                Skill skill = skillRepository.findById(dto.getSkillId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill not found: " + dto.getSkillId()));
                TaskSkillRequirement tsr = TaskSkillRequirement.builder()
                        .task(task)
                        .skill(skill)
                        .minProficiencyLevel(dto.getMinProficiencyLevel())
                        .build();
                task.getRequiredSkills().add(tsr);
            }
            task = taskRepository.save(task);
        }

        // Auto-allocate
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

                notificationService.notify(candidate.getUser(),
                    String.format("You have been assigned task: \"%s\" [Priority: %s]",
                        task.getTitle(), task.getPriority()));

                log.info("Task '{}' assigned to '{}' with score {}",
                    task.getTitle(), candidate.getUser().getName(), candidate.getScore());
            },
            () -> log.warn("No suitable candidate found for task: {}", task.getTitle())
        );
        return task;
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, Task.Status newStatus, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        task.setStatus(newStatus);
        taskRepository.save(task);

        if (newStatus == Task.Status.DONE && task.getCreatedBy() != null) {
            notificationService.notify(task.getCreatedBy(),
                String.format("Task \"%s\" has been marked as DONE by %s",
                    task.getTitle(),
                    task.getAssignedTo() != null ? task.getAssignedTo().getName() : "assignee"));
        }

        return toResponse(task);
    }

    @Transactional
    public TaskResponse manualAssign(Long taskId, Long employeeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        task.setAssignedTo(employee);
        task.setAssignedAt(LocalDateTime.now());
        task.setStatus(Task.Status.ASSIGNED);
        task.setAllocationScore(null);
        taskRepository.save(task);

        notificationService.notify(employee,
            String.format("[Manual Assignment] You have been assigned: \"%s\"", task.getTitle()));

        return toResponse(task);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getMyTasks(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return taskRepository.findByAssignedToId(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        return toResponse(taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")));
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .assignedAt(task.getAssignedAt())
                .allocationScore(task.getAllocationScore())
                .createdBy(task.getCreatedBy() == null ? null : TaskResponse.UserSummary.builder()
                        .id(task.getCreatedBy().getId())
                        .name(task.getCreatedBy().getName())
                        .email(task.getCreatedBy().getEmail())
                        .build())
                .assignedTo(task.getAssignedTo() == null ? null : TaskResponse.UserSummary.builder()
                        .id(task.getAssignedTo().getId())
                        .name(task.getAssignedTo().getName())
                        .email(task.getAssignedTo().getEmail())
                        .build())
                .requiredSkills(task.getRequiredSkills().stream()
                        .map(r -> TaskResponse.SkillRequirementSummary.builder()
                                .skillId(r.getSkill().getId())
                                .skillName(r.getSkill().getName())
                                .skillCategory(r.getSkill().getCategory())
                                .minProficiencyLevel(r.getMinProficiencyLevel())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
