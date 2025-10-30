package com.example.restservice.controller;

import com.example.restservice.entity.User;
import com.example.restservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Create user (original endpoint)
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
    
    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find user by username
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getPassword().equals(loginRequest.getPassword())) {
                    // Login successful
                    response.put("success", true);
                    response.put("message", "Login successful");
                    response.put("username", user.getUsername());
                    response.put("userId", user.getId());
                    return ResponseEntity.ok(response);
                }
            }
            
            // Invalid credentials
            response.put("success", false);
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
            
        } catch (Exception e) {
            // Server error
            response.put("success", false);
            response.put("message", "An error occurred during login");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User newUser) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if username already exists
            Optional<User> existingUserOptional = userRepository.findByUsername(newUser.getUsername());
            
            if (existingUserOptional.isPresent()) {
                response.put("success", false);
                response.put("message", "Username already exists");
                return ResponseEntity.status(400).body(response);
            }
            
            // Validate username and password
            if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username is required");
                return ResponseEntity.status(400).body(response);
            }
            
            if (newUser.getPassword() == null || newUser.getPassword().length() < 4) {
                response.put("success", false);
                response.put("message", "Password must be at least 4 characters long");
                return ResponseEntity.status(400).body(response);
            }
            
            // Save new user
            User savedUser = userRepository.save(newUser);
            
            response.put("success", true);
            response.put("message", "Account created successfully");
            response.put("username", savedUser.getUsername());
            response.put("userId", savedUser.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred during registration");
            return ResponseEntity.status(500).body(response);
        }
    }
}