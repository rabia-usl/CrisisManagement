package com.crisis.crisismanagement.controller;

import java.util.HashMap;
import java.util.Map;
import com.crisis.crisismanagement.model.User;
import com.crisis.crisismanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getLatitude() != null && user.getLongitude() != null) {
            GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
            Point point = factory.createPoint(
                    new Coordinate(user.getLongitude(), user.getLatitude())
            );
            user.setUserLocation(point);
        }
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        User user = userRepository.findByIdentityNumberAndUserPassword(
                loginRequest.getIdentityNumber(),
                loginRequest.getUserPassword()
        );
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("userName", user.getUserName());
            response.put("phoneNumber", user.getPhoneNumber());
            response.put("userRole", user.getUserRole());
            response.put("identityNumber", user.getIdentityNumber());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Hatalı TC numarası veya şifre");
    }
}