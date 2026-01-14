package com.example.hello_sring_boot.repository;

import com.example.hello_sring_boot.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer>, JpaSpecificationExecutor<RolePermission> {
}
