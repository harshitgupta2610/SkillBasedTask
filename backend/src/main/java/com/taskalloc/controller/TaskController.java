package com.taskalloc.controller;

import com.taskalloc.dto.*;
import com.taskalloc.model.Task;
import com.taskalloc.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /** Create task — triggers auto-allocation immediately */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest req,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.createTask(req, userDetails.getUsername()));
    }

    /** Get all tasks (manager view) */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    /** Get tasks assigned to the logged-in employee */
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getMyTasks(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    /** Update task status (employee updates own task progress) */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id,
                                                      @RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        Task.Status status = Task.Status.valueOf(body.get("status"));
        return ResponseEntity.ok(taskService.updateStatus(id, status, userDetails.getUsername()));
    }

    /** Manual reassign by manager */
    @PatchMapping("/{id}/assign/{employeeId}")
    public ResponseEntity<TaskResponse> manualAssign(@PathVariable Long id,
                                                      @PathVariable Long employeeId) {
        return ResponseEntity.ok(taskService.manualAssign(id, employeeId));
    }
}
