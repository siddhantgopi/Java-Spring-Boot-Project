package com.realestate.service;

import com.realestate.model.User;
import org.springframework.transaction.annotation.Transactional;
import com.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User registerUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password); // Storing plain text (NOT recommended)
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("User found: " + user.getEmail() + " | Stored password: " + user.getPassword());
            
            // Check password manually since we're using plain text
            return user.getPassword().equals(password);
        }

        System.out.println("User not found");
        return false;
    }
}

