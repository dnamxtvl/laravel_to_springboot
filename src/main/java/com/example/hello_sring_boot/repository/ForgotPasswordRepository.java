package com.example.hello_sring_boot.repository;

import com.example.hello_sring_boot.entity.ForgotPassword;
import com.example.hello_sring_boot.entity.User;
import com.example.hello_sring_boot.enums.OtpStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, String>, JpaSpecificationExecutor<ForgotPassword> {
    Optional<User> findFirstByUserIdAndOtpAndStatusOrderByCreatedAtDesc(
            String userId,
            String otp,
            Integer status
    );

    @Modifying
    @Query("UPDATE ForgotPassword f SET f.status = :expiredStatus " +
            "WHERE f.userId = :userId AND f.status = :pendingStatus")
    void invalidateOldOtps(
            @Param("userId") String userId,
            @Param("pendingStatus") OtpStatus pendingStatus,
            @Param("expiredStatus") OtpStatus expiredStatus
    );
}