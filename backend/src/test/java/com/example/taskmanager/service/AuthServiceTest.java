package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.example.taskmanager.dto.AuthResponse;
import com.example.taskmanager.dto.LoginRequest;
import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.DuplicateResourceException;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            user.setRole(Role.USER);
            return user;
        });
        when(jwtService.generateToken(any(), anyLong())).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("alice@example.com", response.getUser().getEmail());
        assertEquals(Role.USER, response.getUser().getRole());
    }

    @Test
    void register_shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    void login_shouldReturnTokenForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("hashed-password");
        user.setRole(Role.USER);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User("alice@example.com", "hashed-password",
                        java.util.List.of()),
                null,
                java.util.List.of());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(), anyLong())).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("alice@example.com", response.getUser().getEmail());
    }
}
