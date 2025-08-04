package com.ingemark.productmanager.controller;

import com.ingemark.productmanager.configuration.jwt.JwtUtil;
import com.ingemark.productmanager.exception.EmailInUseException;
import com.ingemark.productmanager.exception.UsernameInUseException;
import com.ingemark.productmanager.model.user.JwtResponse;
import com.ingemark.productmanager.model.user.LoginRequest;
import com.ingemark.productmanager.model.user.RegisterRequest;
import com.ingemark.productmanager.model.user.UserPrincipal;
import com.ingemark.productmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication-related endpoints.
 * Provides user login and registration functionalities.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Authenticates the user and generates a JWT token.
     *
     * @param loginRequest Contains username and password.
     * @return JWT token and user details if authentication is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userPrincipal);

        return ResponseEntity.ok(new JwtResponse(jwt, userPrincipal.getUsername(), userPrincipal.getEmail()));
    }

    /**
     * Registers a new user account.
     *
     * @param registerRequest Contains new user registration details.
     * @return HTTP 200 if registration is successful.
     * @throws UsernameInUseException if username is already taken.
     * @throws EmailInUseException if email is already registered.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {

        userService.createUser(registerRequest);
        return ResponseEntity.ok().build();
    }
}
