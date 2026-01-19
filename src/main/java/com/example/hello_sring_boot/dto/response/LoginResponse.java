package com.example.hello_sring_boot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String refreshToken;
}
