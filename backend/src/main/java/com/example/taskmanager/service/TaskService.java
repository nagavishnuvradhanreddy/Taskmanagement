package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskStatusUpdateRequest;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.ForbiddenAccessException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponse createTask(Long ownerId, TaskRequest request) {
        User owner = findUser(ownerId);
        Task task = new Task();
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setOwner(owner);
        Date now = new Date();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        Task savedTask = taskRepository.save(task);
        return TaskMapper.toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId, Long currentUserId) {
        Task task = findTask(taskId);
        ensureAccess(task, currentUserId);
        return TaskMapper.toResponse(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksForUser(
            Long userId,
            TaskStatus status,
            TaskPriority priority,
            String search,
            String sortBy,
            String sortDir,
            int page,
            int size) {
        User owner = findUser(userId);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        String normalizedSearch = search != null && !search.isBlank() ? search.trim() : null;
        return taskRepository.findByOwnerWithFilters(owner, status, priority, normalizedSearch, pageable)
                .map(TaskMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasksForAdmin(
            TaskStatus status,
            TaskPriority priority,
            String search,
            String sortBy,
            String sortDir,
            int page,
            int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        String normalizedSearch = search != null && !search.isBlank() ? search.trim() : null;
        return taskRepository.findAllWithFilters(status, priority, normalizedSearch, pageable)
                .map(TaskMapper::toResponse);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, Long currentUserId, TaskUpdateRequest request) {
        Task task = findTask(taskId);
        ensureAccess(task, currentUserId);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription().trim());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        task.setUpdatedAt(new Date());

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, Long currentUserId, TaskStatusUpdateRequest request) {
        Task task = findTask(taskId);
        ensureAccess(task, currentUserId);
        task.setStatus(request.getStatus());
        task.setUpdatedAt(new Date());
        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long taskId, Long currentUserId) {
        Task task = findTask(taskId);
        ensureAccess(task, currentUserId);
        taskRepository.delete(task);
    }

    private void ensureAccess(Task task, Long currentUserId) {
        User currentUser = findUser(currentUserId);
        if (!task.getOwner().getId().equals(currentUserId) && currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenAccessException("You are not allowed to access this task.");
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Task findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }
}
