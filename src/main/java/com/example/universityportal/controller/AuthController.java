package com.example.universityportal.controller;

import com.example.universityportal.model.*;
import com.example.universityportal.repository.UserJdbcRepository;
import com.example.universityportal.security.JwtUtil;
import com.example.universityportal.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserJdbcRepository userRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ADMIN");

        userRepository.save(user);
        return ResponseEntity.ok("Admin registration successful");
    }


    @PostMapping(value = "/register-student", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerStudent(
            @RequestPart("data") MultipartFile data,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        RegisterRequest request;
        try {
            request = objectMapper.readValue(data.getInputStream(), RegisterRequest.class);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Invalid registration data.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("STUDENT");

        userRepository.save(user);

        Student student = new Student();
        student.setFullName(request.getFullName());
        student.setGroupNumber(request.getGroupNumber());
        student.setCourse(request.getCourse());
        student.setUserId(user.getId());

        try {
            studentService.save(student, photo);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save student photo.");
        }

        return ResponseEntity.ok("Student registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());

        if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            studentService.findByUserId(user.getId()).ifPresent(student -> response.put("studentId", student.getId()));
        }

        return ResponseEntity.ok(response);
    }
}
