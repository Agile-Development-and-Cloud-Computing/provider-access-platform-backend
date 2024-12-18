package com.fuas.providers_access_platform.dto;

public class LoginResponse {
    private String userType;

    // Constructor
    public LoginResponse(String userType) {
        this.userType = userType;
    }

    // Getter and Setter
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
