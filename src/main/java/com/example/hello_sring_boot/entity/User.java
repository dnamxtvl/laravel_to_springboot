package com.example.hello_sring_boot.entity;

import jakarta.persistence.Index;
import lombok.*;
import org.hibernate.annotations.*;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_phone_number", columnList = "phone_number"),
        @Index(name = "idx_name", columnList = "first_name, last_name"),
        @Index(name = "idx_cities", columnList = "from_city_id, current_city_id"),
        @Index(name = "idx_status", columnList = "status, status_active"),
        @Index(name = "idx_dob", columnList = "day_of_birth, month_of_birth, year_of_birth")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private String id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "phone_verified_at", length = 20)
    private String phoneVerifiedAt;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "background_profile", length = 255)
    private String backgroundProfile;

    @Column(name = "from_city_id")
    private Integer fromCityId;

    @Column(name = "current_city_id")
    private Integer currentCityId;

    @Column(name = "relationship_status")
    private Integer relationshipStatus;

    @Column(name = "gender", nullable = false)
    private Byte gender; // 0: nữ, 1: nam, 2: khác

    @Column(name = "day_of_birth")
    private Integer dayOfBirth;

    @Column(name = "month_of_birth")
    private Integer monthOfBirth;

    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @Column(name = "about_me", length = 255)
    private String aboutMe;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "latest_login_at")
    private LocalDateTime latestLoginAt;

    @Column(name = "latest_ip_login", length = 255)
    private String latestIpLogin;

    @Column(name = "last_activity_at", columnDefinition = "TIMESTAMP COMMENT 'Thời gian online cuối'")
    private LocalDateTime lastActivityAt;

    @Column(name = "status")
    private Byte status = 1;

    @Column(name = "status_active", columnDefinition = "TINYINT DEFAULT 0 COMMENT 'Trạng thái hoạt động'")
    private Byte statusActive = 0;

    @Column(name = "type_user", nullable = false, columnDefinition = "TINYINT COMMENT 'học sinh, phụ huynh, nhà trường, admin'")
    private Byte typeUser;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @Column(name = "refresh_token", length = 255)
    private String refreshToken;

    @Column(name = "remember_token", length = 100)
    private String rememberToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Helper methods
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public Integer getAge() {
        if (yearOfBirth == null) return null;
        int currentYear = LocalDateTime.now().getYear();
        return currentYear - yearOfBirth;
    }
}