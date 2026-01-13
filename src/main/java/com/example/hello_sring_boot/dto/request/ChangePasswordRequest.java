package com.example.hello_sring_boot.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    @NotEmpty(message = "user.validation.password_required")
    @Size(min = 5, max = 30, message = "user.validation.password_length")
    private String password;
}
