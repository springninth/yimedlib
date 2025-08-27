package com.example.ymedlib.service;

import com.example.ymedlib.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    void deleteUser(Long id);
    User updateUser(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
} 