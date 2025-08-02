package com.example.universityportal.model;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long id;
    private String fullName;
    private String groupNumber;
    private int course;
    private String email;
    private String password;
    private String photoPath;
    private Long userId;
}
