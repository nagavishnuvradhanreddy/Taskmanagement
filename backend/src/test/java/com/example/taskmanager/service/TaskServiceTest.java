package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskStatusUpdateRequest;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.ForbiddenAccessException;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User owner;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner.setPassword("secret");
        owner.setRole(Role.USER);

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Other");
        anotherUser.setEmail("other@example.com");
        anotherUser.setPassword("secret");
        anotherUser.setRole(Role.USER);
    }

    @Test
    void createTask_shouldPersistTaskForOwner() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Write tests");
        request.setDescription("Add missing coverage");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(new Date());

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(10L);
            task.setCreatedAt(new Date());
            task.setUpdatedAt(new Date());
            return task;
        });

        TaskResponse response = taskService.createTask(1L, request);

        assertEquals("Write tests", response.getTitle());
        assertEquals(TaskStatus.TODO, response.getStatus());
        assertEquals(TaskPriority.HIGH, response.getPriority());
    }

    @Test
    void taskOwnership_shouldRejectUnauthorizedAccess() {
        Task task = new Task();
        task.setId(7L);
        task.setTitle("Private task");
        task.setOwner(owner);

        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(taskRepository.findById(7L)).thenReturn(Optional.of(task));

        assertThrows(ForbiddenAccessException.class, () -> taskService.getTaskById(7L, 2L));
    }

    @Test
    void updateTaskStatus_shouldChangeStatus() {
        Task task = new Task();
        task.setId(7L);
        task.setTitle("Task");
        task.setStatus(TaskStatus.TODO);
        task.setOwner(owner);

        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest();
        request.setStatus(TaskStatus.IN_PROGRESS);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(taskRepository.findById(7L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.updateTaskStatus(7L, 1L, request);

        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
    }
}
