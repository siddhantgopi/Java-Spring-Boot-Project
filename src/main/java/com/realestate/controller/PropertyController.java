package com.realestate.controller;

import com.realestate.model.Property;
import com.realestate.service.PropertyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/property")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addProperty(HttpServletRequest request,
            @RequestParam String name,
            @RequestParam String location,
            @RequestParam double dimensions,
            @RequestParam double pricePerSqFt,
            @RequestParam String dateOfBuy,  
            @RequestParam MultipartFile document) {

        // Get user email from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        String email = (String) session.getAttribute("email");

        try {
            propertyService.addProperty(email, name, location, dimensions, pricePerSqFt, dateOfBuy, document);
            return ResponseEntity.ok("Property added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error saving property: " + e.getMessage());
        }
    }

    @GetMapping("/user-properties")
    public ResponseEntity<?> getUserProperties(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Don't create a new session
        if (session == null || session.getAttribute("email") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        String email = (String) session.getAttribute("email");
        try {
            List<Property> properties = propertyService.getUserProperties(email);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching properties: " + e.getMessage());
        }
    }
}
