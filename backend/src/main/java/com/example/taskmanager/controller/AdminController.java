package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.UserResponse;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final TaskService taskService;

    public AdminController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity
                .ok(taskService.getAllTasksForAdmin(status, priority, search, sortBy, sortDir, page, size));
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> getMetrics() {
        Page<TaskResponse> tasks = taskService.getAllTasksForAdmin(null, null, null, "createdAt", "desc", 0, 1000);

        long totalTasks = tasks.getTotalElements();
        long completedTasks = tasks.getContent().stream()
                .filter(task -> task.getStatus() != null && task.getStatus().name().equals("COMPLETED"))
                .count();
        long openTasks = totalTasks - completedTasks;

        Map<String, Long> metrics = new HashMap<>();
        metrics.put("totalTasks", totalTasks);
        metrics.put("completedTasks", completedTasks);
        metrics.put("openTasks", openTasks);

        return ResponseEntity.ok(metrics);
    }
}
