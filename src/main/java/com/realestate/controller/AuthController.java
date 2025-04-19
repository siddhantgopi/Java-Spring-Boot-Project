package com.realestate.controller;

import com.realestate.model.User;
import com.realestate.service.UserService;
import com.realestate.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Register Endpoint (Use JSON body instead of query parameters)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password: " + user.getPassword());

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use!");
        }
        userService.registerUser(user.getEmail(), user.getPassword());  // Save user in database
        return ResponseEntity.ok("User registered successfully!");
    }

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        Optional<User> existingUser = userService.findByEmail(user.getEmail());

        if (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword())) {
            // Store the user's email in the session
            HttpSession session = request.getSession();
            session.setAttribute("email", user.getEmail());

            // Return the same redirect functionality
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("redirectUrl", "/dashboard");
            return ResponseEntity.ok(responseBody);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(HttpSession session) {
        String email = (String) session.getAttribute("loggedInUser");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();  // Destroy session
        return ResponseEntity.ok("Logged out successfully");
    }
}
