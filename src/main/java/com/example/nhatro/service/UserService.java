package com.example.nhatro.service;

import java.util.List;

import com.example.nhatro.entity.User;
import com.example.nhatro.enums.UserRole;

public interface UserService {
    
    List<User> getAllUsers();
    
    User getUserById(Long id);
    
    User updateUserRole(Long userId, UserRole role);
    
    User getCurrentUser(String email);
    
    void deleteUser(Long userId);
}
