package com.example.hello_sring_boot.controller;

import com.example.hello_sring_boot.dto.request.CreateRoleRequest;
import com.example.hello_sring_boot.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<Void> createRole(@Valid @RequestBody CreateRoleRequest createRoleRequest) throws BadRequestException {
        roleService.createRole(createRoleRequest);
        return ResponseEntity.ok().build();
    }
}
