package com.example.universityportal.repository;

import com.example.universityportal.model.Student;
import com.example.universityportal.model.User;

import java.util.List;
import java.util.Optional;

public interface StudentJdbcRepository {
    void save(Student student);
    Optional<Student> findById(Long id);
    List<Student> findAll();
    Optional<Student> findByUserId(Long userId);
    void deleteById(Long id);
    boolean existsById(Long id);
    void updateStudent(Student student);
// new
}
