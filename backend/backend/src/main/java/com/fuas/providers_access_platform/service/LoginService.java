package com.fuas.providers_access_platform.service;

import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.LoginRequest;
import com.fuas.providers_access_platform.dto.LoginResponse;
import com.fuas.providers_access_platform.model.User;
import com.fuas.providers_access_platform.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public CommonResponse<LoginResponse> simplifiedAuthenticate(LoginRequest inputPayload, Logger logger) {
        // Log the incoming request if needed
        logger.info("Attempting to authenticate user: {}", inputPayload.getUsername());

        // Try to fetch the user from the database based on the username
        User user = userRepository.findByUsername(inputPayload.getUsername());

        // Check if user is found and password matches
        if (user != null && user.getPassword().equals(inputPayload.getPassword())) {
            // Authentication successful, return user data in CommonResponse
            LoginResponse loginResponse = new LoginResponse(user.getUserType());
            return new CommonResponse<>(true, "Login is successful", loginResponse);
        } else {
            // Authentication failed, return error message
            return new CommonResponse<>(false, "Invalid username or password", null);
        }
    }
}
