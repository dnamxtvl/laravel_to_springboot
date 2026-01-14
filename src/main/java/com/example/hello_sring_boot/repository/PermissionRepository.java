package com.example.hello_sring_boot.repository;

import com.example.hello_sring_boot.entity.Permission;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    @Query("SELECT COUNT(p) FROM Permission p WHERE p.id IN :permissionIds")
    Integer countByIdIn(@Param("permissionIds") List<Integer> permissionIds);
}
