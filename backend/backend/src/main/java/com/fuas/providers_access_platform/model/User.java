package com.fuas.providers_access_platform.model;
import jakarta.persistence.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    private String username;
    private String password;
    private String userType;
    private String email;
    private Long id;

    // Constructors, getters, setters, etc.

    // Constructors
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    ;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() { return email;}

    public String setEmail(String email){ this.email = email;
        return email;
    }

    public Long getId() { return id;}

    public Long setId(Long id) {this.id = id;
        return id;
    }
}
