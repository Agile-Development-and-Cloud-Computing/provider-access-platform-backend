package com.fuas.providers_access_platform.controller;

import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.LoginRequest;
import com.fuas.providers_access_platform.dto.LoginResponse;
import com.fuas.providers_access_platform.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class LoginController {

    @Autowired
    private LoginService loginService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<CommonResponse<LoginResponse>> processLogin(@RequestBody LoginRequest inputPayload) {


        // Call the simplified authenticate method in the LoginService
        CommonResponse<LoginResponse> response = loginService.simplifiedAuthenticate(inputPayload, logger);

        // Return the response directly
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response); // Unauthorized if authentication fails
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("{ \"message\": \"Successfully logged out\" }");
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<LoginResponse>> registerUser(@RequestBody LoginRequest inputPayload) {
        System.out.println("Inside Register User");
        CommonResponse<LoginResponse> response = loginService.registerUser(inputPayload);
        if (response.isSuccess()) {
            System.out.println("Success");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("Failure");
            return ResponseEntity.status(401).body(response);
        }
    }
}