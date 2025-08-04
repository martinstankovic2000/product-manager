package com.ingemark.productmanager.service;

import com.ingemark.productmanager.exception.UserNotFoundException;
import com.ingemark.productmanager.model.user.Role;
import com.ingemark.productmanager.model.user.User;
import com.ingemark.productmanager.model.user.UserPrincipal;
import com.ingemark.productmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_WhenUserExists_ReturnsUserPrincipal() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedpass")
                .role(Role.CUSTOMER)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertTrue(userDetails instanceof UserPrincipal);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ThrowsException() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("missing"));
    }
}
