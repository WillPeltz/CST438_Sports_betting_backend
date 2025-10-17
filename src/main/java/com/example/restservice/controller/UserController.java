package com.example.restservice.controller;

import com.example.restservice.entity.User;
import com.example.restservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService; 
    
    // all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // create user
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userService.loginUser(
            loginRequest.getUsername(),
            loginRequest.getPassword(),
        );
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}