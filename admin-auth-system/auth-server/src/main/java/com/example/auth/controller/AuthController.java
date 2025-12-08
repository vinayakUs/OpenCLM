package com.example.auth.controller;

import com.example.auth.entity.ERole;
import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.payload.request.SignupRequest;
import com.example.auth.payload.response.MessageResponse;
import com.example.auth.repository.RoleRepository;
import com.example.auth.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
        @Autowired
        UserRepository userRepository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        PasswordEncoder encoder;

        @PostMapping("/signup")
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(new MessageResponse("Error: Email is already in use!"));
                }

                // Create new user's account
                User user = new User(signUpRequest.getName(),
                                signUpRequest.getEmail(),
                                encoder.encode(signUpRequest.getPassword()));

                Set<Role> roles = new HashSet<>();

                // Always assign ADMIN role
                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(adminRole);

                user.setRoles(roles);
                userRepository.save(user);

                return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        }
}
