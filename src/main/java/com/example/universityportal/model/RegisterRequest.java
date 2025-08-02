package com.example.universityportal.model;

import lombok.*;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String role;

    private String fullName;
    private String groupNumber;
    private int course;
}
