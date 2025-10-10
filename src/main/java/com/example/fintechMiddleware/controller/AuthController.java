package com.example.fintechMiddleware.controller;

import com.example.fintechMiddleware.dto.AuthRequest;
import com.example.fintechMiddleware.dto.AuthResponse;
import com.example.fintechMiddleware.model.User;
import com.example.fintechMiddleware.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private com.example.fintechMiddleware.config.JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        try {
            log.info("Login attempt for email: {}", authRequest.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            String email = authentication.getName();
            String token = jwtUtil.generateToken(email);
            log.info("Login successful for email: {}", email);
            return new AuthResponse(token);
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for email: {}", authRequest.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        } catch (Exception e) {
            log.error("Login failed for email: {} - {}", authRequest.getEmail(), e.getMessage());
            throw new RuntimeException("Authentication failed", e);
        }
    }
}