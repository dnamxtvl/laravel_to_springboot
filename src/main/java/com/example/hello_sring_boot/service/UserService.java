package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.dto.request.CreateUserRequest;
import com.example.hello_sring_boot.dto.request.UpdateUserRequest;
import com.example.hello_sring_boot.dto.response.UserResponse;
import com.example.hello_sring_boot.dto.response.UserWithPermsResponse;
import com.example.hello_sring_boot.dto.user.DetailTodos;
import com.example.hello_sring_boot.entity.Permission;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.mapper.UserMapper;
import com.example.hello_sring_boot.repository.UserRefreshTokenRepository;
import com.example.hello_sring_boot.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.hello_sring_boot.enums.UserType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

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
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    // Get all with pagination
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    // Update
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    // Delete (soft delete)
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Hard delete
    public void hardDeleteUser(String id) {
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
    public void updatePassword(String id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Update email verification
    public void verifyEmail(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Update login info
    public void updateLoginInfo(String id, String ipAddress) {
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
    public void restoreUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setDeletedAt(null);
        userRepository.save(user);
    }

    public Page<UserResponse> searchUserPagination(String firstName, String lastName, String email, Byte typeUser,
            Byte status,
            Pageable pageable) {
        Page<User> users = userRepository.searchUserPagination(firstName, lastName, email, typeUser, status, pageable);

        return users.map(userMapper::toResponse);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<SimpleGrantedAuthority> authorities = Arrays
                .asList(new SimpleGrantedAuthority("ROLE_" + UserType.fromValue(user.getTypeUser()).name()));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    public UserWithPermsResponse findWithRolesAndPermissionsById(String id) {
        User user = userRepository.findWithRolesAndPermissionsById(id).
                orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        List<String> permissions = Optional.ofNullable(user.getRole())
                .map(role -> role.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        return UserWithPermsResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .statusActive(user.getStatusActive())
                .name(user.getFirstName() + " " + user.getLastName())
                .permissions(permissions).build();
    }

    @Transactional
    public void disableUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user.not_found"));

        user.setStatusActive((byte) 0);
        userRepository.save(user);

        userRefreshTokenRepository.deleteByUserId(userId);
    }

    public DetailTodos getTodo() {
        WebClient client = WebClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();

        return client.get()
                .uri("/todos/1")
                .retrieve()
                .bodyToMono(DetailTodos.class).block();
    }

    @Transactional
    public int revokeExpiredRefreshToken() {
        log.error(LocalDateTime.now().toString());
        return userRefreshTokenRepository.deleteByExpiredAtBefore(LocalDateTime.now());
    }
}