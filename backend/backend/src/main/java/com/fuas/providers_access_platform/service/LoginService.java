package com.fuas.providers_access_platform.service;

import com.fuas.providers_access_platform.dto.*;
import com.fuas.providers_access_platform.model.User;
import com.fuas.providers_access_platform.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;


@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public CommonResponse<Map<String, Object>> simplifiedAuthenticate(LoginRequest inputPayload) {
        String username = inputPayload.getUsername();
        logger.info("Attempting to authenticate user: {}", username);

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(inputPayload.getPassword())) {
            logger.info("Authentication successful for user: {}", username);

            String token = jwtService.generateToken(user.getUsername(), user.getUserType());
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("token", token);
            response.put("userType", user.getUserType());
            response.put("email", user.getEmail());
            response.put("providerId", user.getProviderId());
            response.put("providerName", user.getProviderName());

            logger.debug("Authentication response: {}", response);
            return new CommonResponse<>(true, "Login successful", response);
        } else {
            logger.warn("Invalid login attempt for user: {}", username);
            return new CommonResponse<>(false, "Invalid username or password", null);
        }
    }

    public CommonResponse registerUser(LoginRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        logger.info("Attempting to register user with username: {} and email: {}", username, email);

        // Check if the username or email already exists
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            logger.warn("Registration failed: Username or email already exists for username: {} or email: {}", username, email);
            return new CommonResponse<>(false, "Username or email already exists", null);
        }

        // Set default userType if not provided
        if (registerRequest.getUserType() == null || registerRequest.getUserType().isEmpty()) {
            registerRequest.setUserType("User"); // Default value
            logger.debug("UserType not provided. Setting default value: User");
        }

        // Create new User entity and save it
        User user = new User();
        user.setUsername(username);
        user.setPassword(registerRequest.getPassword()); // Save password as plain text
        user.setEmail(email);
        user.setId(registerRequest.getId());
        user.setUserType(registerRequest.getUserType());

        userRepository.save(user);
        logger.info("Registration successful for user: {}", username);

        return new CommonResponse<>(true, "Registration successful", null);
    }
}