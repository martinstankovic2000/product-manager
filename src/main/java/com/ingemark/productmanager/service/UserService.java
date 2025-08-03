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


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
