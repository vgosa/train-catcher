package org.group21.user.service;


import jakarta.persistence.*;
import org.group21.JwtUtil;
import org.group21.user.exception.InvalidCredentialsException;
import org.group21.user.exception.UserNotFoundException;
import org.group21.user.model.*;
import org.group21.user.repository.*;
import org.group21.user.util.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        user.setPassword(PasswordUtil.hashPassword(user.getEmail(), user.getPassword()));
        return userRepository.save(user);
    }


    public List<User> createUsers(List<User> users) {
        users.forEach(user ->
                user.setPassword(PasswordUtil.hashPassword(user.getEmail(), user.getPassword()))
        );
        return userRepository.saveAll(users);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setPassword(PasswordUtil.hashPassword(user.getEmail(), updatedUser.getPassword()));
                    user.setPhone(updatedUser.getPhone());
                    user.setBalance(updatedUser.getBalance());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException("User not found during update operation", id));
    }


    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User not found during delete operation", id);
        }
    }


    public String loginUser(String email, String password) {
        String hashedPassword = PasswordUtil.hashPassword(email, password);

        User user = userRepository.findByEmailIgnoreCase(email)
                .filter(u -> u.getPassword().equals(hashedPassword))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email/password supplied"));

        return JwtUtil.generateToken(email, user.getId());
    }

    public User topUpBalance(Long id, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        user.setBalance(user.getBalance() + amount);
        return userRepository.save(user);
    }


}
