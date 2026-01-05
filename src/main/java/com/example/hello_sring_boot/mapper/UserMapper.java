package com.example.hello_sring_boot.mapper;

import com.example.hello_sring_boot.dto.request.CreateUserRequest;
import com.example.hello_sring_boot.dto.request.UpdateUserRequest;
import com.example.hello_sring_boot.dto.response.UserResponse;
import com.example.hello_sring_boot.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    // Entity từ Request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @Mapping(target = "phoneVerifiedAt", ignore = true)
    @Mapping(target = "latestLoginAt", ignore = true)
    @Mapping(target = "latestIpLogin", ignore = true)
    @Mapping(target = "lastActivityAt", ignore = true)
    @Mapping(target = "fcmToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "rememberToken", ignore = true)
    @Mapping(target = "gender", source = "gender.value")
    @Mapping(target = "typeUser", source = "typeUser.value")
    @Mapping(target = "status", constant = "1")
    @Mapping(target = "statusActive", constant = "0")
    User toEntity(CreateUserRequest request);

    // Response từ Entity
    @Mapping(target = "gender", expression = "java(com.example.hello_sring_boot.enums.Gender.fromValue(user.getGender()))")
    @Mapping(target = "typeUser", expression = "java(com.example.hello_sring_boot.enums.UserType.fromValue(user.getTypeUser()))")
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "age", expression = "java(user.getAge())")
    UserResponse toResponse(User user);

    // Update entity từ update request
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget User user, UpdateUserRequest request);
}