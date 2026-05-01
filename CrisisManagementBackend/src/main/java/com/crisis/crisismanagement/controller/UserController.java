package com.crisis.crisismanagement.controller;

import com.crisis.crisismanagement.model.User;
import com.crisis.crisismanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByIdentityNumber(user.getIdentityNumber()) != null) {
            return ResponseEntity.status(400).body("Bu TC kimlik numarası zaten kayıtlı");
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        User user = userRepository.findByIdentityNumberAndUserPassword(
                loginRequest.getIdentityNumber(),
                loginRequest.getUserPassword()
        );
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("Hatalı TC numarası veya şifre");
    }
}