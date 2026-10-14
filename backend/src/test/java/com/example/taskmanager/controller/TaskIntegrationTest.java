package com.example.taskmanager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.taskmanager.entity.Role;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void authenticatedUserCanCreateAndFetchOwnTask() throws Exception {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword(passwordEncoder.encode("secret123"));
        user.setRole(Role.USER);
        userRepository.save(user);

        String token = loginAndGetToken("alice@example.com", "secret123");

        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"title\":\"Task 1\",\"description\":\"Description\",\"status\":\"TODO\",\"priority\":\"HIGH\",\"dueDate\":\"2026-12-31T00:00:00.000Z\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"));

        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Task 1"));
    }

    @Test
    void userCannotAccessAnotherUsersTask() throws Exception {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner.setPassword(passwordEncoder.encode("secret123"));
        owner.setRole(Role.USER);
        owner = userRepository.save(owner);

        User other = new User();
        other.setName("Other");
        other.setEmail("other@example.com");
        other.setPassword(passwordEncoder.encode("secret123"));
        other.setRole(Role.USER);
        other = userRepository.save(other);

        Task task = new Task();
        task.setTitle("Private Task");
        task.setDescription("Secret");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        task.setDueDate(new Date());
        task.setOwner(owner);
        task.setCreatedAt(new Date());
        task.setUpdatedAt(new Date());
        taskRepository.save(task);

        String tokenForOther = loginAndGetToken("other@example.com", "secret123");

        mockMvc.perform(get("/api/tasks/" + task.getId())
                .header("Authorization", "Bearer " + tokenForOther))
                .andExpect(status().isForbidden());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
}
