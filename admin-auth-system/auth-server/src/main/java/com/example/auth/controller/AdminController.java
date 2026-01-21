package com.example.auth.controller;

import com.example.auth.dto.request.UserRegisterRequest;
import com.example.auth.dto.response.MessageResponse;
import com.example.auth.entity.ERole;
import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.repository.RoleRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/registerUser")
    public ResponseEntity<?>  registerUser(@RequestBody UserRegisterRequest registerRequest){
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(registerRequest.getName(),
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword()));

        Set<Role> roles = new HashSet<>();

        // Always assign ADMIN role
        Role adminRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(adminRole);
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }

}
