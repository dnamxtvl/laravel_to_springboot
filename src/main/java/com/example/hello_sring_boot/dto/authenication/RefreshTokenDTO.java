package com.example.hello_sring_boot.dto.authenication;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RefreshTokenDTO {
    private String token;
    private String userId;
    private LocalDateTime expiredAt;
}
