package com.ingemark.productmanager.service;

import com.ingemark.productmanager.exception.UserNotFoundException;
import com.ingemark.productmanager.model.user.User;
import com.ingemark.productmanager.model.user.UserPrincipal;
import com.ingemark.productmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing Spring Security's UserDetailsService.
 * Loads user details for authentication by username.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by username.
     *
     * @param username Username of the user.
     * @return UserDetails implementation for authentication.
     * @throws UserNotFoundException if user is not found.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return UserPrincipal.create(user);
    }
}