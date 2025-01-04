package com.fuas.providers_access_platform.service;

import com.fuas.providers_access_platform.dto.*;
import com.fuas.providers_access_platform.model.User;
import com.fuas.providers_access_platform.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;


@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public CommonResponse<Map<String, Object>> simplifiedAuthenticate(LoginRequest inputPayload, Logger logger) {
        // Log the incoming request if needed
        logger.info("Attempting to authenticate user: {}", inputPayload.getUsername());

        // Try to fetch the user from the database based on the username
        User user = userRepository.findByUsername(inputPayload.getUsername());

        // Check if user is found and password matches
        if (user != null && user.getPassword().equals(inputPayload.getPassword())) {
            // Authentication successful, return user data in CommonResponse
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("userType",user.getUserType());
            return new CommonResponse<>(true, "Login is successful",response);
        } else {
            // Authentication failed, return error message
            return new CommonResponse<>(false, "Invalid username or password", null);
        }
    }

    public CommonResponse registerUser(LoginRequest registerRequest) {
        // Check if the username or email already exists
        if (userRepository.existsByUsername(registerRequest.getUsername()) ||
                userRepository.existsByEmail(registerRequest.getEmail())) {
            return new CommonResponse<>(false, "Username or email already exists", null);
        }

        // Check added to add the default value for userType if not provided
        if (registerRequest.getUserType() == null || registerRequest.getUserType().isEmpty()) {
            registerRequest.setUserType("User"); // Default value
        }

        // Create a new User entity and save it with the plain password
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword()); // Save password as plain text
        user.setEmail(registerRequest.getEmail());
        user.setId(registerRequest.getId());
        user.setUserType(registerRequest.getUserType());
        userRepository.save(user);


        return new CommonResponse<>(true, "Registration successful", null);
    }
}