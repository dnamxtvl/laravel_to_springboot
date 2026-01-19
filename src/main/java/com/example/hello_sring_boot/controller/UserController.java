package com.example.hello_sring_boot.controller;

import com.example.hello_sring_boot.anotation.FileValidator;
import com.example.hello_sring_boot.dto.csv.UserCsvRepresentation;
import com.example.hello_sring_boot.dto.request.CreateUserRequest;
import com.example.hello_sring_boot.dto.request.UpdateUserRequest;
import com.example.hello_sring_boot.dto.response.ApiResponse;
import com.example.hello_sring_boot.dto.response.PaginatedResponse;
import com.example.hello_sring_boot.dto.response.UserResponse;
import com.example.hello_sring_boot.dto.user.DetailTodos;
import com.example.hello_sring_boot.mapper.PaginationMapper;
import com.example.hello_sring_boot.service.CsvService;
import com.example.hello_sring_boot.service.FileStorageService;
import com.example.hello_sring_boot.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CsvService csvService;

    // Create user
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        // Test redis
        redisTemplate.opsForValue().set("test", "test redis ahihi", 60, TimeUnit.MINUTES);
        String test = (String) redisTemplate.opsForValue().get("test");
        log.error("Test: {}", test);
        UserResponse response = userService.getUserById(String.valueOf(id));
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder().data(response).build();

        return ResponseEntity.ok(apiResponse);
    }

    // Get all users with pagination
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    // Update user
    @PostMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateUserRequest request,
            @FileValidator(maxSize = 1024 * 1024, allowedTypes = { "pdf" }) @RequestParam("file") MultipartFile file) {

        String fileName = fileStorageService.save(file);
        log.error("File name: {}", fileName);

        UserResponse response = userService.updateUser(String.valueOf(id), request);
        return ResponseEntity.ok(response);
    }

    // Delete user (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(String.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    // Hard delete user
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable UUID id) {
        userService.hardDeleteUser(String.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    // Search users
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Byte typeUser,
            @RequestParam(required = false) Byte status) {

        List<UserResponse> users = userService.searchUsers(
                firstName, lastName, email, typeUser, status);
        return ResponseEntity.ok(users);
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    // Update password
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable UUID id,
            @RequestParam String newPassword) {
        userService.updatePassword(String.valueOf(id), newPassword);
        return ResponseEntity.ok().build();
    }

    // Verify email
    @PostMapping("/{id}/verify-email")
    public ResponseEntity<Void> verifyEmail(@PathVariable UUID id) {
        userService.verifyEmail(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    // Get deleted users
    @GetMapping("/deleted")
    public ResponseEntity<List<UserResponse>> getDeletedUsers() {
        List<UserResponse> users = userService.getDeletedUsers();
        return ResponseEntity.ok(users);
    }

    // Restore user
    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreUser(@PathVariable UUID id) {
        userService.restoreUser(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    // Update login info
    @PostMapping("/{id}/login-info")
    public ResponseEntity<Void> updateLoginInfo(
            @PathVariable UUID id,
            @RequestParam String ipAddress) {
        userService.updateLoginInfo(String.valueOf(id), ipAddress);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pagination")
    public ResponseEntity<PaginatedResponse<UserResponse>> searchUserPagination(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Byte typeUser,
            @RequestParam(required = false) Byte status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserResponse> userPage = userService.searchUserPagination(
                firstName, lastName, email, typeUser, status, pageable);
        PaginatedResponse<UserResponse> response = PaginationMapper.toPaginatedResponse(userPage);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping("/with-roles-and-permissions/{id}")
    public ResponseEntity<Void> findWithRolesAndPermissionsByEmail(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('USER_DELETE')")
    @PostMapping("/disable/{id}")
    public ResponseEntity<Void> findBlacklistTokenByEmail(@PathVariable UUID id) {
        userService.disableUser(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/call-todo")
    public ResponseEntity<ApiResponse<DetailTodos>> callTodo() {
        DetailTodos detailTodos = userService.getTodo();
        ApiResponse<DetailTodos> response = ApiResponse.<DetailTodos>builder().data(detailTodos).build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<List<UserCsvRepresentation>> uploadCsvFile(
        @FileValidator(maxSize = 1024 * 1024, allowedTypes = { "csv" }) @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<UserCsvRepresentation> users = csvService.parseCsv(file);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}