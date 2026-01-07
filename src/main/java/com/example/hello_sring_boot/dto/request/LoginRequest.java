package com.example.hello_sring_boot.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotEmpty(message = "user.validation.email_required")
    @Size(min = 5, max = 100, message = "user.validation.email_length")
    @Email(message = "user.validation.email_invalid")
    private String email;

    @NotEmpty(message = "user.validation.password_required")
    @Size(min = 5, max = 30, message = "user.validation.password_length")
    private String password;
}
