package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.dto.request.CreateUserRequest;
import com.example.hello_sring_boot.dto.request.UpdateUserRequest;
import com.example.hello_sring_boot.dto.response.UserResponse;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.mapper.UserMapper;
import com.example.hello_sring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // Create
    public UserResponse createUser(CreateUserRequest request) {
        // Validate unique constraints
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    // Get by ID
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    // Get all with pagination
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    // Update
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    // Delete (soft delete)
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Hard delete
    public void hardDeleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Search users
    public List<UserResponse> searchUsers(String firstName, String lastName, String email, Byte typeUser, Byte status) {
        List<User> users = userRepository.searchUsers(firstName, lastName, email, typeUser, status);
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Find by email
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    // Update password
    public void updatePassword(UUID id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Update email verification
    public void verifyEmail(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Update login info
    public void updateLoginInfo(UUID id, String ipAddress) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setLatestLoginAt(LocalDateTime.now());
        user.setLatestIpLogin(ipAddress);
        user.setLastActivityAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // Get deleted users
    public List<UserResponse> getDeletedUsers() {
        List<User> deletedUsers = userRepository.findAllDeleted();
        return deletedUsers.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Restore user
    public void restoreUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setDeletedAt(null);
        userRepository.save(user);
    }
}