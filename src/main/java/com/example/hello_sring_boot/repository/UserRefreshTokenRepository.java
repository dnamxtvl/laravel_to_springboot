package com.example.hello_sring_boot.repository;

import com.example.hello_sring_boot.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long>, JpaSpecificationExecutor<UserRefreshToken> {
    Optional<UserRefreshToken> findByToken(String token);

    Optional<UserRefreshToken> findByUserId(String userId);

    void deleteByToken(String token);

    void deleteByUserId(String userId);
}
