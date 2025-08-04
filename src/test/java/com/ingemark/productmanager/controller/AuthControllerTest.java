package com.ingemark.productmanager.controller;

import com.ingemark.productmanager.configuration.jwt.JwtUtil;
import com.ingemark.productmanager.model.user.JwtResponse;
import com.ingemark.productmanager.model.user.LoginRequest;
import com.ingemark.productmanager.model.user.RegisterRequest;
import com.ingemark.productmanager.model.user.UserPrincipal;
import com.ingemark.productmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testUser", "testPass");
        registerRequest = new RegisterRequest("newUser", "newuser@example.com", "password123");
    }

    @Test
    void login_ShouldReturnJwtResponse_WhenAuthenticationSucceeds() {
        // Arrange
        UserPrincipal userPrincipal = new UserPrincipal(1L, "testUser", "test@example.com", "encodedPass", List.of());
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtUtil.generateToken(userPrincipal)).thenReturn("jwt-token");

        // Act
        ResponseEntity<JwtResponse> response = authController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse jwtResponse = response.getBody();
        assertThat(jwtResponse).isNotNull();
        assertThat(jwtResponse.token()).isEqualTo("jwt-token");
        assertThat(jwtResponse.username()).isEqualTo("testUser");
        assertThat(jwtResponse.email()).isEqualTo("test@example.com");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(userPrincipal);
    }

    @Test
    void register_ShouldCallUserServiceCreateUserAndReturnOk() {
        // Act
        ResponseEntity<String> response = authController.register(registerRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).createUser(registerRequest);
    }
}

