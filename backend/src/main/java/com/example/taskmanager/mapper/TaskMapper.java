package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;

public class TaskMapper {
    private TaskMapper() {
    }

    public static TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getOwner() != null ? task.getOwner().getId() : null);
    }
}
