package com.fuas.providers_access_platform.service;

import com.fuas.providers_access_platform.model.User;
import com.fuas.providers_access_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public User authenticate(String username, String password) {
        // Fetch user from the database
        User user = userRepository.findByUsername(username);

        // Validate the password
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null; // Authentication failed
    }
}
