package com.fuas.providers_access_platform.controller;

import com.fuas.providers_access_platform.dto.CommonResponse;
import com.fuas.providers_access_platform.dto.LoginRequest;
import com.fuas.providers_access_platform.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    @Autowired
    private LoginService loginService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<CommonResponse<Map<String, Object>>> processLogin(@RequestBody LoginRequest inputPayload) {
        CommonResponse<Map<String, Object>> response = loginService.simplifiedAuthenticate(inputPayload, logger);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body("{ \"message\": \"Successfully logged out\" }");
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse> registerUser(@RequestBody LoginRequest inputPayload) {
        CommonResponse response = loginService.registerUser(inputPayload);
        if (response.isSuccess()) {
            System.out.println("Success");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("Failure");
            return ResponseEntity.status(401).body(response);
        }
    }
}