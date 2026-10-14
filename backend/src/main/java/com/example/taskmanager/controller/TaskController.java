package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskStatusUpdateRequest;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.createTask(currentUser.getId(), request));
    }

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.getTasksForUser(
                currentUser.getId(), status, priority, search, sortBy, sortDir, page, size));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.getTaskById(id, currentUser.getId()));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.updateTask(id, currentUser.getId(), request));
    }

    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.updateTaskStatus(id, currentUser.getId(), request));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        taskService.deleteTask(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));
    }
}
