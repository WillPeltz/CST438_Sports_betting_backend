package com.example.restservice.controller;

// This is a Data Transfer Object (DTO) to map the incoming JSON
public class LoginRequest {
    private String username;
    private String password;

    // Getters and Setters are needed for JSON deserialization
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
}