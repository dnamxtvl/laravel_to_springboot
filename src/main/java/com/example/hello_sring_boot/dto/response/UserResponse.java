package com.example.hello_sring_boot.dto.response;

import com.example.hello_sring_boot.enums.Gender;
import com.example.hello_sring_boot.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime emailVerifiedAt;
    private String phoneNumber;
    private String phoneVerifiedAt;
    private String avatar;
    private String backgroundProfile;
    private Integer fromCityId;
    private Integer currentCityId;
    private Integer relationshipStatus;
    private Gender gender;
    private Integer dayOfBirth;
    private Integer monthOfBirth;
    private Integer yearOfBirth;
    private String aboutMe;
    private LocalDateTime latestLoginAt;
    private String latestIpLogin;
    private LocalDateTime lastActivityAt;
    private Byte status;
    private Byte statusActive;
    private UserType typeUser;
    private String fcmToken;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private String fullName;
    private Integer age;
}