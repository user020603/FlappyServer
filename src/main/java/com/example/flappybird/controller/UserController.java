package com.example.flappybird.controller;

import com.example.flappybird.model.User;
import com.example.flappybird.repository.UserRepository;
import com.example.flappybird.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(400).body("Username already taken");
        }

        if (!isPasswordStrong(user.getPassword())) {
            return ResponseEntity.status(400).body("Password must be strong enough");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody User loginUser) {
        User user = userRepository.findByUsername(loginUser.getUsername());
        if (user != null && passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            // Tạo phản hồi chứa userId và username
            Map<String, String> response = new HashMap<>();
            response.put("userId", String.valueOf(user.getId()));
            response.put("username", user.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        return password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    }
}
