package com.example.hello_sring_boot.controller;

import com.example.hello_sring_boot.dto.request.CreateRoleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @PostMapping("/create")
    public ResponseEntity<Void> createRole(@RequestBody CreateRoleRequest createRoleRequest) {
        return ResponseEntity.ok().build();
    }
}
