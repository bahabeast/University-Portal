package com.example.universityportal.repository;

import com.example.universityportal.model.User;

import java.util.Optional;

public interface UserJdbcRepository {
    void save(User user);
    Optional<User> findByEmail(String email);
    void deleteById(Long id);

}
