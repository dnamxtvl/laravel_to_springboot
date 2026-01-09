package com.example.hello_sring_boot.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ForgotPasswordRequest {
    @NotBlank(message = "forgotPasswordRequest.email.required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "forgotPasswordRequest.email.max")
    private String email;
}