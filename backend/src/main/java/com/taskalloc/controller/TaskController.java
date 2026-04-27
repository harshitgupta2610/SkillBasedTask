package com.taskalloc.controller;

import com.taskalloc.dto.*;
import com.taskalloc.model.Task;
import com.taskalloc.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /** Create task — triggers auto-allocation immediately. createdById comes from request body. */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest req) {
        return ResponseEntity.ok(taskService.createTask(req));
    }

    /** Get all tasks (manager view) */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    /** Get tasks assigned to a specific user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getMyTasks(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    /** Update task status (employee marks Start / Done) */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id,
                                                      @RequestBody Map<String, String> body) {
        Task.Status status = Task.Status.valueOf(body.get("status"));
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }
}
