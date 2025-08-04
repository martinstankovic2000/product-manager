package com.ingemark.productmanager.service;

import com.ingemark.productmanager.exception.EmailInUseException;
import com.ingemark.productmanager.exception.UsernameInUseException;
import com.ingemark.productmanager.model.user.RegisterRequest;
import com.ingemark.productmanager.model.user.Role;
import com.ingemark.productmanager.model.user.User;
import com.ingemark.productmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("newUser", "newuser@example.com", "password123");
    }

    @Test
    void createUser_ShouldSaveUser_WhenUsernameAndEmailAreAvailable() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");

        // Act
        userService.createUser(registerRequest);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo(registerRequest.username());
        assertThat(savedUser.getEmail()).isEqualTo(registerRequest.email());
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(savedUser.isEnabled()).isTrue();
    }

    @Test
    void createUser_ShouldThrowUsernameInUseException_WhenUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(registerRequest))
                .isInstanceOf(UsernameInUseException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldThrowEmailInUseException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(registerRequest))
                .isInstanceOf(EmailInUseException.class);

        verify(userRepository, never()).save(any());
    }
}
