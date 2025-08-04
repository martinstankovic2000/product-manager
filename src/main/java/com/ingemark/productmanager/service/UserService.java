package com.ingemark.productmanager.service;

import com.ingemark.productmanager.exception.EmailInUseException;
import com.ingemark.productmanager.exception.UsernameInUseException;
import com.ingemark.productmanager.model.user.RegisterRequest;
import com.ingemark.productmanager.model.user.Role;
import com.ingemark.productmanager.model.user.User;
import com.ingemark.productmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user-related business logic.
 * Handles user creation and validation.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user from registration details.
     *
     * @param registerRequest DTO containing username, email, and password.
     * @throws UsernameInUseException if username already exists.
     * @throws EmailInUseException if email is already registered.
     */
    @Transactional
    public void createUser(RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.username()))
            throw new UsernameInUseException(registerRequest.username());

        if (userRepository.existsByEmail(registerRequest.email()))
            throw new EmailInUseException(registerRequest.email());

        User user = User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        userRepository.save(user);
    }
}
