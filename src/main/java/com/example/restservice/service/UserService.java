package com.example.restservice.service;

import com.example.restservice.entity.User;
import com.example.restservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Authenticates a user based on username and password.
     * @param username The user's username
     * @param password The user's plain-text password
     * @return An Optional containing the User if authentication is successful, 
     * otherwise an empty Optional.
     */
    public Optional<User> loginUser(String username, String password) {
        // Find the user by their username
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // --- TEMPORARY: replace later with secure password hashing
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty(); // User not found or password incorrect
    }

}