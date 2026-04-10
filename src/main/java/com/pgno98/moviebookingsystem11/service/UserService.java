package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.User;
import com.pgno98.moviebookingsystem11.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional
    public void deleteUserByEmail(String email) {
        System.out.println("UserService: Attempting to delete user with email: " + email);
        
        // First find the user to ensure it exists
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("UserService: Found user with ID: " + user.getId());
            
            // Delete the user (cascade will handle bookings and reviews)
            userRepository.delete(user);
            System.out.println("UserService: User deleted successfully");
        } else {
            System.out.println("UserService: User not found with email: " + email);
            throw new RuntimeException("User not found with email: " + email);
        }
    }
}
