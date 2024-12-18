package com.fuas.providers_access_platform.controller;

import com.fuas.providers_access_platform.model.LoginRequest;
import com.fuas.providers_access_platform.model.User;
import com.fuas.providers_access_platform.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;



    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> processLogin(@RequestBody LoginRequest loginRequest) {
        System.out.println("LoginRequest: " + loginRequest);
        Map<String, String> response = new HashMap<>();
        User user = loginService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            response.put("message", "Login is successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("{ \"message\": \"Successfully logged out\" }");
    }
}
