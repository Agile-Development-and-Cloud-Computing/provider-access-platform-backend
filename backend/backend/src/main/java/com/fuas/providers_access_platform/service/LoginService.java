package com.fuas.providers_access_platform.service;

import com.fuas.providers_access_platform.model.User;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public User authenticate(String username, String password) {
        // For simplicity, this method checks against hardcoded values.
        // In real applications, you'd query the database or an external service.
        if ("admin".equals(username) && "password123".equals(password)) {
            return new User(username, password);
        }
        return null;
    }
}
