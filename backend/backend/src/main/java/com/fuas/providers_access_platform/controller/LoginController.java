package com.fuas.providers_access_platform.controller;

import com.fuas.providers_access_platform.model.User;
import com.fuas.providers_access_platform.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    /*
    @PostMapping("/login")
    public ResponseEntity<?> processLogin(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        User user = userService.authenticate(username, password);
        if (user != null) {
            // Successful login
            return ResponseEntity.ok().body("{ \"message\": \"Login is successful\" }");
        } else {
            // Failed login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{ \"message\": \"Invalid username or password\" }");
        }
    }
    */

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> processLogin(@RequestParam("username") String username,
                                                            @RequestParam("password") String password) {
        Map<String, String> response = new HashMap<>();
        User user = loginService.authenticate(username, password);
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
