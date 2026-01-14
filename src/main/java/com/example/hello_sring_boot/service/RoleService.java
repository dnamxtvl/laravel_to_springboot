package com.example.hello_sring_boot.service;

import com.example.hello_sring_boot.dto.request.CreateRoleRequest;
import com.example.hello_sring_boot.entity.Role;
import com.example.hello_sring_boot.entity.RolePermission;
import com.example.hello_sring_boot.repository.PermissionRepository;
import com.example.hello_sring_boot.repository.RolePermissionRepository;
import com.example.hello_sring_boot.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Transactional
    public void createRole(CreateRoleRequest createRoleRequest) throws BadRequestException {
        List<Integer> permissionIds = createRoleRequest.getPermissionIds();
        Integer count = permissionRepository.countByIdIn(permissionIds);
        if (count != permissionIds.size()) throw new BadRequestException("permission.id.invalid");

        Optional<Role> exitsRole = roleRepository.findByName(createRoleRequest.getName().toUpperCase());
        if (exitsRole.isPresent()) throw new BadRequestException("role.name.unique");

        Role role = Role.builder().name(createRoleRequest.getName().toUpperCase()).build();
        Role newRole = roleRepository.save(role);

        List<RolePermission> rolePermissions = permissionIds.stream().map(permissionId ->
                RolePermission.builder().roleId(newRole.getId()).permissionId(permissionId).build()).toList();

        rolePermissionRepository.saveAll(rolePermissions);
    }
}
